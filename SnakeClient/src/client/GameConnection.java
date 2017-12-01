package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

class GameConnection {
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    GameConnection(Socket socket) throws IOException {
        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream(socket.getOutputStream());
    }

    void sendRequestToPlay() throws IOException {
        outputStream.writeUTF(GameMessage.NameByMessageType.get(GameMessageType.Request));
    }

    GameMessage getNextMessage() throws IOException, IllegalGameMessageFormatException {
        String answer = inputStream.readUTF();
        return new GameMessage(answer);
    }
}
