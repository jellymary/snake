package snake;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.function.Supplier;

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

    private HashMap<Message, Supplier<String[]>> Content = new HashMap<>();

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
        fillContent();
    }

    private void fillContent() {
        Content.put(Message.GAME_IS_READY, () -> new String[]{Integer.toString(GAME.map.getSize().x), Integer.toString(GAME.map.getSize().y)});
        Content.put(Message.GAME_STATE, () -> new String[]{serializer.serializeForPlayer(GAME, ID)});
        Content.put(Message.GAME_STARTED, () -> new String[]{});
        Content.put(Message.GAME_FINISHED, () -> new String[]{isWinner ? "WIN" : "LOSE"});
    }

    public void setID(int id) { ID = id; }

    public int getID() { return ID; }

    @Override
    public String toString() {
        return SOCKET.getInetAddress().toString() + String.format("(%s)", NAME);
    }

    String read (Message messageType) throws IllegalGameMessageFormatException {
        if (this.isAvailable)
            try {
                GameMessage message = new GameMessage(IN.readUTF());
                if (messageType != message.messageType)
                    throw new IllegalGameMessageFormatException("Another type of message was expected");
                if (messageType.equals(Message.REQUEST)) {
                    this.NAME = message.content[0];
                    return message.content[1];
                }
                else return message.content[0];
            } catch (IOException e) {
                isAvailable = false;
                return null;
            }
        return null;
    }

    void send(Message messageType) {
        if (this.isAvailable)
            try {
                OUT.writeUTF(GameMessage.getFullMessage(messageType, Content.get(messageType).get()));
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
