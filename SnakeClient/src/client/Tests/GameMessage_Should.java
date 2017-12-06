package client.Tests;

import client.GameMessage;
import client.GameMessageType;
import client.IllegalGameMessageFormatException;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameMessage_Should {
    String content = "con\ntet";

    @Test
    void content_ShouldBeFromSecondLineTillTheEndOfRaw() throws Exception {
        String raw = "REQUEST\n" + content;

        GameMessage gameMessage = new GameMessage(raw);

        assertEquals(content, gameMessage.content);
    }

    @Test
    void _ShouldBeParsedCorrectly() throws IllegalGameMessageFormatException {
        assertGameMessageTypeParsedCorrectly(GameMessageType.GameFinished, "GAME_FINISHED");
    }

    private void assertGameMessageTypeParsedCorrectly(GameMessageType expectedType, String strMessageType) throws IllegalGameMessageFormatException {
        assertEquals(expectedType, new GameMessage(strMessageType + "\n" + content).messageType);
    }
}
