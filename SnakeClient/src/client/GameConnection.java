package client;

import client.Exceptions.IllegalGameMessageFormatException;

import java.io.IOException;

public interface GameConnection {
    void sendMessage(GameMessage message) throws IOException;

    GameMessage receiveMessage() throws IOException, IllegalGameMessageFormatException;
}
