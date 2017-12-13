package snake.connection;

import snake.model.FieldObjects.SnakeHead;
import snake.model.Game;
import snake.model.Interfaces.IFieldObject;
import snake.model.util.Location;

public class LevelSerializer implements Serializer<Game>{
    private static final String OBJECT_SEPARATOR = "\n";

    @Override
    public String serializeForPlayer(Game game, int playerId) {
        StringBuilder sb = new StringBuilder();

        for (int x = 0; x < game.getField().getWidth(); x++)
            for (int y = 0; y < game.getField().getHeight(); y++) {
                IFieldObject object = game.getField().getObjectAt(x, y);
                if (object instanceof SnakeHead)
                    serializeHead(sb, (SnakeHead) object, playerId);
                else
                if (object != null)
                    serializeFieldObject(sb, object);
            }
        return sb.toString();
    }

    private void serializeHead(StringBuilder sb, SnakeHead object, int playerId) {
        String name = object.getClass().getName();
        sb.append(name).append('\n');
        serializeLocation(sb, object.getLocation());
        if (object.getID() == playerId)
            sb.append("YOU\n");
        else
            sb.append(object.getID()).append("\n");
        sb.append(OBJECT_SEPARATOR);
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
