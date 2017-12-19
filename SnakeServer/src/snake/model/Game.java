package snake.model;

import snake.model.FieldObjects.Apple;
import snake.model.Interfaces.IField;
import snake.model.Interfaces.IFieldObject;
import snake.model.Interfaces.IGame;
import snake.model.util.Location;

import java.util.HashSet;
import java.util.Random;

public class Game implements IGame {
    private IField field;

    public Game(IField field)
    {
        this.field = field;
    }

    @Override
    public void tick()
    {
        HashSet<IFieldObject> objectsWhichActed = new HashSet<>();
        for (IFieldObject object : field) {
            if (object != null && !objectsWhichActed.contains(object)) {
                objectsWhichActed.add(object);
                object.act();
            }
        }
        checkAppleAvailability();
    }

    private void checkAppleAvailability() {
        Random random = new Random();
        Boolean appleExists = appleExists();
        while (!appleExists) {
            int x = random.nextInt(field.getWidth());
            int y = random.nextInt(field.getHeight());
            Location location = new Location(x, y);
            if (field.getObjectAt(location) == null) {
                field.addObject(new Apple(location, field, 1));
                appleExists = true;
            }
        }
    }

    private Boolean appleExists() {
        for (IFieldObject object : field) {
            if (object instanceof Apple)
                return true;
        }
        return false;
    }

    public Boolean isPlaying() {
        if (field.getSnakeCount() == 1)
            return field.getSnakeHead(0).isAlive();
        Boolean someoneAlive = false;
        for (int i = 0; i < field.getSnakeCount(); i++) {
            if (someoneAlive && this.field.getSnakeHead(i).isAlive())
                return true;
            someoneAlive = someoneAlive || this.field.getSnakeHead(i).isAlive();
        }
        return false;
    }

    @Override
    public IField getField() {
        return field;
    }
}