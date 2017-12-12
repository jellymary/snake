package client.Tests;

import client.GameMessage;
import client.GameMessageType;
import client.Exceptions.IllegalGameMessageFormatException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GameMessage_Should {
    private String content = "con\ntet";

    @Test
    void content_ShouldBeFromSecondLineTillTheEndOfRaw() throws Exception {
        String raw = "REQUEST\n" + content;

        GameMessage gameMessage = new GameMessage(raw);

        assertEquals(content, gameMessage.content);
    }

    @Test
    void GameFinished_ShouldBeParsedCorrectly() throws IllegalGameMessageFormatException {
        assertGameMessageTypeParsedCorrectly(GameMessageType.GameFinished, "GAME_FINISHED");
    }

    @Test
    void ClientIsReady_ShouldBeParsedCorrectly() throws IllegalGameMessageFormatException {
        assertGameMessageTypeParsedCorrectly(GameMessageType.ClientIsReady, "CLIENT_IS_READY");
    }

    @Test
    void GameStarted_ShouldBeParsedCorrectly() throws IllegalGameMessageFormatException {
        assertGameMessageTypeParsedCorrectly(GameMessageType.GameStarted, "GAME_STARTED");
    }

    @Test
    void GameIsReady_ShouldBeParsedCorrectly() throws IllegalGameMessageFormatException {
        assertGameMessageTypeParsedCorrectly(GameMessageType.GameIsReady, "GAME_IS_READY");
    }

    @Test
    void GameState_ShouldBeParsedCorrectly() throws IllegalGameMessageFormatException {
        assertGameMessageTypeParsedCorrectly(GameMessageType.GameState, "GAME_STATE");
    }

    @Test
    void PlayersAction_ShouldBeParsedCorrectly() throws IllegalGameMessageFormatException {
        assertGameMessageTypeParsedCorrectly(GameMessageType.PlayersAction, "PLAYER_ACTION");
    }

    private void assertGameMessageTypeParsedCorrectly(GameMessageType expectedType, String strMessageType) throws IllegalGameMessageFormatException {
        assertEquals(expectedType, new GameMessage(strMessageType + "\n" + content).messageType);
    }
}
