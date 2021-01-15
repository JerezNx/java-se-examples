package xyz.jereznx.se.concurrent.future;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author LQL
 * @since Create in 2020/8/17 21:29
 */
public class FutureTaskTest {

    public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {
        final FutureTask<String> futureTask = new FutureTask<String>(() -> {
            System.out.println("start");
            Thread.sleep(5000);
            System.out.println("end");
            return "res";
        });
        new Thread(futureTask).start();
//        System.out.println(futureTask.get());
//        System.out.println(futureTask.get(4, TimeUnit.SECONDS));
//        System.out.println(futureTask.isDone());
//        System.out.println(futureTask.cancel(true));
        Thread.sleep(1000);
        System.out.println(futureTask.cancel(false));
        System.out.println(futureTask.isCancelled());
    }

}
