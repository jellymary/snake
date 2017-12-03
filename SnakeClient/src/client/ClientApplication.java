package client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
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
import java.util.Objects;

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
            Node hat = Shape.subtract(new Circle(size / 2, Color.BROWN), new Rectangle(- size / 2, 0, size, size));
            group.getChildren().add(hat);
            group.getChildren().add(new Rectangle(-size / 4, 0 , size / 2, size / 2));
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
    }

    private void connectAndPlayGameThroughSocket(Socket socket) throws GameUnavailableException, IOException {
        GameConnection gameConnection = new GameConnection(socket);
        gameConnection.sendRequestToPlay();

        primaryStage.setScene(gameSceneHolder.getScene());

        while (true) {
            final GameMessage serverMessage;
            try {
                serverMessage = gameConnection.getNextMessage();
            } catch (IllegalGameMessageFormatException e) {
                Platform.runLater(() -> showGameEndSceneWithMessage("Connection format error"));
                return;
            }

            switch (serverMessage.type) {
                case GameIsReady:
                    Platform.runLater(() -> {
                        try {
                            arrangeGameScene(serverMessage);
                        } catch (IllegalGameMessageFormatException e) {
                            showGameEndSceneWithMessage("Game crushed");
                        }
                    });
                    break;
                case GameStarted:
                    Platform.runLater(() -> activateControls(gameConnection));
                    break;
                case GameState:
                    //TODO
                    break;
                case GameFinished:
                    finishGame(serverMessage);
                    return;
            }
        }
    }

    private void finishGame(GameMessage message) {
        String[] lines = message.raw.split("\n");
        String printedMessage = "Default message";
        try {
            if (Objects.equals(lines[1], "WIN"))
                printedMessage = "Win!";
            else if (Objects.equals(lines[1], "LOSE"))
                printedMessage = "Game over!";
        } catch (IndexOutOfBoundsException ignored){}
        final String res = printedMessage;
        Platform.runLater(() -> showGameEndSceneWithMessage(res));
    }

    private void activateControls(final GameConnection gameConnection) {
        gameSceneHolder.getScene().setOnKeyPressed(event -> {
            if (event.getCode().isArrowKey())
                try {
                    gameConnection.sendPlayersAction(event.getCode());
                } catch (IOException e) {
                    try {
                        gameConnection.getSocket().close();
                    } catch (IOException ec) {
                    }
                    finally {
                        showGameEndSceneWithMessage("Connection lost");
                    }
                }
            if (event.getCode() == KeyCode.ESCAPE)
                try {
                    gameConnection.getSocket().close();
                } catch (IOException e) {
                }
                finally {
                    showGameEndSceneWithMessage("You quit");
                }
        });
    }

    private void showGameEndSceneWithMessage(String message) {
        gameEndText.setText(message);
        primaryStage.setScene(gameEndScene);
    }

    private void arrangeGameScene(GameMessage message) throws IllegalGameMessageFormatException {
        String[] lines = message.raw.split("\n");
        int fieldWidth, fieldHeight;
        try {
            fieldWidth = Integer.parseInt(lines[1]);
            fieldHeight = Integer.parseInt(lines[2]);
        } catch (NumberFormatException e)
        {
            throw new IllegalGameMessageFormatException("Field width or height not recognized");
        }
        catch (IndexOutOfBoundsException e)
        {
            throw new IllegalGameMessageFormatException("Unexpected end of message");
        }

        gameSceneHolder.resize(fieldWidth, fieldHeight);
        gameSceneHolder.clear();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
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

    private void switchToGameScene() {
        //TODO
        primaryStage.setScene(gameSceneHolder.getScene());
    }
}
