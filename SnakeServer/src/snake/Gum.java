package snake;

public class Gum extends MapObject
{
    private final int DELAY = 5;
    private final int RATIO = 2;
    private final int TIMEOUT = 10;
    
    public Gum(Level level, Vector location)
    {
        super(level, location);
    }
    
    @Override 
    public void interact()
    {
        level.timeout *= RATIO;
        level.addEffect(new Effect(level -> level.timeout /= RATIO, DELAY, level));
        (new Gum(level, level.findFreeCell())).setRevealDelay(TIMEOUT);
        delete();
    }
}
