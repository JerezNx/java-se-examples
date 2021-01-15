package xyz.jereznx.se.concurrent.pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.locks.Lock;

/**
 * @author LQL
 * @since Create in 2020/8/17 23:04
 */
public class FixedThreadPoolTest {

    public static void main(String[] args) {

        final ExecutorService executorService = Executors.newFixedThreadPool(1);
        final ExecutorService executorService2 = Executors.newSingleThreadExecutor();
        final ExecutorService executorService1 = Executors.newCachedThreadPool();
        final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
    }

}
