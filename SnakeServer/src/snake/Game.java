package snake;

public class Game extends Thread {
    private Player[] PLAYERS;

    public Game(Player... players) {
        PLAYERS = players;
        setPriority(NORM_PRIORITY);
        setDaemon(true);
        start();
    }

    @Override
    public void run() {
        Level game = new LevelLoader().loadRandomLevel();
        int id = 0;
        for (Player player : PLAYERS) {
            player.setID(id++);
            player.setGame(game);
            player.send(Message.GAME_IS_READY);
        }
        for (Player player : PLAYERS)
            try {
                player.read(Message.CLIENT_IS_READY);
            } catch (Exception e) {
                e.printStackTrace();
            }
        Thread[] playersActionThread = new Thread[PLAYERS.length];
        for (Player player : PLAYERS) {
            id = player.getID();
            playersActionThread[id] = new Thread(() -> {
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
            playersActionThread[id].setDaemon(true);
        }
        for (Player player : PLAYERS) {
            player.send(Message.GAME_STARTED);
            playersActionThread[player.getID()].start();
        }

        while (game.state == LevelState.PLAYING) {
            for (Player player : PLAYERS)
                player.send(Message.GAME_STATE);
            game.tick();
            try {
                sleep(1000);
            } catch (InterruptedException ignore) {}
        }
        for (Player player : PLAYERS) {
            player.isWinner = game.state == LevelState.COMPLETED;
            player.send(Message.GAME_FINISHED);
            player.socketClosing();
        }
    }
}