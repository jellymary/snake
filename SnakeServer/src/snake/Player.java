package snake;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Player {
    private Socket player;
    private DataInputStream in;
    private DataOutputStream out;

    public Player(Socket player)
    {
        this.player = player;
        try {
            this.in = new DataInputStream(player.getInputStream());
            this.out = new DataOutputStream(player.getOutputStream());
        } catch (IOException ignore) {}
    }

    public void send(String message) {
        try {
                out.writeUTF(message);
            } catch (IOException ignore) {}
    }

    public void sendMap(Level level) {
        try {
            out.writeUTF(getBytes(level));
        } catch (IOException ignore) {}
    }

    private String getBytes(Level level) {
        String [][] view = renderView(level);
        String[] lines = new String[view.length];
        for (int index = 0; index < lines.length; index++)
            lines[index] = String.join("", view[index]);
        return String.join("\n", lines);
    }

    public String catchEventKey() {
        try {
            return in.readUTF();
        } catch (IOException ignore) {}
        return null;
    }

    public void gameClosing() {
        send("Game over");
        try {
            player.close();
        } catch (IOException ignore) {}
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
}




