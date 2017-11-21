package snake;

import java.util.function.Consumer;

public class Effect
{
    private int duration;
    private int passed;
    private Level level;
    
    private Consumer<Level> action;
    
    public boolean finished;
    
    public Effect(Consumer<Level> action, int duration, Level level)
    {
        this.action = action != null ? action : lvl -> {};
        
        this.duration = duration;
        this.level = level;
        passed = 0;
        finished = false;
    }
    
    public void tick()
    {
        if (++passed > duration)
        {
            finished = true;
            action.accept(level);
            return;
        }
    }
}
