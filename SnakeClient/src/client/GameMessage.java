package client;

import java.util.HashMap;
import java.util.Map;

public class GameMessage {
    final GameMessageType type;
    final String raw;

    public static final Map<String, GameMessageType> MessageTypeByName = new HashMap<>();
    public static final Map<GameMessageType, String> NameByMessageType = new HashMap<>();

    static {
        MessageTypeByName.put("REQUEST", GameMessageType.Request);
        MessageTypeByName.put("GAME_IS_READY", GameMessageType.GameIsReady);
        MessageTypeByName.put("CLIENT_IS_READY", GameMessageType.ClientIsReady);
        MessageTypeByName.put("GAME_STARTED", GameMessageType.GameStarted);
        MessageTypeByName.put("PLAYERS_ACTION", GameMessageType.PlayersAction);
        MessageTypeByName.put("GAME_STATE", GameMessageType.GameState);
        MessageTypeByName.put("GameFinished", GameMessageType.GameFinished);

        NameByMessageType.put(GameMessageType.Request, "REQUEST");
        NameByMessageType.put(GameMessageType.GameIsReady, "GAME_IS_READY");
        NameByMessageType.put(GameMessageType.ClientIsReady, "CLIENT_IS_READY");
        NameByMessageType.put(GameMessageType.GameStarted, "GAME_STARTED");
        NameByMessageType.put(GameMessageType.PlayersAction, "PLAYERS_ACTION");
        NameByMessageType.put(GameMessageType.GameState, "GAME_STATE");
        NameByMessageType.put(GameMessageType.GameFinished, "GameFinished");
    }

    GameMessage(String raw) throws IllegalGameMessageFormatException {
        this.raw = raw;
        type = getMessageType(raw);
    }

    private GameMessageType getMessageType(String raw) throws IllegalGameMessageFormatException {
        for (String key:
             MessageTypeByName.keySet())
            if (raw.startsWith(key))
                return MessageTypeByName.get(key);
        throw new IllegalGameMessageFormatException("Message type not recognized");
    }
}
