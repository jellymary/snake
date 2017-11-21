package snake.tests;

import static org.junit.Assert.*;
import org.junit.Test;
import snake.*;

public class OracleTests 
{
    @Test
    public void oracleStayingTest()
    {
        Level level = buildLevel(1);
        
        level.tick();
        
        assertTrue(level.map.get(new Vector(1, 0)) instanceof Oracle);
    }
    
    @Test
    public void removingWallsTest()
    {
        Level level = buildLevel(1);
        
        level.tick();
        
        assertNull(level.map.get(new Vector(1, 1)));
        assertNull(level.map.get(new Vector(2, 1)));
    }
    
    @Test
    public void correctDurationTest()
    {
        Level level = buildLevel(1);
        
        for (int i = 0; i < 4; i++)
            level.tick();
        
        assertNull(level.map.get(new Vector(1, 1)));
        assertNull(level.map.get(new Vector(2, 1)));
    }
    
    @Test
    public void restoringWallsTest()
    {
        Level level = buildLevel(1);
        
        for (int i = 0; i < 5; i++)
            level.tick();
        
        assertTrue(level.map.get(new Vector(2, 0)) instanceof Wall);
        assertTrue(level.map.get(new Vector(3, 0)) instanceof Wall);
    }
    
    private Level buildLevel(int snakeLength)
    {
        Level level = new Level();
        level.map = new GameMap(new Vector(5, 5));
        
        level.snake = new Snake(new Vector(0, 0), Direction.RIGHT, level.map.getSize());
        level.snake.adjustLength(snakeLength - 1);

        Vector oracleLocation = new Vector(1, 0);
        Oracle oracle = new Oracle(level, oracleLocation);
        oracle.configure("3:2,0-3,0");
        level.map.set(oracleLocation, oracle);
        
        Vector[] walls = new Vector[] {new Vector(2, 0), new Vector(3, 0)};
        for (Vector wall : walls)
            level.map.set(wall, new Wall(level, wall));
        
        while (--snakeLength > 0)
            level.tick();
        return level;
    }
}
