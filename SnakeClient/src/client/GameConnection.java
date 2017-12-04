package client;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

class GameConnection implements Closeable {
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private Socket socket;

    GameConnection(Socket socket) throws IOException {
        this.socket = socket;
        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream(socket.getOutputStream());
    }

    public void sendMessage(GameMessage message) throws IOException {
        outputStream.writeUTF(message.toString());
    }

    public GameMessage receiveMessage() throws IOException, IllegalGameMessageFormatException {
        return new GameMessage(inputStream.readUTF());
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }
}
