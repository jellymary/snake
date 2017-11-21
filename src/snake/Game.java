package snake;

import java.net.Socket;

public class Game extends Thread {
    private Player player;

    public Game(Socket[] playerSockets) {
        this.player = new Player(playerSockets[0]);
        setPriority(NORM_PRIORITY);
        setDaemon(true);
        start();
    }

    @Override
    public void run() {
        player.send("Welcome to Snake game");
        LevelLoader loader = new LevelLoader();
        int levelNumber = 1;
        while (true) {
            Level level = loader.load(levelNumber);
            if (level == null) {
                //player.send("Game over");
                break;
            }
            while (level.state == LevelState.PLAYING) {
                player.sendMap(level);
                String eventKey = player.catchEventKey();

                Vector direction = Direction.parse(eventKey);
                if (!direction.equals(Direction.NONE))
                    level.snake.setDirection(direction);
                level.tick();
                try {
                    sleep(1000);
                } catch (InterruptedException ignore) {}
            }
            if (level.state == LevelState.COMPLETED)
                ++levelNumber;
            else if (level.state == LevelState.FAILED)
                player.send("Level failed");
                break;
        }
        player.gameClosing();
    }
}
