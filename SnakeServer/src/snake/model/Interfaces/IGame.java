package snake.model.Interfaces;

public interface IGame{
    void tick() throws  Exception;

    IField getField();
}
