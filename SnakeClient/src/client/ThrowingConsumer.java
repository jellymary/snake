package client;

public interface ThrowingConsumer<T>{
    void accept(T value) throws Exception;
}
