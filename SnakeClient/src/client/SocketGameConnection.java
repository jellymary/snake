package client;

import client.Exceptions.IllegalGameMessageFormatException;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SocketGameConnection implements Closeable, GameConnection {
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private Socket socket;

    SocketGameConnection(Socket socket) throws IOException {
        this.socket = socket;
        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream(socket.getOutputStream());
    }

    @Override
    public void sendMessage(GameMessage message) throws IOException {
        outputStream.writeUTF(message.toString());
    }

    @Override
    public GameMessage receiveMessage() throws IOException, IllegalGameMessageFormatException {
        return new GameMessage(inputStream.readUTF());
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }
}
