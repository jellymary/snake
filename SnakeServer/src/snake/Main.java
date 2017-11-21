package snake;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException
    {
        ServerSocket server = new ServerSocket(8080);
        System.out.println("Server is started");
        HashMap<Integer, ArrayList<Socket>> sockets = new HashMap<>();

        while(true) {
            boolean isWaiting = true;
            Socket currentSocket = server.accept();
            System.out.println(new Formatter().format("Client %s accepted", currentSocket.getInetAddress()));
            DataOutputStream out = new DataOutputStream(currentSocket.getOutputStream());
            DataInputStream in = new DataInputStream(currentSocket.getInputStream());

            int playersCount = in.readInt();
            if (playersCount == 1) {
                new Game(new Socket[]{currentSocket});
                continue;
            }
            if (sockets.containsKey(playersCount)) {
                ArrayList<Socket> players = sockets.get(playersCount);
                players.add(currentSocket);
                if (players.size() == playersCount) {
                    new Game((Socket[]) players.toArray());
                    players.clear();
                    isWaiting = false;
                }
            }
            else sockets.put(playersCount, new ArrayList<>(Arrays.asList(currentSocket)));
             if (isWaiting)
                 out.writeUTF("Wait for other players");
        }
    }
}
