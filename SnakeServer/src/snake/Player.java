package snake;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Objects;

public class Player {
    private Socket SOCKET;
    private DataInputStream IN;
    private DataOutputStream OUT;
    private LevelSerializer serializer;
    private String NAME;
    private Level GAME;
    private int ID;
    public boolean isWinner;
    public boolean isAvailable = true;

    Player(Socket socket)
    {
        SOCKET = socket;
        try {
            IN = new DataInputStream(socket.getInputStream());
            OUT = new DataOutputStream(socket.getOutputStream());
        } catch (IOException ignore) {}
        serializer = new LevelSerializer();
    }

    public void setGame(Level level) {
        GAME = level;
    }

    public void setID(int id) { ID = id; }

    String read (Message messageType) throws IllegalGameMessageFormatException {
        try {
            String message = IN.readUTF();
            String[] lines = message.split("\n");
            if (!Objects.equals(lines[0], messageType.toString()))
                throw new IllegalGameMessageFormatException(
                        String.format("Expected: %s, actual: %s", messageType.toString(), lines[0])
                );
            if (messageType == Message.REQUEST) {
                NAME = lines[1];
                return lines[2];
            }
            else if (messageType == Message.PLAYER_ACTION) {
                return lines[1];
            }
        } catch (IOException e) {
            isAvailable = false;
        }
        return null;
    }

    void send(Message messageType) {
        String message = messageType.toString() + "\n";
        if (messageType == Message.GAME_IS_READY) {
            Vector mapSize = GAME.map.getSize();
            message += mapSize.x + "\n" + mapSize.y;
        }
        else if (messageType == Message.GAME_STATE) {
            message += serializer.serializeForPlayer(GAME, ID);
        }
        else if (messageType == Message.GAME_FINISHED) {
            message += isWinner ? "WIN" : "LOSE";
        }
        try {
            OUT.writeUTF(message);
        } catch (IOException e) {
            isAvailable = false;
        }
    }

    void socketClosing() {
        try {
            SOCKET.close();
        } catch (IOException ignore) {}
    }
}
