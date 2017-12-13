package snake.connection;

import snake.model.Game;
import snake.model.FieldGenerators;
import snake.model.util.Vector;

public class GameThread extends Thread {
    private Player[] players;
    private Game game;
    private int delay = 1000;

    public GameThread(Player... players) {
        this.players = players;
        setPriority(NORM_PRIORITY);
        setDaemon(true);
        start();
    }

    @Override
    public void run() {
        this.game = new Game(FieldGenerators.genBoardedField(10, 10, players.length));
        sendGameReadinessMessage();
        readClientsReadinessMessage();
        createPlayerActionThreads();
        sendGameStartingMessage();

        while (game.isPlaying()) {
            sendGameStateMessage();
            game.tick();
            try {
                sleep(delay);
            } catch (InterruptedException ignore) {}
        }
        sendGameEndingMessage();
    }

    private void createPlayerActionThreads() {
        for (Player player : players) {
            player.actionThread = new Thread(() -> {
                while (game.isPlaying())
                    try {
                        String playerAction = player.read(Message.PLAYER_ACTION);
                        Vector direction = (Vector)Vector.class.getField(playerAction).get(Vector.ZERO);
                        game.getField().getSnakeHead(player.ID).setDirection(direction);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            });
            player.actionThread.setDaemon(true);
        }
    }

    private void sendGameStateMessage() {
        for (Player player : players)
        player.send(Message.GAME_STATE);
    }

    private void sendGameReadinessMessage() {
        int id = 0;
        for (Player player : players) {
            player.ID = id++;
            player.setGame(game);
            player.send(Message.GAME_IS_READY);
        }
    }

    private void readClientsReadinessMessage() {
        for (Player player : players)
            try {
                player.read(Message.CLIENT_IS_READY);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    private void sendGameStartingMessage() {
        for (Player player : players) {
            player.send(Message.GAME_STARTED);
            player.actionThread.start();
        }
    }

    private void sendGameEndingMessage() {
        for (Player player : players) {
            player.isWinner = game.getField().getSnakeHead(player.ID).isAlive();
            player.send(Message.GAME_FINISHED);
            player.socketClosing();
        }
    }
}