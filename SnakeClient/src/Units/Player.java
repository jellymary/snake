package Units;

import client.SocketGameConnection;
import client.GameMessage;

public class Player extends Unit {
    public Player(String name, SocketGameConnection gameConnection) {
        super(name, gameConnection);
    }

    @Override
    protected void prepareForGame(GameMessage gameMessage) {

    }

    @Override
    protected void handleState(GameMessage gameMessage) {

    }

    @Override
    protected void onGameStarted(GameMessage gameMessage) {

    }

    @Override
    protected void onGameFinished(GameMessage gameMessage) {

    }

    @Override
    protected void stopGameWithError(String errorMessage) {

    }
}
