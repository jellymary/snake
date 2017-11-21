package snake;

public class GameMap 
{
    private MapObject[][] objects;
    
    public GameMap(Vector size)
    {
        objects = new MapObject[size.x][];
        
        for (int x = 0; x < objects.length; x++)
        {
            objects[x] = new MapObject[size.y];
        }
    }
       
    public Vector getSize()
    {
        return new Vector(objects.length, objects.length > 0 ? objects[0].length : 0);
    }
    
    public void set(Vector location, MapObject object)
    {
        objects[location.x][location.y] = object;
    }
    
    public MapObject get(Vector location) 
    {
        return objects[location.x][location.y];
    }
}