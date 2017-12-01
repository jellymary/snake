package snake;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

public class LevelLoader
{
    private static final Path LEVELS_PATH = Paths.get("src", "snake", "levels");
    private final Random random = new Random();
    private int LevelNumber;

    public LevelLoader() {
        File levelsDirectory = new File(LEVELS_PATH.toAbsolutePath().toString());
        int levelsCount = levelsDirectory.listFiles().length;
        LevelNumber = 1 + random.nextInt(levelsCount);
    }

    public Level loadRandomLevel() { return load(LevelNumber); }

    public Level loadLevel(int number) {
        return load(number);
    }

    private Level load(int number)
    {
        try
        {
            List<String> lines = readLines(number);
            Level level = buildConfiguration(lines.get(0));
            for (int i = 1; i < lines.size() - 1; i++)
            {
                MapObject[] objects = parseObject(lines.get(i), level);
                for (MapObject object : objects)
                    level.map.set(object.getLocation(), object);
            }
            String[] snakeInfo = lines.get(lines.size() - 1).split(" ");
            level.snake = new Snake(
                    Vector.parse(snakeInfo[0], ","),
                    Direction.parse(snakeInfo[1]),
                    level.map.getSize());
            return level;
        }
        catch (Exception ex)
        {
            return null;
        }
    }

    private List<String> readLines(int levelNumber) throws Exception
    {
        Path path = LEVELS_PATH.resolve(Integer.toString(levelNumber) + ".lvl");
        return Files.readAllLines(path);
    }

    private Level buildConfiguration(String line)
    {
        Level level = new Level();
        String[] parts = line.split(" ");
        Vector size = Vector.parse(parts[0], "x");
        level.map = new GameMap(size);
        level.targetLength = Integer.parseInt(parts[1]);
        level.timeout = Integer.parseInt(parts[2]);
        return level;
    }

    private MapObject[] parseObject(String data, Level level) throws Exception
    {
        String[] parts = data.split(" ");
        String[] instances = parts[1].split(";");
        MapObject[] objects = new MapObject[instances.length];
        for (int i = 0; i < objects.length; i++)
        {
            String[] details = instances[i].split("=");
            objects[i] = (MapObject)Class
                    .forName("snake." + parts[0])
                    .getConstructor(Level.class, Vector.class)
                    .newInstance(level, Vector.parse(details[0], ","));
            if (details.length > 1)
                objects[i].configure(details[1]);
        }
        return objects;
    }
}
