package xyz.jereznx.se.concurrent.create;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * @author LQL
 * @since Create in 2020/8/17 20:57
 */
public class MyCallableTest {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        final FutureTask<String> futureTask = new FutureTask<>(new MyCallable());
        new Thread(futureTask).start();
        System.out.println(futureTask.get());
        System.out.println("main");
    }

    static class MyCallable implements Callable<String> {
        @Override
        public String call() throws Exception {
            System.out.println("run start");
            Thread.sleep(1000);
            System.out.println("run end");
            return "res";
        }
    }
}
