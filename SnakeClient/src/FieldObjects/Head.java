package FieldObjects;

public class Head extends LocatedFieldObject {
    public final boolean you;

    public Head(String owner) {
        you = owner.trim().toLowerCase().equals("you");
    }
}
