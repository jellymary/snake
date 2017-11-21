package snake;

public class Mushroom extends MapObject
{
    private final int DIVIDER = 2;
    private final int TIMEOUT = 5;
    
    public Mushroom(Level level, Vector location) 
    {
        super(level, location);
    }
    
    @Override
    public void interact()
    {
        level.snake.adjustLength(-level.snake.getLength() / DIVIDER);
        (new Mushroom(level, getLocation())).setRevealDelay(TIMEOUT);
        delete();
    }
}
