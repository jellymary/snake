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

    ConnectionSceneHolder(ThrowingConsumer<Socket> socketUser) {
        GridPane root = new GridPane();
        root.setAlignment(Pos.CENTER);

        root.add(new Text("Server address and port:"), 0, 0);
        root.add(serverAddressField, 1, 0);
        portField.setPrefWidth(60);
        root.add(portField, 2, 0);
        Button connectButton = new Button("Connect");
        root.add(connectButton, 3, 0);
        root.add(statusText, 1, 1, 3, 1);

        scene = new Scene(root);

        connectionEstablisher = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        Platform.runLater(() -> PrintProcessState("Connecting..."));

                        String serverAddress = serverAddressField.textProperty().getValueSafe();
                        int port;
                        try {
                            port = Integer.parseInt(portField.textProperty().getValueSafe());
                        }
                        catch (NumberFormatException e)
                        {
                            Platform.runLater(() -> PrintError("Port should be integer"));
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
                            socketUser.accept(socket);
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
