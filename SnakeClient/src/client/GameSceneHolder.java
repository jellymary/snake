package client;

import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;

import java.util.List;

public class GameSceneHolder implements SceneHolder {
    private final Scene scene;
    private final Group fieldObjects = new Group();
    private final SubScene gameArea;
    private int columns, rows;
    private final double WINDOW_WIDTH;
    private final double WINDOW_HEIGHT;
    private double cellSize;
    private EventHandler<KeyEvent> controlsHandler;

    GameSceneHolder(double windowWidth, double windowHeight) {
        WINDOW_HEIGHT = windowHeight;
        WINDOW_WIDTH = windowWidth;

        gameArea = new SubScene(fieldObjects, 1, 1);
        gameArea.setFill(Color.WHITE);
        gameArea.setLayoutX(WINDOW_WIDTH / 2);
        gameArea.setLayoutY(WINDOW_HEIGHT / 2);
        scene = new Scene(new Group(gameArea));
        scene.setFill(Color.BLUE);

        gameArea.setOnKeyPressed(this::handleKeyPressed);
    }

    @Override
    public Scene getScene() {
        return scene;
    }

    public void resize(int columns, int rows) {
        this.columns = columns;
        this.rows = rows;

        double height, width;
        if (rows * WINDOW_WIDTH > WINDOW_HEIGHT * columns) {
            height = WINDOW_HEIGHT;
            width = height / rows * columns;
        } else {
            width = WINDOW_WIDTH;
            height = width / columns * rows;
        }

        gameArea.setHeight(height);
        gameArea.setWidth(width);
        this.cellSize = height / rows;

        gameArea.setTranslateX(-gameArea.getWidth() / 2);
        gameArea.setTranslateY(-gameArea.getHeight() / 2);
    }

    public void clear() {
        fieldObjects.getChildren().clear();
    }

    public double getCellSize() {
        return cellSize;
    }

    public void DrawField(List<Node> objects) {
        clear();
        fieldObjects.getChildren().addAll(objects);
    }

    public void setOnControlsKeyPressed(EventHandler<KeyEvent> handler) {
        controlsHandler = handler;
    }

    private void handleKeyPressed(KeyEvent event) {
        KeyCode code = event.getCode();
        if (code == KeyCode.ESCAPE)
            tryQuitGame();
        else if (code.isArrowKey())
            controlsHandler.handle(event);
        event.consume();
    }

    private void tryQuitGame() {
        //TODO
    }

    public void setControlsActive(boolean state){
        gameArea.setFocusTraversable(state);
    }
}
