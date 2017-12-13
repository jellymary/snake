package snake.model.Interfaces;

import snake.model.util.Location;

public interface IField extends Iterable<IFieldObject> {
    int getWidth();

    int getHeight();

    IFieldObject getObjectAt(Location location);

    IFieldObject getObjectAt(int x, int y);

    void addObject(IFieldObject object);

    ISnakeHead getSnakeHead(int id);

    int getSnakeCount();

    void eraseAt(Location location);
}