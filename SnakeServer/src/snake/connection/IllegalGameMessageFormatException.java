package snake.connection;

public class IllegalGameMessageFormatException extends Exception {
    public IllegalGameMessageFormatException(String message){
        super(message);
    }
}