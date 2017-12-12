package client.Utils;

public class StringLinesQueue implements StringSplitQueue {
    private final String text;
    private int index;
    private String cachedPeek = null;

    public StringLinesQueue(String text) {
        this.text = text;
        this.index = 0;
    }

    @Override
    public String peek() {
        if (cachedPeek != null)
            return cachedPeek;
        if (index >= text.length())
            return null;
        int endOfLine = text.indexOf('\n', index);
        if (endOfLine == -1)
            endOfLine = text.length();
        cachedPeek = text.substring(index, endOfLine);
        index = endOfLine + 1;
        return cachedPeek;
    }

    @Override
    public String pop() {
        String result = peek();
        cachedPeek = null;
        return result;
    }

    @Override
    public boolean empty() {
        return cachedPeek == null && index >= text.length();
    }
}
