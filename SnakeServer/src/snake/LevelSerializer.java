package snake;

public class LevelSerializer implements Serializer<Level>{
    private static final String OBJECT_SEPARATOR = "\n";

    @Override
    public String serializeForPlayer(Level level, int playerId) {
        StringBuilder sb = new StringBuilder();

        sb.append("Head").append("\n");
        serializeVector(sb, level.snake.getHeadLocation());
        if (playerId == playerId)
            sb.append("YOU\n");//TODO change to actual id
        else
            sb.append(playerId).append("\n");//TODO change to actual id
        sb.append(OBJECT_SEPARATOR);

        Vector[] body = level.snake.getTrace();
        for (int i = 1; i < body.length; i++) {
            sb.append("Body").append("\n");
            serializeVector(sb, body[i]);
            sb.append(OBJECT_SEPARATOR);
        }

        for (int x = 0; x < level.map.getSize().x; x++)
            for (int y = 0; y < level.map.getSize().y; y++) {
                Vector vector = new Vector(x, y);
                MapObject object = level.map.get(vector);
                if (object != null)
                    serializeMapObject(sb, object);
            }
        return sb.toString();
    }

    private void serializeMapObject(StringBuilder sb, MapObject object) {
        String name = object.getClass().getName();
        sb.append(name).append('\n');
        serializeVector(sb, object.getLocation());
        sb.append(OBJECT_SEPARATOR);
    }

    private void serializeVector(StringBuilder sb, Vector vector) {
        sb.append(vector.x).append(' ').append(vector.y).append('\n');
    }
}
