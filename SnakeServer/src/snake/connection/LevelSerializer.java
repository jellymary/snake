package snake.connection;

import snake.model.Game;
import snake.model.Interfaces.IFieldObject;
import snake.model.util.Location;

public class LevelSerializer implements Serializer<Game>{
    private static final String OBJECT_SEPARATOR = "\n";

    @Override
    public String serializeForPlayer(Game game, int playerId) {
        StringBuilder sb = new StringBuilder();

        sb.append("Head").append("\n");
        serializeLocation(sb, game.getField().getSnakeHead().getLocation());
        sb.append(OBJECT_SEPARATOR);


//        Vector[] body = game.snake.getTrace();
//        for (int i = 1; i < body.length; i++) {
//            sb.append("Body").append("\n");
//            serializeLocation(sb, body[i]);
//            sb.append(OBJECT_SEPARATOR);
//        }

        for (int x = 0; x < game.getField().getWidth(); x++)
            for (int y = 0; y < game.getField().getHeight(); y++) {
                IFieldObject object = game.getField().getObjectAt(x, y);
                if (object != null)
                    serializeFieldObject(sb, object);
            }
        return sb.toString();
    }

    private void serializeFieldObject(StringBuilder sb, IFieldObject object) {
        String name = object.getClass().getName();
        sb.append(name).append('\n');
        serializeLocation(sb, object.getLocation());
        sb.append(OBJECT_SEPARATOR);
    }

    private void serializeLocation(StringBuilder sb, Location location) {
        sb.append(location.getX()).append(' ').append(location.getY()).append('\n');
    }
}
