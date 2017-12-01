package snake;

import java.util.HashSet;
import java.util.Random;

public class Level
{
    public Snake snake;
    public GameMap map;
    public int timeout;
    public int targetLength;
    public LevelState state;

    private int elapsed;
    private HashSet<Effect> effects;

    public Level()
    {
        state = LevelState.PLAYING;
        effects = new HashSet<>();
    }

    public void addEffect(Effect effect)
    {
        effects.add(effect);
    }

    public void tick()
    {
        if (++elapsed < timeout)
            return;
        elapsed = 0;

        snake.makeStep();
        checkCollisions();
        effectsTick();

        if (snake.getLength() == targetLength)
            state = LevelState.COMPLETED;
    }

    public Vector findFreeCell()
    {
        Random random = new Random();
        Vector mapSize = map.getSize();
        HashSet<Vector> snakeTrace = new HashSet<>();
        for (Vector part : snake.getTrace())
            snakeTrace.add(part);

        Vector location;
        do
            location = new Vector(random.nextInt(mapSize.x), random.nextInt(mapSize.y));
        while (snakeTrace.contains(location) || map.get(location) != null);

        return location;
    }

    private void checkCollisions()
    {
        Vector headLocation = snake.getHeadLocation();
        MapObject object = map.get(headLocation);
        if (object != null)
            object.interact();

        Vector[] snakeTrace = snake.getTrace();
        for (int i = 1; i < snakeTrace.length; i++)
        {
            if (snakeTrace[i].equals(headLocation))
            {
                state = LevelState.FAILED;
                break;
            }
        }
    }

    private void effectsTick()
    {
        HashSet<Effect> toRemove = new HashSet<>();
        for (Effect effect : effects)
        {
            effect.tick();
            if (effect.finished)
                toRemove.add(effect);
        }
        effects.removeAll(toRemove);
    }

    public String serialize() {
        return "it's game map :)";
    }
}
