package client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool {
    private ThreadPool(){};
    private static ExecutorService pool;

    public static ExecutorService get() {
        if (pool != null)
            return pool;
        pool = Executors.newCachedThreadPool();
        return pool;
    }
}
