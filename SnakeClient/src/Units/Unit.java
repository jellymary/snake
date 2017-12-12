package Units;


import client.Exceptions.IllegalGameMessageFormatException;
import client.GameConnection;
import client.GameMessage;
import client.GameMessageType;
import client.Utils.Direction;

import java.io.DataInputStream;
import java.io.IOException;

public abstract class Unit {
    private final String name;
    private final GameConnection gameConnection;

    public Unit(String name, GameConnection gameConnection) {
        this.name = name;
        this.gameConnection = gameConnection;
    }

    public void run(int desiredPlayersNUmber) {
        try {
            gameConnection.sendMessage(GameMessage.makeRequestMessage(name, desiredPlayersNUmber));

            prepareForGame(checkMessagesType(nextMessage(), GameMessageType.ClientIsReady));

            gameConnection.sendMessage(GameMessage.makeClientIsReadyMessage());

            onGameStarted(checkMessagesType(nextMessage(), GameMessageType.GameStarted));

            GameMessage serverMessage;
            while (true) {
                serverMessage = nextMessage();

                if (serverMessage.messageType == GameMessageType.GameFinished)
                    break;
                handleState(checkMessagesType(serverMessage, GameMessageType.GameState));
            }

            onGameFinished(serverMessage);
        } catch (IOException e) { // TODO log maybe?
            stopGameWithError("Network error");
        } catch (GameProcessCorruptedException | UnexpectedMessageTypeException e) {
            stopGameWithError("Game process corrupted");
        } catch (Exception e) {
            stopGameWithError("Unknown error");
        }
    }

    private GameMessage checkMessagesType(GameMessage gameMessage, GameMessageType expectedType) throws UnexpectedMessageTypeException {
        if (gameMessage.messageType != expectedType)
            throw new UnexpectedMessageTypeException();
        return gameMessage;
    }

    private GameMessage nextMessage() throws GameProcessCorruptedException {
        try {
            return gameConnection.receiveMessage();
        } catch (IllegalGameMessageFormatException e) {
            stopGameWithError("Connection format error");
            throw new GameProcessCorruptedException(e);
        } catch (IOException e) {
            stopGameWithError("Connection error");
            throw new GameProcessCorruptedException(e);
        }
    }

    public void changeDirection(Direction desiredDirection) throws IOException {
        gameConnection.sendMessage(GameMessage.makePlayersActionMessage(desiredDirection));
    }

    protected abstract void onGameFinished(GameMessage gameMessage);

    protected abstract void handleState(GameMessage gameMessage);

    protected abstract void onGameStarted(GameMessage gameMessage);

    protected abstract void prepareForGame(GameMessage gameMessage);

    protected abstract void stopGameWithError(String errorMessage);
}
