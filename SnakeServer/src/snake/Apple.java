package snake;

public class Apple extends MapObject
{
    private final int COST = 1;
    private final int TIMEOUT = 2;
    
    public Apple(Level level, Vector location)
    {
        super(level, location);
    }
    
    @Override
    public void interact()
    {
        level.snake.adjustLength(COST);
        (new Apple(level, level.findFreeCell())).setRevealDelay(TIMEOUT);
        delete();
    }
}
