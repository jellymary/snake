package client;

import FieldObjects.*;
import client.Exceptions.IllegalGameMessageFormatException;
import client.Utils.Size;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ClientApplication extends Application {
    private static final double WINDOW_HEIGHT = 480;
    private static final double WINDOW_WIDTH = 640;

    private Stage primaryStage;
    private SceneHolder connectionSceneHolder;
    private GameSceneHolder gameSceneHolder;
    private Scene gameEndScene;
    private Text gameEndText = new Text();
    private final FieldDeserializer deserializer = new FieldDeserializer();
    private final Map<Type, Function<FieldObject, Node>> visualizations = new HashMap<>();

    @Override
    public void init() {
        connectionSceneHolder = new ConnectionSceneHolder(this::connectAndPlayGameThroughSocket);
        gameSceneHolder = new GameSceneHolder(WINDOW_WIDTH, WINDOW_HEIGHT);
        initGameEndScene();

        setUpVisualizations();
    }

    private void setUpVisualizations() {
        visualizations.put(Apple.class, (object) -> makeCircleObject(gameSceneHolder.getCellSize(), Color.RED));
        visualizations.put(Gum.class, (object) -> makeCircleObject(gameSceneHolder.getCellSize(), Color.PINK));
        visualizations.put(Mushroom.class, (object) -> {
            Group group = new Group();
            double size = gameSceneHolder.getCellSize();
            Node hat = Shape.subtract(new Circle(size / 2, Color.BROWN), new Rectangle(-size / 2, 0, size, size));
            group.getChildren().add(hat);
            group.getChildren().add(new Rectangle(-size / 4, 0, size / 2, size / 2));
            group.setLayoutX(size / 2);
            group.setLayoutY(size / 2);
            return group;
        });
        visualizations.put(Portal.class, (object) -> makeCircleObject(gameSceneHolder.getCellSize(), Color.BLUE));
        visualizations.put(Head.class, (object) -> makeCircleObject(gameSceneHolder.getCellSize(), Color.LIGHTGREEN));
        visualizations.put(Body.class, (object) -> makeCircleObject(gameSceneHolder.getCellSize(), Color.GREEN));
        visualizations.put(Wall.class, (object) -> new Rectangle(gameSceneHolder.getCellSize(), gameSceneHolder.getCellSize(), Color.GRAY));
        visualizations.put(Oracle.class, (object) -> new Rectangle(gameSceneHolder.getCellSize(), gameSceneHolder.getCellSize(), Color.BLUE));
    }

    private Circle makeCircleObject(Double size, Paint fill) {
        Circle node = new Circle(size / 2, fill);
        node.setLayoutX(size / 2);
        node.setLayoutY(size / 2);
        return node;
    }

    private void initGameEndScene() {
        GridPane root = new GridPane();
        root.setAlignment(Pos.CENTER);
        root.add(gameEndText, 0, 0);
        Button toConnectionSceneButton = new Button("Back to main menu");
        root.add(toConnectionSceneButton, 0, 1);
        toConnectionSceneButton.setOnAction(this::switchToConnectionScene);
        gameEndScene = new Scene(root);
    }

    private void switchToConnectionScene(ActionEvent actionEvent) {
        primaryStage.setScene(connectionSceneHolder.getScene());
        actionEvent.consume();
    }

    private void connectAndPlayGameThroughSocket(Socket socket, String name, int desiredPlayersNUmber) {
        try {
            GameConnection gameConnection = new GameConnection(socket);
            gameConnection.sendMessage(GameMessage.makeRequestMessage(name, desiredPlayersNUmber));

            Platform.runLater(() -> primaryStage.setScene(gameSceneHolder.getScene()));

            AtomicReference<String> endMessage = new AtomicReference<>();
            AtomicBoolean shouldRun = new AtomicBoolean(true);
            while (shouldRun.get()) {
                final GameMessage serverMessage;
                try {
                    serverMessage = gameConnection.receiveMessage();
                } catch (IllegalGameMessageFormatException e) {
                    endGameFromThread(endMessage, shouldRun, "Connection format error");
                    break;
                }


                switch (serverMessage.messageType) {
                    case GameIsReady:
                        prepareGameFromThread(gameConnection, endMessage, shouldRun, serverMessage);
                        break;
                    case GameStarted:
                        startGameFromThread(gameConnection, endMessage, shouldRun);
                        break;
                    case GameState:
                        drawFieldFromMessageFromThread(serverMessage);
                        break;
                    case GameFinished:
                        endGameFromThread(endMessage, shouldRun, GameMessage.parseFinishResult(serverMessage));
                        break;
                }
            }
            showGameEndSceneWithMessageAsync(endMessage.get());
        } catch (Exception e) {
            //todo log
            showGameEndSceneWithMessageAsync("Unknown error");
        }
    }

    private void prepareGameFromThread(GameConnection gameConnection, AtomicReference<String> endMessage, AtomicBoolean shouldRun, GameMessage serverMessage) {
        Size fieldSize = GameMessage.parseFieldSize(serverMessage);
        Platform.runLater(() -> arrangeGameScene(fieldSize));
        try {
            gameConnection.sendMessage(GameMessage.makeClientIsReadyMessage());
        } catch (IOException e) {
            endGameFromThread(endMessage, shouldRun, "Network error");
        }
    }

    private void startGameFromThread(GameConnection gameConnection, AtomicReference<String> endMessage, AtomicBoolean shouldRun) {
        Platform.runLater(() -> {
            gameSceneHolder.setOnControlsKeyPressed((keyEvent) -> {
                try {
                    gameConnection.sendMessage(GameMessage.makePlayersActionMessage(keyEvent.getCode()));
                } catch (IOException e) {
                    endGameFromThread(endMessage, shouldRun, "Connection lost");
                }
            });
            gameSceneHolder.setControlsActive(true);
        });
    }

    private void endGameFromThread(AtomicReference<String> endMessage, AtomicBoolean shouldRun, String s) {
        endMessage.set(s);
        shouldRun.set(false);
    }

    private void drawFieldFromMessageFromThread(GameMessage serverMessage) {
        List<FieldObject> fieldObjects = deserializer.parseObjects(serverMessage.content);
        List<Node> nodes = fieldObjects.stream()
                .map(fieldObject -> {
                    if (visualizations.containsKey(fieldObject.getClass()))
                        return visualizations.get(fieldObject.getClass()).apply(fieldObject);
                    return getDefaultVisualization();
                })
                .collect(Collectors.toList());
        Platform.runLater(() -> gameSceneHolder.DrawField(nodes));
    }

    private Node getDefaultVisualization() {
        return new Rectangle(gameSceneHolder.getCellSize(), gameSceneHolder.getCellSize(), Color.BLACK);
    }

    private void showGameEndSceneWithMessageAsync(String message) {
        Platform.runLater(() -> showGameEndSceneWithMessage(message));
    }

    private void showGameEndSceneWithMessage(String message) {
        gameEndText.setText(message);
        primaryStage.setScene(gameEndScene);
    }

    private void arrangeGameScene(Size size) {
        gameSceneHolder.clear();
        gameSceneHolder.resize(size.width, size.height);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setScene(connectionSceneHolder.getScene());
        primaryStage.resizableProperty().setValue(false);
        setPrimaryStageSize();

        primaryStage.show();
    }

    private void setPrimaryStageSize() {
        primaryStage.setWidth(WINDOW_WIDTH
                + primaryStage.getScene().getWindow().getWidth()
                - primaryStage.getScene().getWidth());
        primaryStage.setHeight(WINDOW_HEIGHT
                + primaryStage.getScene().getWindow().getHeight()
                - primaryStage.getScene().getHeight());
    }
}
