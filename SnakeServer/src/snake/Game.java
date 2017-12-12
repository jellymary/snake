package snake;

public class Game extends Thread {
    private Player[] players;
    private Level game;
    private int delay = 1000;

    public Game(Player... players) {
        this.players = players;
        setPriority(NORM_PRIORITY);
        setDaemon(true);
        start();
    }

    @Override
    public void run() {
        this.game = new LevelLoader().loadRandomLevel();
        sendGameReadinessMessage();
        readClientsReadinessMessage();
        createPlayerActionThreads();
        sendGameStartingMessage();

        while (game.state == LevelState.PLAYING) {
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
                while (game.state == LevelState.PLAYING)
                    try {
                        String playerAction = player.read(Message.PLAYER_ACTION);
                        Vector direction = Direction.parse(playerAction);
                        if (!direction.equals(Direction.NONE))
                            game.snake.setDirection(direction);
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
            player.isWinner = game.state == LevelState.COMPLETED;
            player.send(Message.GAME_FINISHED);
            player.socketClosing();
        }
    }
}