package snake.connection;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException, IllegalGameMessageFormatException {
        ServerSocket server = new ServerSocket(8080);
        System.out.println("Server is started");
        HashMap<Integer, ArrayList<Player>> sockets = new HashMap<>();

        while(true) {
            Player currentPlayer = new Player(server.accept());
            int playersCount = Integer.parseInt(currentPlayer.read(Message.REQUEST));
            System.out.println(String.format("Client %s accepted", currentPlayer));
            if (playersCount == 1) {
                new GameThread(currentPlayer);
                continue;
            }
            if (sockets.containsKey(playersCount)) {
                ArrayList<Player> players = sockets.get(playersCount);
                players.add(currentPlayer);
                if (players.size() == playersCount) {
                    new GameThread(players.toArray(new Player[players.size()]));
                    players.clear();
                }
            }
            else sockets.put(playersCount, new ArrayList<>(Arrays.asList(currentPlayer)));
        }
    }
}
