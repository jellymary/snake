package client.Tests;

import Units.Unit;
import client.Exceptions.IllegalGameMessageFormatException;
import client.GameConnection;
import client.GameMessage;
import client.GameMessageType;
import javafx.util.Pair;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Unit_Should {
    @Test
    void Run_Should() {
        GameConnection fakeConnection = new assertingGameConnection();
        Unit unit = new Unit("name", fakeConnection) {
            @Override
            protected void onGameFinished(GameMessage gameMessage) {
            }
            @Override
            protected void handleState(GameMessage gameMessage) {
            }
            @Override
            protected void onGameStarted(GameMessage gameMessage) {
            }
            @Override
            protected void prepareForGame(GameMessage gameMessage) {
            }
            @Override
            protected void stopGameWithError(String errorMessage) {
            }
        };

        unit.run(1);
    }

    private class assertingGameConnection implements GameConnection {
        private Queue<Pair<Boolean, GameMessageType>> expected = new LinkedBlockingQueue<>();

        public assertingGameConnection() {
            expected.add(new Pair(true, GameMessageType.Request));
            expected.add(new Pair(false, GameMessageType.GameIsReady));
            expected.add(new Pair(true, GameMessageType.ClientIsReady));
            expected.add(new Pair(false, GameMessageType.GameStarted));
            expected.add(new Pair(false, GameMessageType.GameFinished));
        }

        @Override
        public void sendMessage(GameMessage message) throws IOException {
            assertEquals(expected.peek(), new Pair<>(true, message.messageType));
            expected.remove();
        }

        @Override
        public GameMessage receiveMessage() throws IOException, IllegalGameMessageFormatException {
            assertEquals(expected.peek().getKey(), false);
            GameMessageType type = expected.peek().getValue();
            expected.remove();
            return new GameMessage("GAME_FINISHED");
        }
    }
}
