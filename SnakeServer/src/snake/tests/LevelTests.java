package snake.tests;

import static org.junit.Assert.*;
import org.junit.Test;
import snake.*;

public class LevelTests 
{
    @Test
    public void findingEmptyCellTest() 
    {
        Level level = buildLevel();
        
        for (Vector vector : new Vector[] {new Vector(1, 1), new Vector(2, 2), new Vector(3, 3)})
            level.map.set(vector, new Wall(level, vector));
        
        assertNull(level.map.get(level.findFreeCell()));
    }
    
    @Test
    public void effectsExecutingTest()
    {
        Level level = buildLevel();
        level.addEffect(new Effect(lvl -> lvl.state = LevelState.FAILED, 2, level));
        
        for (int i = 0; i < 3; i++)
            level.tick();
        
        assertEquals(level.state, LevelState.FAILED);
    }
    
    @Test
    public void timeoutTest()
    {
        Level level = buildLevel();
        level.timeout = 4;
        
        for (int i = 0; i < 4; i++)
            level.tick();
        
        assertEquals(level.snake.getHeadLocation(), new Vector(1, 0));
    }
    
    @Test
    public void completingTest()
    {
        Level level = buildLevel();
        level.targetLength = 2;
        
        level.snake.adjustLength(1);
        level.tick();
        
        assertEquals(level.state, LevelState.COMPLETED);
    }
    
    private Level buildLevel()
    {
        Level level = new Level();
        level.map = new GameMap(new Vector(5, 5));
        level.snake = new Snake(new Vector(0, 0), Direction.RIGHT, level.map.getSize());
        return level;
    }
}
