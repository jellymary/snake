package snake.connectionTests;


import org.junit.Test;
import snake.GameMessage;
import snake.IllegalGameMessageFormatException;
import snake.Message;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class GameMessageTests {
    @Test
    public void correctOneLineMessageToRead() throws IllegalGameMessageFormatException {
        GameMessage message = new GameMessage("GAME_STARTED");
        assertEquals(Message.GAME_STARTED, message.messageType);
        assertArrayEquals(new String[]{}, message.content);
    }

    @Test
    public void correctMultiLineMessageToRead() throws IllegalGameMessageFormatException {
        GameMessage message = new GameMessage("GAME_IS_READY\n10\n15");
        assertEquals(Message.GAME_IS_READY, message.messageType);
        assertArrayEquals(new String[]{"10", "15"}, message.content);
    }
}
