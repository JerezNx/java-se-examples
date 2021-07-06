package xyz.jereznx.se.concurrent.pool;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author liqilin
 * @since 2021/6/27 13:44
 */
@Slf4j
@SuppressWarnings("ALL")
public class ThreadPoolTest {

    @Test
    public void test() {
        final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 2,
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1));

        Runnable task = () -> {
            log.info("start");
            sleep(30);
            log.info("end");
        };

//        直接创建core线程，执行任务
        threadPoolExecutor.execute(task);
//        添加到缓冲队列
        threadPoolExecutor.execute(task);
//        创建普通线程，执行任务
        threadPoolExecutor.execute(task);
//        拒绝策略，默认抛异常
        threadPoolExecutor.execute(task);

        sleep(666666);
    }

    private static void sleep(int timeout) {
        try {
            TimeUnit.SECONDS.sleep(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
