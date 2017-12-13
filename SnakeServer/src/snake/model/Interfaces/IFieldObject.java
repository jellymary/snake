package snake.model.Interfaces;

import snake.model.util.Location;

public interface IFieldObject
{
    Location getLocation();

    void snakeInteract(ISnakeHead snake);

    void act();
}