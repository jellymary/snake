package snake.model;

import snake.model.FieldObjects.SnakeHead;
import snake.model.FieldObjects.Wall;
import snake.model.Interfaces.IField;
import snake.model.util.Location;
import snake.model.util.Vector;

import java.util.Random;

public final class FieldGenerators {
    private FieldGenerators() {
    }

    public static IField genBoardedField(int height, int width, int playerCount) {
        if (height < 3 || width < 3)
            throw new IllegalArgumentException();

        IField field = new Field(height, width, playerCount);
        for (int x = 0; x < width; x++) {
            field.addObject(new Wall(new Location(x, 0)));
            field.addObject(new Wall(new Location(x, height - 1)));
        }

        for (int y = 1; y < height - 1; y++) {
            field.addObject(new Wall(new Location(0, y)));
            field.addObject(new Wall(new Location(width - 1, y)));
        }

        Random random = new Random();
        int index = 0;
        Vector direction = Vector.RIGHT;
        while (index < playerCount) {
            int x = random.nextInt(field.getWidth());
            int y = random.nextInt(field.getHeight());
            Location location = new Location(x, y);
            Location nextLocation = location.moved(direction);
            if (field.getObjectAt(location) == null && field.getObjectAt(nextLocation) == null) {
                SnakeHead snake = new SnakeHead(index, location,null, Vector.RIGHT, field);
                field.addObject(snake);
                index++;
            }
        }
        return field;
    }
}
