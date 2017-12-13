package snake.model.FieldObjects;

import snake.model.Interfaces.ISnakeHead;
import snake.model.util.Location;

public abstract class AbstractSolidFieldObject extends AbstractFieldObject {
    public AbstractSolidFieldObject(Location location) {
        super(location);
    }

    @Override
    public void snakeInteract(ISnakeHead snake) {
        snake.kill();
    }
}
