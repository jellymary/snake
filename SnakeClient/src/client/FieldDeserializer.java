package client;

import FieldObjects.*;
import client.Exceptions.CoordinatesFormatException;
import client.Utils.StringLinesQueue;
import client.Utils.StringSplitQueue;
import client.Utils.VarArgsFunction;

import java.util.*;

public class FieldDeserializer {
    private Map<String, VarArgsFunction<String, FieldObject>> createFieldObjectByName = new HashMap<>();

    public List<FieldObject> parseObjects(String serialization) {
        List<FieldObject> result = new ArrayList<>();
        StringLinesQueue stringLinesQueue = new StringLinesQueue(serialization);
        while (!stringLinesQueue.empty())
            serializeImpl(result, stringLinesQueue);
        return result;
    }

    private void serializeImpl(List<FieldObject> result, StringSplitQueue stringQueue) {
        String name = parseName(stringQueue.pop());
        Collection<String> lines = new LinkedList<>();
        Location location;
        try {
            location = parseLocation(stringQueue.pop());
        } catch (CoordinatesFormatException e) {
            return;
        }
        while (!stringQueue.empty()) {
            String line = stringQueue.pop();
            if (isEndOfObject(line))
                break;
            lines.add(line);
        }

        VarArgsFunction<String, FieldObject> creator = createFieldObjectByName.get(name);
        if (creator == null)
            return;
        FieldObject fieldObject = creator.apply(lines.toArray(new String[lines.size()]));
        fieldObject.setLocation(location);
        result.add(fieldObject);
    }

    private Location parseLocation(String line) throws CoordinatesFormatException {
        String[] words = line.split(" ", 2);
        int x, y;
        try {
            x = Integer.parseInt(words[0]);
            y = Integer.parseInt(words[1]);
        } catch (NumberFormatException e) {
            throw new CoordinatesFormatException(e);
        }
        return new Location(x, y);
    }

    private boolean isEndOfObject(String line) {
        return line.trim().length() == 0;
    }

    private String parseName(String line) {
        String name = line;
        int indexOfDot = line.lastIndexOf('.');
        if (indexOfDot != -1)
            name = line.substring(indexOfDot + 1);
        return name.trim();
    }

    public void registerObjectName(String name, VarArgsFunction<String, FieldObject> creator) {
        createFieldObjectByName.put(name, creator);
    }

    public void registerDefaultObjects() {
        registerObjectName("Apple", (String[] lines) -> new Apple());
        registerObjectName("Gum", (String[] lines) -> new Gum());
        registerObjectName("Mushroom", (String[] lines) -> new Mushroom());
        registerObjectName("Portal", (String[] lines) -> new Portal());
        registerObjectName("SnakeHead", (String[] lines) -> new Head(lines[0])); //TODO
        registerObjectName("SnakeBody", (String[] lines) -> new Body()); //TODO
        registerObjectName("Wall", (String[] lines) -> new Wall());
        registerObjectName("Oracle", (String[] lines) -> new Oracle());
    }
}
