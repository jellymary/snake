package snake.model.FieldObjects;

import snake.model.Interfaces.ISnakeHead;
import snake.model.util.Location;

public class SnakeBody extends SnakePart {
    private final ISnakeHead head;
    protected SnakePart next;

    public SnakeBody(Location location, ISnakeHead head, SnakeBody prev, SnakePart next)
    {
        super(location, prev, next.field);
        this.next = next;
        this.head = head;
    }

    @Override
    protected void move() {
        moveChild();
        setLocation(next.getLocation());
        field.addObject(this);
    }

    @Override
    public ISnakeHead getHead() {
        return head;
    }
}
