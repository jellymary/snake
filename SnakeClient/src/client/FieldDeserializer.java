package client;

import javafx.scene.Node;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class FieldDeserializer {
    private Map<String, Function<Double, Node>> createNodeByName = new HashMap<>();

    public Node[] parseNodes(String serialization, double size) {
        List<Node> result = new LinkedList<>();
        int index = 0;
        while (index < serialization.length()) {
            index = serializeImpl(result, serialization, index, size);
        }
        return (Node[]) result.toArray();
    }

    private int serializeImpl(List<Node> result, String serialization, int index, double size) {
        String name;
        int endOfLine = serialization.indexOf('\n', index);
        name = serialization.substring(index, endOfLine);
        index = endOfLine + 1;
        endOfLine = serialization.indexOf('\n', index);
        String coordinates = serialization.substring(index, endOfLine);
        index = endOfLine + 1;

        int x, y;
        String[] splitedCoordinates = coordinates.split(" ");
        x = Integer.parseInt(splitedCoordinates[0]);
        y = Integer.parseInt(splitedCoordinates[1]);

        Node node = createNodeByName.get(name).apply(size);
        node.setTranslateX(x * size);
        node.setTranslateY(y * size);
        result.add(node);
        return index;
    }

    public void registerVisualization(String name, Function<Double, Node> visualizer) {
        createNodeByName.put(name, visualizer);
    }
}
