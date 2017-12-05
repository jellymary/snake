package client;

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
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class ClientApplication extends Application {
    private static final double WINDOW_HEIGHT = 480;
    private static final double WINDOW_WIDTH = 640;

    private Stage primaryStage;
    private SceneHolder connectionSceneHolder;
    private GameSceneHolder gameSceneHolder;
    private Scene gameEndScene;
    private Text gameEndText = new Text();
    private FieldDeserializer deserializer = new FieldDeserializer();

    @Override
    public void init() {
        connectionSceneHolder = new ConnectionSceneHolder(this::connectAndPlayGameThroughSocket);
        gameSceneHolder = new GameSceneHolder(WINDOW_WIDTH, WINDOW_HEIGHT);
        initGameEndScene();

        setUpDeserializer();
    }

    private void setUpDeserializer() {
        deserializer.registerVisualization("Apple", (size) -> makeCircleObject(size, Color.RED));
        deserializer.registerVisualization("Gum", (size) -> makeCircleObject(size, Color.PINK));
        deserializer.registerVisualization("Mushroom", (size) -> {
            Group group = new Group();
            Node hat = Shape.subtract(new Circle(size / 2, Color.BROWN), new Rectangle(-size / 2, 0, size, size));
            group.getChildren().add(hat);
            group.getChildren().add(new Rectangle(-size / 4, 0, size / 2, size / 2));
            group.setLayoutX(size / 2);
            group.setLayoutY(size / 2);
            return group;
        });
        deserializer.registerVisualization("Portal", (size) -> makeCircleObject(size, Color.BLUE));
        deserializer.registerVisualization("Head", (size) -> makeCircleObject(size, Color.LIGHTGREEN));
        deserializer.registerVisualization("Body", (size) -> makeCircleObject(size, Color.GREEN));
        deserializer.registerVisualization("Wall", (size) -> new Rectangle(size, size, Color.GRAY));
        deserializer.registerVisualization("Oracle", (size) -> new Rectangle(size, size, Color.BLUE));
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

    private void connectAndPlayGameThroughSocket(Socket socket, String name, int desiredPlayersNUmber) throws IOException {
        try {
            GameConnection gameConnection = new GameConnection(socket);
            gameConnection.sendMessage(GameMessage.makeRequestMessage(name, desiredPlayersNUmber));

            Platform.runLater(() -> primaryStage.setScene(gameSceneHolder.getScene()));

            FieldDeserializer deserializer = new FieldDeserializer();

            AtomicReference<String> endMessage = new AtomicReference<>();
            AtomicBoolean shouldRun = new AtomicBoolean(true);
            while (shouldRun.get()) {
                final GameMessage serverMessage;
                try {
                    serverMessage = gameConnection.receiveMessage();
                } catch (IllegalGameMessageFormatException e) {
                    endMessage.set("Connection format error");
                    shouldRun.set(false);
                    break;
                }


                switch (serverMessage.messageType) {
                    case GameIsReady:
                        Size fieldSize = GameMessage.parseFieldSize(serverMessage);
                        Platform.runLater(() -> {
                            arrangeGameScene(fieldSize);
                        });
                        try {
                            gameConnection.sendMessage(GameMessage.makeClientIsReadyMessage());
                        } catch (IOException e) {
                            endMessage.set("Network error");
                            shouldRun.set(false);
                        }
                        break;
                    case GameStarted:
                        Platform.runLater(() -> {
                            gameSceneHolder.setOnControlsKeyPressed((keyEvent) -> {
                                try {
                                    gameConnection.sendMessage(GameMessage.makePlayersActionMessage(keyEvent.getCode()));
                                } catch (IOException e) {
                                    endMessage.set("Connection lost");
                                    shouldRun.set(false);
                                }
                            });
                            gameSceneHolder.setControlsActive(true);
                        });
                        break;
                    case GameState:
                        Node[] nodes = deserializer.parseNodes(serverMessage.content, gameSceneHolder.getCellSize());
                        Platform.runLater(() -> gameSceneHolder.DrawField(nodes));
                        break;
                    case GameFinished:
                        endMessage.set(GameMessage.parseFinishResult(serverMessage));
                        shouldRun.set(false);
                        break;
                }
            }
            showGameEndSceneWithMessageAsync(endMessage.get());
        } catch (Exception e) {
            e.printStackTrace();//todo log
            showGameEndSceneWithMessageAsync("Unknown error");
        }
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
