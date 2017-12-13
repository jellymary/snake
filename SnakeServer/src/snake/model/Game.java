package snake.model;

import snake.model.Interfaces.IField;
import snake.model.Interfaces.IFieldObject;
import snake.model.Interfaces.IGame;

import java.util.HashSet;

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
    }

    public Boolean isPlaying() {
        Boolean isPlaying = false;
        for (int i = 0; i < field.getSnakeCount(); i++) {
            isPlaying =  !isPlaying && this.field.getSnakeHead(i).isAlive();
            if (isPlaying && this.field.getSnakeHead(i).isAlive())
                return true;
        }
        return false;
    }

    @Override
    public IField getField() {
        return field;
    }
}