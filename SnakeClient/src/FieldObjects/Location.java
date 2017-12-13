package FieldObjects;

public class Location {
    public final int x, y;
    public static final Location Empty = new Location(0, 0);

    public Location(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int hashCode() {
        return x * 12843234 + y;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof  Location))
            return false;
        Location loc = (Location) object;
        return loc.x == x && loc.y == y;
    }
}
