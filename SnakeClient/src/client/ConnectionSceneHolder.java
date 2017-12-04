package client;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class ConnectionSceneHolder implements SceneHolder {
    private final Scene scene;
    private final TextField portField = new TextField();
    private final TextField serverAddressField = new TextField();
    private final Text statusText = new Text();
    private final Service<Void> connectionEstablisher;
    private final TextField nameField = new TextField();
    private final TextField desiredPlayersNumberField = new TextField();

    ConnectionSceneHolder(ThrowingTripleConsumer<Socket, String, Integer> socketUser) {
        GridPane root = new GridPane();
        root.setAlignment(Pos.CENTER);

        root.add(new Text("Nickname and desired players number:"), 0, 0);
        root.add(nameField, 1, 0);
        root.add(desiredPlayersNumberField, 2, 0);
        root.add(new Text("Server address and port:"), 0, 1);
        root.add(serverAddressField, 1, 1);
        portField.setPrefWidth(60);
        root.add(portField, 2, 1);
        Button connectButton = new Button("Connect");
        root.add(connectButton, 3, 1);
        root.add(statusText, 1, 2, 3, 1);

        scene = new Scene(root);

        connectionEstablisher = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        Platform.runLater(() -> PrintProcessState("Connecting..."));

                        String serverAddress = serverAddressField.textProperty().getValueSafe();
                        String name = nameField.textProperty().getValueSafe();
                        int port;
                        int desiredPlayersNumber;
                        try {
                            port = Integer.parseInt(portField.textProperty().getValueSafe());
                        }
                        catch (NumberFormatException e)
                        {
                            Platform.runLater(() -> PrintError("Port should be integer"));
                            return null;
                        }

                        try {
                            desiredPlayersNumber = Integer.parseInt(desiredPlayersNumberField.textProperty().getValueSafe());
                        }
                        catch (NumberFormatException e)
                        {
                            Platform.runLater(() -> PrintError("Desired players number should be integer"));
                            return null;
                        }
                        if (desiredPlayersNumber < 1)
                        {
                            Platform.runLater(() -> PrintError("Desired players number should be one or greater"));
                            return null;
                        }

                        Socket socket;
                        try {
                            socket = new Socket(serverAddress, port);
                        }
                        catch (UnknownHostException e)
                        {
                            Platform.runLater(() -> PrintError("Unknown host"));
                            return null;
                        }
                        catch (IOException e)
                        {
                            Platform.runLater(() -> PrintError("Unable to connect"));
                            return null;
                        }

                        socket.close();
                        Platform.runLater(() -> PrintProcessState("Connected!"));

                        try {
                            socketUser.accept(socket, name, desiredPlayersNumber);
                        }
                        catch (GameUnavailableException e)
                        {
                            Platform.runLater(() -> PrintError("Server responded game not available"));
                        }

                        return null;
                    }
                };
            }
        };

        connectButton.setOnAction(this::connect);
        root.disableProperty().bind(connectionEstablisher.runningProperty());
    }

    private void connect(ActionEvent actionEvent) {
        connectionEstablisher.reset();
        connectionEstablisher.start();
        actionEvent.consume();
    }

    private void PrintProcessState(String message) {
        statusText.setText(message);
        statusText.setFill(Color.GREEN);
    }

    private void PrintError(String message) {
        statusText.setText(message);
        statusText.setFill(Color.RED);
    }

    @Override
    public Scene getScene() {
        return scene;
    }
}
