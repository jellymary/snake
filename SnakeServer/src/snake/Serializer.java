package snake;

public interface Serializer<T> {
    String serializeForPlayer(T object, int playerId);
}
