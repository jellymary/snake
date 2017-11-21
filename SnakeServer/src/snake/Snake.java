package snake;

import java.util.LinkedList;

public class Snake 
{    
    private LinkedList<Vector> body;
    private Vector direction;
    private Vector mapSize;
    private int lengthQueue;
    
    public Snake(Vector location, Vector direction, Vector mapSize)
    {
        lengthQueue = 0;
        body = new LinkedList<Vector>();
        
        body.add(location.clone());
        this.direction = direction.clone();
        this.mapSize = mapSize.clone();
    }
    
    public int getLength()
    {
        return body.size();
    }
    
    public Vector getDirection()
    {
        return direction.clone();
    }
    
    public void setDirection(Vector direction)
    {
        if (Vector.getScalarProduct(this.direction, direction) == 0)
            this.direction = direction.clone();
    }
    
    public Vector getNextLocation()
    {
        return body.peekFirst().add(direction).bound(mapSize);
    }
    
    public Vector getHeadLocation()
    {
        return body.peekFirst().clone();
    }
    
    public void setHeadLocation(Vector location)
    {
        body.removeFirst();
        body.addFirst(location.bound(mapSize));
    }
    
    public Vector[] getTrace()
    {
        Vector[] trace = new Vector[getLength()];
        body.toArray(trace);
        return trace;
    }
    
    public void adjustLength(int length)
    {
        while (length < 0)
        {
            body.removeLast();
            length++;
        }
        
        lengthQueue += length;
    }
    
    public void makeStep()
    {
        body.addFirst(getNextLocation());
        
        if (lengthQueue > 0)
            lengthQueue--;
        else
            body.removeLast();
    }
}