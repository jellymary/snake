package client.Utils;

public interface ThrowingTripleConsumer<T1, T2, T3> {
    void accept(T1 value1, T2 value2, T3 value3) throws Exception;
}
