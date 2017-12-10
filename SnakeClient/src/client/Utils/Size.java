package client.Utils;

public class Size {
    public final int width;
    public final int height;

    public Size(int width, int height) {
        if (width < 1 || height < 1)
            throw new IllegalArgumentException("Width and height can't be zero or negative");
        this.width = width;
        this.height = height;
    }
}
