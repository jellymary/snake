package snake;

public class Portal extends MapObject
{
    private Vector outLocation;
    
    public boolean in;
    
    public Portal(Level level, Vector location) 
    {
        super(level, location);
    }
    
    @Override
    public void interact()
    {
        if (!in)
            return;
        level.snake.setHeadLocation(outLocation);
        level.map.get(outLocation).delete();
        outLocation = level.findFreeCell();
        buildOutPortal();
    }
    
    @Override
    public void configure(String info)
    {
        outLocation = Vector.parse(info, ",");
        in = true;
        buildOutPortal();
    }
    
    private void buildOutPortal()
    {
        Portal outPortal = new Portal(level, outLocation);
        outPortal.in = false;
        level.map.set(outLocation, outPortal);
    }
}
