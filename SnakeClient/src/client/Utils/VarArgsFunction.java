package client.Utils;

import FieldObjects.Location;

@FunctionalInterface
public interface VarArgsFunction<T, R> {
    R apply(T... args);
}
