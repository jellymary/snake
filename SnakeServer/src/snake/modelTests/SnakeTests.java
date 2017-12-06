package snake.modelTests;

import org.junit.Test;
import snake.Direction;
import snake.Snake;
import snake.Vector;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class SnakeTests 
{
    @Test
    public void lengthIncreasingTest()
    {
        Snake snake = new Snake(new Vector(0, 0), Direction.RIGHT, new Vector(5, 5));
        
        snake.adjustLength(2);
        snake.makeStep();
        snake.makeStep();
        
        assertEquals(snake.getLength(), 3);
    }
    
    @Test
    public void lengthDecreasingTest()
    {
        Snake snake = new Snake(new Vector(0, 0), Direction.RIGHT, new Vector(5, 5));
        snake.adjustLength(2);
        snake.makeStep();
        snake.makeStep();
        
        snake.adjustLength(-2);
        snake.makeStep();
        
        assertEquals(snake.getLength(), 1);
    }
    
    @Test
    public void movingTest() 
    {
        Snake snake = buildSnake(new Vector(0, 3), Direction.UP, new Vector(10, 10), 3);
        assertArrayEquals(snake.getTrace(), 
                new Vector[] {new Vector(0, 1), new Vector(0, 2), new Vector(0, 3)}
        );
        
        snake.makeStep();
        assertArrayEquals(snake.getTrace(), 
                new Vector[] {new Vector(0, 0), new Vector(0, 1), new Vector(0, 2)}
        );
        snake.makeStep();
        assertArrayEquals(snake.getTrace(), 
                new Vector[] {new Vector(0, 9), new Vector(0, 0), new Vector(0, 1)}
        );
        snake.makeStep();
        assertArrayEquals(snake.getTrace(), 
                new Vector[] {new Vector(0, 8), new Vector(0, 9), new Vector(0, 0)}
        );
    }
    
    @Test
    public void wrongDirectionTest()
    {
        Snake snake = buildSnake(new Vector(0, 0), Direction.DOWN, new Vector(10, 10), 3);
        
        snake.setDirection(Direction.UP);
        
        assertEquals(snake.getDirection(), Direction.DOWN);
    }
    
    @Test
    public void turningTest()
    {
        Snake snake = buildSnake(new Vector(0, 0), Direction.DOWN, new Vector(10, 10), 3);
        assertArrayEquals(snake.getTrace(), 
                new Vector[] {new Vector(0, 2), new Vector(0, 1), new Vector(0, 0)}
        );
        
        snake.setDirection(Direction.RIGHT);
        
        snake.makeStep();
        assertArrayEquals(snake.getTrace(), 
                new Vector[] {new Vector(1, 2), new Vector(0, 2), new Vector(0, 1)}
        );
        snake.makeStep();
        assertArrayEquals(snake.getTrace(), 
                new Vector[] {new Vector(2, 2), new Vector(1, 2), new Vector(0, 2)}
        );
        snake.makeStep();
        assertArrayEquals(snake.getTrace(), 
                new Vector[] {new Vector(3, 2), new Vector(2, 2), new Vector(1, 2)}
        );
    }
    
    @Test
    public void gettingLocationsTest()
    {
        Snake snake = buildSnake(new Vector(0, 0), Direction.RIGHT, new Vector(10, 10), 3);
        
        assertEquals(snake.getHeadLocation(), new Vector(2, 0));
        assertEquals(snake.getNextLocation(), new Vector(3, 0));
    }
    
    @Test
    public void teleportationTest()
    {
        Snake snake = buildSnake(new Vector(0, 0), Direction.RIGHT, new Vector(10, 10), 3);
        
        snake.setHeadLocation(new Vector(0, 5));
        
        assertArrayEquals(snake.getTrace(),
                new Vector[] {new Vector(0, 5), new Vector(1, 0), new Vector(0, 0)}
        );
        snake.makeStep();
        assertArrayEquals(snake.getTrace(),
                new Vector[] {new Vector(1, 5), new Vector(0, 5), new Vector(1, 0)}
        );
        snake.makeStep();
        assertArrayEquals(snake.getTrace(),
                new Vector[] {new Vector(2, 5), new Vector(1, 5), new Vector(0, 5)}
        );
    }
    
    private Snake buildSnake(Vector location, Vector direction, Vector mapSize, int length)
    {
        Snake snake = new Snake(location, direction, mapSize);
        snake.adjustLength(length - 1);
        while (--length > 0)
            snake.makeStep();
        return snake;
    }
}
