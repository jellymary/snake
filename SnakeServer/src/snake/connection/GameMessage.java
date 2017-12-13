package snake.connection;

public class GameMessage {
    public final Message messageType;
    public final String[] content;

    public GameMessage(String receivedMessage) throws IllegalGameMessageFormatException {
        if (receivedMessage == null)
            throw new IllegalGameMessageFormatException("Received message should not be empty");
        int endOfFirstLine = getEndOfFirstLine(receivedMessage);
        this.messageType = Message.valueOf(receivedMessage.substring(0, endOfFirstLine));
        if (endOfFirstLine != receivedMessage.length())
            this.content = receivedMessage.substring(endOfFirstLine + 1).split("\n");
        else this.content = new String[]{};
    }

    private int getEndOfFirstLine(String receivedMessage) {
        int endOfFirstLine = receivedMessage.indexOf('\n');
        if (endOfFirstLine == -1)
            endOfFirstLine = receivedMessage.length();
        return endOfFirstLine;
    }

    public static String getFullMessage(Message messageType, String[] messageContent){
        return String.join("\n", messageType.toString(), String.join("\n", messageContent));
    }

    public String toString() {
        return String.join("\n", this.messageType.toString(), String.join("\n",this.content));
    }
}
