package snake;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class CUI
{
    public static void main(String[] args) throws Exception
    {
        LevelLoader loader = new LevelLoader();
        int levelNumber = 1;
        while (true)
        {
            Level level = loader.loadLevel(levelNumber);
            if (level == null) {
                System.out.println("Game completed!");
                break;
            }
            while (level.state == LevelState.PLAYING)
            {
                printView(renderView(level));
                String line = readLine();
                Vector direction = Direction.parse(line);
                if (!direction.equals(Direction.NONE))
                    level.snake.setDirection(direction);
                level.tick();
            }
            if (level.state == LevelState.COMPLETED)
                levelNumber++;
            else if (level.state == LevelState.FAILED) {
                System.out.println("Level failed!");
                break;
            }
        }
    }

    private static String readLine()
    {
        try
        {
            InputStreamReader reader = new InputStreamReader(System.in);
            BufferedReader buffer = new BufferedReader(reader);
            return buffer.readLine();
        }
        catch (Exception ex)
        {
            return "";
        }
    }

    private static String[][] renderView(Level level)
    {
        Vector size = level.map.getSize();
        String[][] characters = new String[size.y][];
        for (int y = 0; y < size.y; y++)
        {
            characters[y] = new String[size.x];
            for (int x = 0; x < size.x; x++)
            {
                MapObject object = level.map.get(new Vector(x, y));
                if (object instanceof Apple)
                    characters[y][x] = "A";
                else if (object instanceof Wall)
                    characters[y][x] = "X";
                else if (object instanceof Mushroom)
                    characters[y][x] = "M";
                else if (object instanceof Gum)
                    characters[y][x] = "G";
                else if (object instanceof Oracle)
                    characters[y][x] = "@";
                else if (object instanceof Portal)
                    characters[y][x] = ((Portal)object).in ? "I" : "O";
                else if (object == null)
                    characters[y][x] = " ";
                else
                    characters[y][x] = "?";
            }
        }
        for (Vector part : level.snake.getTrace())
            characters[part.y][part.x] = "S";
        return characters;
    }

    private static void printView(String[][] view)
    {
        String[] lines = new String[view.length];
        for (int index = 0; index < lines.length; index++)
            lines[index] = String.join("", view[index]);
        String text = String.join("\n", lines);
        System.out.println(text);
    }
}