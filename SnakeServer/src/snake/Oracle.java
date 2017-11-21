package snake;

public class Oracle extends MapObject
{
    private Vector[] area;
    private int delay;
    
    public Oracle(Level level, Vector location)
    {
        super(level, location);
    }
    
    @Override
    public void interact()
    {
        int delay = level.snake.getLength() + this.delay;
        for (Vector wall : area)
        {
            level.map.set(wall, null);
            (new Wall(level, wall)).setRevealDelay(delay);
        }
    }
    
    @Override
    public void configure(String info)
    {
        String[] parts = info.split(":");
        delay = Integer.parseInt(parts[0]);
        String[] walls = parts[1].split("-");
        area = new Vector[walls.length];
        for (int i = 0; i < area.length; i++)
            area[i] = Vector.parse(walls[i], ",");
    }
}
