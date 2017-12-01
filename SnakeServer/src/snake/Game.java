package snake;

public class Game extends Thread {
    private Player[] PLAYERS;

    public Game(Player[] players) {
        PLAYERS = players;
        setPriority(NORM_PRIORITY);
        setDaemon(true);
        start();
    }

    @Override
    public void run() {
        Level game = new LevelLoader().loadRandomLevel();
        for (Player player : PLAYERS) {
            player.set(game);
            player.send(Message.GAME_IS_READY);
        }
        for (Player player : PLAYERS)
            try {
                player.read(Message.CLIENT_IS_READY);
            } catch (Exception e) {
                e.printStackTrace();
            }

        for (Player player : PLAYERS)
            player.send(Message.GAME_STARTED);

        Thread playerActionThread = new Thread(() -> {
            while (game.state == LevelState.PLAYING)
                for (Player player : PLAYERS) {
                    try {
                        String playerAction = player.read(Message.PLAYER_ACTION);
                        Vector direction = Direction.parse(playerAction);
                        if (!direction.equals(Direction.NONE))
                            game.snake.setDirection(direction);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        });
        playerActionThread.setDaemon(true);
        playerActionThread.start();

        while (game.state == LevelState.PLAYING) {
            for (Player player : PLAYERS)
                player.send(Message.GAME_STATE);
            game.tick();
            try {
                sleep(1000);
            } catch (InterruptedException ignore) {}
        }
        for (Player player : PLAYERS) {
            player.send(Message.GAME_FINISHED);
            player.socketClosing();
        }
    }
}