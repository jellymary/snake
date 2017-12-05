package client;

import javafx.scene.Node;

import java.util.*;
import java.util.function.Function;

public class FieldDeserializer {
    private Map<String, Function<Double, Node>> createNodeByName = new HashMap<>();

    public List<Node> parseNodes(String serialization, double size) {
        List<Node> result = new ArrayList<>();
        int index = 0;
        while (index < serialization.length()) {
            index = serializeImpl(result, serialization, index, size);
        }
        return result;
    }

    private int serializeImpl(List<Node> result, String serialization, int index, double size) {
        String name;
        int endOfLine = endOfLineFrom(serialization, index);

        name = serialization.substring(index, endOfLine).trim();
        int indexOfDot = name.lastIndexOf('.');
        if (indexOfDot != -1)
            name = name.substring(indexOfDot + 1);

        index = endOfLine + 1;
        endOfLine = endOfLineFrom(serialization, index);
        String[] splitedCoordinates = serialization.substring(index, endOfLine).split("\\s+", 2);
        endOfLine = serialization.indexOf('\n', index);
        String coordinates = serialization.substring(index, endOfLine);
        index = endOfLine + 1;

        int x = Integer.parseInt(splitedCoordinates[0]);
        int y = Integer.parseInt(splitedCoordinates[1]);

        Node node = createNodeByName.get(name).apply(size);
        node.setTranslateX(x * size);
        node.setTranslateY(y * size);
        result.add(node);
        return index;
    }

    private static int endOfLineFrom(String string, int index) {
        int result = string.indexOf('\n', index);
        if (result == -1)
            return string.length();
        return result;
    }

    public void registerVisualization(String name, Function<Double, Node> visualizer) {
        createNodeByName.put(name, visualizer);
    }
}
