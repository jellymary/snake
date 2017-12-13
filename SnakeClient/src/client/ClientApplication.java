package client;

import FieldObjects.*;
import Units.Unit;
import client.Utils.Direction;
import client.Utils.GameResult;
import client.Utils.Size;
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
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.*;
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

        deserializer.registerDefaultObjects();
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

    private void connectAndPlayGameThroughSocket(Socket socket, String name, int desiredPlayersNumber, boolean isBot) {
        SocketGameConnection gameConnection;
        try {
            gameConnection = new SocketGameConnection(socket);
        } catch (IOException e) {
            Platform.runLater(() -> {
                gameEndText.setText("Can't establish connection");
                gameEndText.setFill(Color.RED);
                primaryStage.setScene(gameEndScene);
            });
            return;
        }

        Map<KeyCode, Direction> keyCodeDirectionMap = new HashMap<>();
        keyCodeDirectionMap.put(KeyCode.UP, Direction.Up);
        keyCodeDirectionMap.put(KeyCode.DOWN, Direction.Down);
        keyCodeDirectionMap.put(KeyCode.LEFT, Direction.Left);
        keyCodeDirectionMap.put(KeyCode.RIGHT, Direction.Right);

        Map<GameResult, String> gameResultStringMap = new HashMap<>();
        gameResultStringMap.put(GameResult.LOSS, "You suck!");
        gameResultStringMap.put(GameResult.WIN, "You suck anyway!");
        gameResultStringMap.put(GameResult.TIE, "Your opponents suck too!");

        final Size[] fieldSize = new Size[1];
        Unit player = new Unit(name, gameConnection) {
            @Override
            protected void prepareForGame(GameMessage gameMessage) {
                fieldSize[0] = GameMessage.parseFieldSize(gameMessage);
                Platform.runLater(() -> {
                    primaryStage.setScene(gameSceneHolder.getScene());
                    arrangeGameScene(fieldSize[0]);
                });
            }

            @Override
            protected void onGameStarted(GameMessage gameMessage) {
                if (!isBot)
                    Platform.runLater(() -> {
                        gameSceneHolder.setOnControlsKeyPressed((keyEvent) -> {
                            try {
                                Direction desiredDirection = keyCodeDirectionMap.get(keyEvent.getCode());
                                if (desiredDirection != null)
                                    this.changeDirection(desiredDirection);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                        gameSceneHolder.setControlsActive(true);
                    });
            }

            @Override
            protected void handleState(GameMessage gameMessage) {
                List<FieldObject> fieldObjects = deserializer.parseObjects(gameMessage.content);
                List<Node> nodes = fieldObjects.stream()
                        .map(fieldObject -> {
                            Node node;
                            if (visualizations.containsKey(fieldObject.getClass()))
                                node = visualizations.get(fieldObject.getClass()).apply(fieldObject);
                            else
                                node = getDefaultVisualization();
                            double cellSize = gameSceneHolder.getCellSize();
                            Location location = fieldObject.getLocation();
                            node.setTranslateX(cellSize * location.x);
                            node.setTranslateY(cellSize * location.y);
                            return node;
                        })
                        .collect(Collectors.toList());
                Platform.runLater(() -> gameSceneHolder.DrawField(nodes));
                if (isBot) {
                    try {
                        changeDirection(botDecide(fieldObjects, fieldSize[0]));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            protected void onGameFinished(GameMessage gameMessage) {
                Platform.runLater(() -> {
                    gameEndText.setText(gameResultStringMap.get(GameMessage.parseFinishResult(gameMessage)));
                    gameEndText.setFill(Color.BLACK);
                    primaryStage.setScene(gameEndScene);
                });
            }

            @Override
            protected void stopGameWithError(String errorMessage) {
                Platform.runLater(() -> {
                    gameEndText.setText(errorMessage);
                    gameEndText.setFill(Color.RED);
                    primaryStage.setScene(gameEndScene);
                });
            }
        };

        player.run(desiredPlayersNumber);
    }

    private Direction botDecide(List<FieldObject> fieldObjects, Size fieldSize) {
        Head head;
        Optional<FieldObject> possibleHead = fieldObjects.stream().filter((o) -> o instanceof Head && ((Head) o).you).findFirst();
        if (possibleHead.isPresent())
            head = (Head) possibleHead.get();
        else
            return Direction.Down;

        FieldObject[][] field = new FieldObject[fieldSize.width][fieldSize.height];
        for (FieldObject fieldObject : fieldObjects)
            field[fieldObject.getLocation().x][fieldObject.getLocation().y] = fieldObject;

        Optional<FieldObject> apple = fieldObjects.stream().filter((o) -> o instanceof Apple).findFirst();
        if (!apple.isPresent())
            return Direction.Down;//TODO better logic

        Queue<Location> queue = new LinkedList<>();
        Set<Location> used = new HashSet<>();
        queue.add(head.getLocation());
        used.add(head.getLocation());
        Map<Location, Location> parent = new HashMap<>();

        while (!queue.isEmpty()) {
            Location cur = queue.poll();
            if (cur == apple.get().getLocation())
                break;
            Collection<Location> neighbours = Arrays.stream(new Location[]{
                    new Location(cur.x - 1, cur.y),
                    new Location(cur.x, cur.y - 1),
                    new Location(cur.x + 1, cur.y),
                    new Location(cur.x, cur.y + 1)
            }).filter((loc) ->
                    loc.x >= 0 &&
                    loc.y >= 0 &&
                    loc.x < fieldSize.width &&
                    loc.y < fieldSize.height &&
                            (field[loc.x][loc.y] == null || loc.equals(apple.get().getLocation())) &&
                    !used.contains(loc)).collect(Collectors.toList());

            queue.addAll(neighbours);
            used.addAll(neighbours);
            for (Location loc : neighbours)
                parent.put(loc, cur);
        }

        if (!used.contains(apple.get().getLocation()))
            return Direction.Down;

        Location next = apple.get().getLocation();
        while (true){
            Location t = parent.get(next);
            if (t.equals(head.getLocation())) {
                break;
            }
            next = t;
        }

        Location dir = new Location(next.x - head.getLocation().x, next.y - head.getLocation().y);
        if (dir.x == 1)
            return Direction.Right;
        if (dir.x == -1)
            return Direction.Left;
        if (dir.y == 1)
            return Direction.Down;
        if (dir.y == -1)
            return Direction.Up;
        return Direction.Down; //TODO
    }

    /*private void prepareGameFromThread(SocketGameConnection gameConnection, AtomicReference<String> endMessage, AtomicBoolean shouldRun, GameMessage serverMessage) {
        Size fieldSize = GameMessage.parseFieldSize(serverMessage);
        Platform.runLater(() -> arrangeGameScene(fieldSize));
        try {
            gameConnection.sendMessage(GameMessage.makeClientIsReadyMessage());
        } catch (IOException e) {
            endGameFromThread(endMessage, shouldRun, "Network error");
        }
    }*/

    /*private void startGameFromThread(SocketGameConnection gameConnection, AtomicReference<String> endMessage, AtomicBoolean shouldRun) {
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
    }*/

    /*private void endGameFromThread(AtomicReference<String> endMessage, AtomicBoolean shouldRun, String s) {
        endMessage.set(s);
        shouldRun.set(false);
    }*/

    /*private void drawFieldFromMessageFromThread(GameMessage serverMessage) {
        List<FieldObject> fieldObjects = deserializer.parseObjects(serverMessage.content);
        List<Node> nodes = fieldObjects.stream()
                .map(fieldObject -> {
                    if (visualizations.containsKey(fieldObject.getClass()))
                        return visualizations.get(fieldObject.getClass()).apply(fieldObject);
                    return getDefaultVisualization();
                })
                .collect(Collectors.toList());
        Platform.runLater(() -> gameSceneHolder.DrawField(nodes));
    }*/

    private Node getDefaultVisualization() {
        return new Rectangle(gameSceneHolder.getCellSize(), gameSceneHolder.getCellSize(), Color.BLACK);
    }

    /*private void showGameEndSceneWithMessageAsync(String message) {
        Platform.runLater(() -> showGameEndSceneWithMessage(message));
    }*/

    /*private void showGameEndSceneWithMessage(String message) {
        gameEndText.setText(message);
        primaryStage.setScene(gameEndScene);
    }*/

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
