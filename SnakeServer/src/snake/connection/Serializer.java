package snake.connection;

public interface Serializer<T> {
    String serializeForPlayer(T object, int playerId);
}
