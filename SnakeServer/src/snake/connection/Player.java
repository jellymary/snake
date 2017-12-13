package snake.connection;

import snake.model.*;

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

    public Thread actionThread;
    private LevelSerializer serializer;
    private Game game;

    private String name;
    public int ID;
    public boolean isWinner;
    public boolean isAvailable = true;

    private HashMap<Message, Supplier<String[]>> content = new HashMap<>();

    Player(Socket socket)
    {
        this.socket = socket;
        try {
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());
        } catch (IOException ignore) {}
        serializer = new LevelSerializer();
    }

    public void setGame(Game level) {
        game = level;
        fillContent();
    }

    private void fillContent() {
        content.put(Message.GAME_IS_READY, () -> new String[]{
                Integer.toString(game.getField().getWidth()),
                Integer.toString(game.getField().getHeight()),
                //Integer.toString(this.ID)
        });
        content.put(Message.GAME_STATE, () -> new String[]{serializer.serializeForPlayer(game, ID)});
        content.put(Message.GAME_STARTED, () -> new String[]{});
        content.put(Message.GAME_FINISHED, () -> new String[]{isWinner ? "WIN" : "LOSE"});
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
                    throw new IllegalGameMessageFormatException("Message type should be" + messageType);
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
                output.writeUTF(GameMessage.getFullMessage(messageType, content.get(messageType).get()));
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
