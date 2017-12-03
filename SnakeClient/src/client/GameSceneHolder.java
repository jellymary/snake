package client;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SubScene;

public class GameSceneHolder implements SceneHolder {
    private final Scene scene;
    private final Group fieldObjects = new Group();
    private final SubScene gameArea;
    private int columns, rows;
    private final double WINDOW_WIDTH;
    private final double WINDOW_HEIGHT;
    private double cellSize;

    GameSceneHolder(double windowWidth, double windowHeight) {
        WINDOW_HEIGHT = windowHeight;
        WINDOW_WIDTH = windowWidth;

        gameArea = new SubScene(fieldObjects, 1, 1);
        gameArea.setLayoutX(WINDOW_WIDTH / 2);
        gameArea.setLayoutY(WINDOW_HEIGHT / 2);
        scene = new Scene(new Group(gameArea));
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

    public void DrawField(Node[] objects) {
        clear();
        fieldObjects.getChildren().addAll(objects);
    }
}
