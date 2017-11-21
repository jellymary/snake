package snake;

public abstract class MapObject 
{
    private Vector location;
    protected Level level;
    
    public MapObject(Level level, Vector location) 
    {
        this.level = level;
        this.location = location.clone();
    }
    
    public void interact() {}
    public void configure(String info) {};
    
    public void delete()
    {
        level.map.set(location, null);
    }
    
    public Vector getLocation()
    {
        return location.clone();
    }
    
    public void setLocation(Vector newLocation)
    {
        newLocation = newLocation.bound(level.map.getSize());
        delete();
        level.map.set(newLocation, this);
        this.location = newLocation;
    }
    
    public void setRevealDelay(int ticks)
    {
        level.addEffect(new Effect(level -> level.map.set(location, this), ticks, level));
    }
}
