package client;

import client.Exceptions.IllegalGameMessageFormatException;
import client.Utils.Direction;
import client.Utils.GameResult;
import client.Utils.Size;
import javafx.scene.input.KeyCode;

import java.util.HashMap;
import java.util.Map;

public class GameMessage {
    public final GameMessageType messageType;
    public final String content;

    private static final Map<String, GameMessageType> MessageTypeByName = new HashMap<>();
    private static final Map<GameMessageType, String> NameByMessageType = new HashMap<>();

    static {
        MessageTypeByName.put("REQUEST", GameMessageType.Request);
        MessageTypeByName.put("GAME_IS_READY", GameMessageType.GameIsReady);
        MessageTypeByName.put("CLIENT_IS_READY", GameMessageType.ClientIsReady);
        MessageTypeByName.put("GAME_STARTED", GameMessageType.GameStarted);
        MessageTypeByName.put("PLAYER_ACTION", GameMessageType.PlayersAction);
        MessageTypeByName.put("GAME_STATE", GameMessageType.GameState);
        MessageTypeByName.put("GAME_FINISHED", GameMessageType.GameFinished);

        MessageTypeByName.forEach((name, messageType) -> NameByMessageType.put(messageType, name));
    }

    public GameMessage(String raw) throws IllegalGameMessageFormatException {
        if (raw == null)
            throw new IllegalArgumentException("Raw shoulfd not be null");
        int endOfFirstLine = getEndOfFirstLine(raw);
        messageType = MessageTypeByName.get(raw.substring(0, endOfFirstLine));
        if (messageType == null)
            throw new IllegalGameMessageFormatException("Message messageType not recognized");
        this.content = raw.substring(endOfFirstLine + 1);
    }

    private GameMessage(GameMessageType messageType, String content) {
        this.messageType = messageType;
        this.content = content;
    }

    private int getEndOfFirstLine(String raw) {
        int endOfFirstLine = raw.indexOf('\n');
        if (endOfFirstLine == -1)
            endOfFirstLine = raw.length();
        return endOfFirstLine;
    }

    public static GameMessage makePlayersActionMessage(Direction desiredDirection) {
        String content;
        switch (desiredDirection) {
            case Up:
                content = "UP";
                break;
            case Down:
                content = "DOWN";
                break;
            case Left:
                content = "LEFT";
                break;
            case Right:
                content = "RIGHT";
                break;
            default:
                throw new IllegalArgumentException();
        }
        return new GameMessage(GameMessageType.PlayersAction, content + "\n");
    }

    public static GameMessage makeRequestMessage(String name, int desiredPlayersNumber) {
        if (name == null || name.isEmpty())
            throw new IllegalArgumentException("Name should be not empty string");
        if (desiredPlayersNumber < 1)
            throw new IllegalArgumentException("Desired players number should not be less than one");
        return new GameMessage(GameMessageType.Request, String.join("\n", name, Integer.toString(desiredPlayersNumber)));
    }

    public static GameMessage makeClientIsReadyMessage() {
        return new GameMessage(GameMessageType.ClientIsReady, "");
    }

    public String toString() {
        return String.join("\n", NameByMessageType.get(messageType), content);
    }

    public static Size parseFieldSize(GameMessage serverMessage) {
        if (serverMessage.messageType != GameMessageType.GameIsReady)
            throw new IllegalArgumentException("Message type should be " + GameMessageType.GameIsReady.name());

        String[] lines = serverMessage.content.split("\n", 2);

        return new Size(Integer.parseInt(lines[0]), Integer.parseInt(lines[0]));
    }

    public static GameResult parseFinishResult(GameMessage serverMessage) {
        if (serverMessage.messageType != GameMessageType.GameFinished)
            throw new IllegalArgumentException("Message type should be " + GameMessageType.GameFinished.name());

        switch (serverMessage.content.split("\n", 1)[0]) {
            case "WIN":
                return GameResult.WIN;
            case "LOSE":
                return GameResult.LOSS;
            case "TIE":
                return GameResult.TIE;
            default:
                throw new IllegalArgumentException("Can't recognize game result");
        }
    }
}
