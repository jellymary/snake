package FieldObjects;

public abstract class LocatedFieldObject implements FieldObject {
    private Location location;

    public LocatedFieldObject(Location location) {
        this.location = location;
    }

    public LocatedFieldObject() {
        this.location = Location.Empty;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public void setLocation(Location location) {
        this.location = location;
    }
}
