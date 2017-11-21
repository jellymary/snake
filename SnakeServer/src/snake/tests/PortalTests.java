package snake.tests;

import static org.junit.Assert.*;
import org.junit.Test;
import snake.*;

public class PortalTests 
{    
    @Test
    public void creatingOutputTest()
    {
        Level level = buildLevel();
        
        MapObject object = level.map.get(new Vector(1, 1));
        
        assertNotNull(object);
        assertTrue(object instanceof Portal);
        assertFalse(((Portal)object).in);
    }
    
    @Test
    public void transparentOutputTest()
    {
        Level level = buildLevel();
        
        level.snake.setDirection(Direction.DOWN);
        level.tick();
        level.tick();
        
        assertArrayEquals(level.snake.getTrace(), new Vector[] {new Vector(1, 2), new Vector(1, 1)});
    }
    
    @Test
    public void correctDirectionAfterTeleportationTest()
    {
        Level level = buildLevel();
        
        level.tick();
        
        assertEquals(level.snake.getDirection(), Direction.RIGHT);
    }
    
    @Test
    public void correctTeleportationTest()
    {
        Level level = buildLevel();
        
        level.tick();
        level.tick();
        
        assertArrayEquals(level.snake.getTrace(), new Vector[] {new Vector(2, 1), new Vector(1, 1)});
    }
    
    @Test
    public void destroyingOutputTest()
    {
        Level level = buildLevel();
        
        level.tick();
        
        assertNull(level.map.get(new Vector(1, 1)));
    }
    
    @Test
    public void respawningOutputTest()
    {
        Level level = buildLevel();
        
        level.tick();
        
        boolean found = false;
        for (int x = 0; x < level.map.getSize().x; x++)
        {
            for (int y = 0; y < level.map.getSize().y; y++)
            {
                MapObject object = level.map.get(new Vector(x, y));
                if (object instanceof Portal && !((Portal)object).in)
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
        
        Portal portal = new Portal(level, new Vector(2, 0));
        portal.configure("1,1");
        level.map.set(portal.getLocation(), portal);
        
        level.snake.adjustLength(1);
        level.tick();
        return level;
    }
}
