package xyz.jereznx.se.concurrent.create;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author LQL
 * @since Create in 2020/8/17 21:03
 */
public class CallableThreadPool {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        final ExecutorService executorService = Executors.newSingleThreadExecutor();
        final Future<?> res = executorService.submit(new MyRunnableTest.MyRunnable());
//        如果还有线程在执行，shutdown会阻塞
        executorService.shutdown();
//        runnable么得返回值
        System.out.println(res.get());
    }

}
