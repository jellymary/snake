package FieldObjects;

public class Location {
    public final int x, y;
    public static final Location Empty = new Location(0, 0);

    public Location(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
