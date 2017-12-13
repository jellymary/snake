package snake.model.Interfaces;

import snake.model.util.Vector;

public interface ISnakeHead extends IFieldObject {
    Vector getDirection();

    void setDirection(Vector direction);

    void kill();

    void eat(int growValue);

    int length();

    boolean isAlive();

    boolean willGrow();

    int getID();
}
