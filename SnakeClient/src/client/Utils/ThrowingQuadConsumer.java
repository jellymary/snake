package client.Utils;

public interface ThrowingQuadConsumer<T1, T2, T3, T4> {
    void accept(T1 value1, T2 value2, T3 value3, T4 value4) throws Exception;
}
