package snake.modelTests;

import org.junit.Test;
import snake.*;

import static org.junit.Assert.*;

public class GameTests 
{
    @Test
    public void ouroborosTest() 
    {
        Level level = buildLevel(5);
        level.snake.setDirection(Direction.DOWN);
        level.tick();
        level.snake.setDirection(Direction.LEFT);
        level.tick();
        level.snake.setDirection(Direction.UP);
        
        level.tick();
        
        assertEquals(level.state, LevelState.FAILED);
    }
    
    @Test
    public void mapObjectRevealingTest()
    {
        Level level = buildLevel(1);
        (new Wall(level, new Vector(0, 0))).setRevealDelay(4);
        
        for (int i = 0; i < 5; i++)
            level.tick();
        
        assertTrue(level.map.get(new Vector(0, 0)) instanceof Wall);
    }
    
    @Test
    public void wallCollisionTest()
    {
        Level level = buildLevel(1);
        level.map.set(new Vector(1, 0), new Wall(level, new Vector(1, 0)));
        
        level.tick();
        
        assertEquals(level.state, LevelState.FAILED);
    }
    
    @Test
    public void gumTest()
    {
        Level level = buildLevel(1);
        level.map.set(new Vector(1, 0), new Gum(level, new Vector(1, 0)));
        int timeout = 1;
        level.timeout = timeout;
        
        level.tick();
        
        assertNotEquals(level.timeout, timeout);
    }
    
    @Test
    public void mushroomTest()
    {
        Level level = buildLevel(4);
        Vector mushroomLocation = new Vector(4, 0);
        level.map.set(mushroomLocation, new Mushroom(level, mushroomLocation));
        
        level.tick();
        
        assertEquals(level.snake.getLength(), 2);
    }

    private Level buildLevel(int snakeLength)
    {
        Level level = new Level();
        level.map = new GameMap(new Vector(5, 5));
        
        level.snake = new Snake(new Vector(0, 0), Direction.RIGHT, level.map.getSize());
        level.snake.adjustLength(snakeLength - 1);
        
        while (--snakeLength > 0)
            level.tick();
        return level;
    }
}
