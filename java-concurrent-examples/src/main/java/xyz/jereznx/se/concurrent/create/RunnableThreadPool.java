package xyz.jereznx.se.concurrent.create;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author LQL
 * @since Create in 2020/8/17 21:03
 */
public class RunnableThreadPool {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        final ExecutorService executorService = Executors.newFixedThreadPool(1);
        final Future<?> res = executorService.submit(new MyCallableTest.MyCallable());
//        shutdownNow 会立即停止线程池，不管是否有线程任务没执行完
        executorService.shutdownNow();
//        get会阻塞主线程
        System.out.println(res.get());
//        executorService.shutdownNow();
    }

}
