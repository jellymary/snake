package snake;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.function.Supplier;

public class Player {
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;
    private LevelSerializer serializer;
    private String name;
    private Level game;
    public int ID;
    public boolean isWinner;
    public boolean isAvailable = true;
    public Thread actionThread;

    private HashMap<Message, Supplier<String[]>> Content = new HashMap<>();

    Player(Socket socket)
    {
        this.socket = socket;
        try {
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());
        } catch (IOException ignore) {}
        serializer = new LevelSerializer();
    }

    public void setGame(Level level) {
        game = level;
        fillContent();
    }

    private void fillContent() {
        Content.put(Message.GAME_IS_READY, () -> new String[]{Integer.toString(game.map.getSize().x), Integer.toString(game.map.getSize().y)});
        Content.put(Message.GAME_STATE, () -> new String[]{serializer.serializeForPlayer(game, ID)});
        Content.put(Message.GAME_STARTED, () -> new String[]{});
        Content.put(Message.GAME_FINISHED, () -> new String[]{isWinner ? "WIN" : "LOSE"});
    }

    @Override
    public String toString() {
        return socket.getInetAddress().toString() + String.format("(%s)", name);
    }

    String read (Message messageType) throws IllegalGameMessageFormatException {
        if (this.isAvailable)
            try {
                GameMessage message = new GameMessage(input.readUTF());
                if (messageType != message.messageType)
                    throw new IllegalGameMessageFormatException("Another type of message was expected");
                if (messageType.equals(Message.REQUEST)) {
                    this.name = message.content[0];
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
                output.writeUTF(GameMessage.getFullMessage(messageType, Content.get(messageType).get()));
            } catch (IOException e) {
                isAvailable = false;
            }
    }

    void socketClosing() {
        try {
            socket.close();
        } catch (IOException ignore) {}
    }
}
