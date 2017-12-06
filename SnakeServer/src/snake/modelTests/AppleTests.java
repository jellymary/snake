package snake.modelTests;

import org.junit.Test;
import snake.*;

import static org.junit.Assert.*;

public class AppleTests 
{
    @Test
    public void appleEatingTest()
    {
        Level level = buildLevel();
        
        level.tick();
        
        assertNull(level.map.get(new Vector(1, 0)));
    }
    
    @Test
    public void appleGrowingTest()
    {
        Level level = buildLevel();
        
        level.tick();
        level.tick();
        
        assertEquals(level.snake.getLength(), 2);
    }
    
    @Test
    public void appleRespawningTest()
    {
        Level level = buildLevel();
        
        for (int i = 0; i < 3; i++)
            level.tick();
        
        boolean found = false;
        for (int x = 0; x < level.map.getSize().x; x++)
        {
            for (int y = 0; y < level.map.getSize().y; y++)
            {
                if (level.map.get(new Vector(x, y)) instanceof Apple)
                    found = true;
            }
        }
        assertTrue(found);
    }
    
    private Level buildLevel()
    {
        Level level = new Level();
        level.map = new GameMap(new Vector(5, 5));
        
        level.snake = new Snake(new Vector(0, 0), Direction.RIGHT, level.map.getSize());
        
        Vector appleLocation = new Vector(1, 0);
        level.map.set(appleLocation, new Apple(level, appleLocation));
        
        return level;
    }
}
