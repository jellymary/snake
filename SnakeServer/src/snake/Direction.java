package snake;

public class Direction 
{
    public static final Vector UP = new Vector(0, -1);
    public static final Vector DOWN = new Vector(0, 1);
    public static final Vector LEFT = new Vector(-1, 0);
    public static final Vector RIGHT = new Vector(1, 0);
    public static final Vector NONE = new Vector(0, 0);
    
    public static Vector parse(String text)
    {
        try
        {
            return (Vector) Direction.class.getField(text.toUpperCase()).get(null);
        }
        catch (Exception ex)
        {
            return Direction.NONE;
        }
    }
}
