package snake.model.FieldObjects;

import snake.model.Interfaces.IField;
import snake.model.Interfaces.ISnakeHead;
import snake.model.util.Location;

public class Apple extends AbstractFieldObject {
    private final int foodValue;
    private IField field;

    public Apple(Location location, IField field, int foodValue) {
        super(location);
        if (field == null)
            throw new IllegalArgumentException("field should not be null.");
        if (foodValue < 1)
            throw new IllegalArgumentException("foodValue should be positive.");
        this.foodValue = foodValue;
        this.field = field;
    }

    @Override
    public void snakeInteract(ISnakeHead snake) {
        snake.eat(foodValue);
        field.eraseAt(getLocation());
    }
}
