package client;

import javafx.scene.input.KeyCode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

class GameConnection {
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private Socket socket;

    GameConnection(Socket socket) throws IOException {
        this.socket = socket;
        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream(socket.getOutputStream());
    }

    void sendRequestToPlay() throws IOException {
        outputStream.writeUTF(GameMessage.NameByMessageType.get(GameMessageType.Request) + "\nAidar\n1\n");
    }

    GameMessage getNextMessage() throws IOException, IllegalGameMessageFormatException {
        String answer = inputStream.readUTF();
        return new GameMessage(answer);
    }

    void sendPlayersAction(KeyCode code) throws IOException {
        String message = "PLAYERS_ACTION\n" + code.getName() + "\n";
        outputStream.writeUTF(message);
    }

    Socket getSocket() {
        return socket;
    }
}
