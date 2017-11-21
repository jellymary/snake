package snake;

public class Wall extends MapObject
{
    public Wall(Level level, Vector location) 
    {
        super(level, location);
    }

    @Override    
    public void interact()
    {
        level.state = LevelState.FAILED;
    }
}
