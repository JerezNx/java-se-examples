package xyz.jereznx.se.concurrent.pool;

import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Test;

import java.util.concurrent.*;

/**
 * @author liqilin
 * @since 2021/3/13 13:23
 */
@SuppressWarnings("AlibabaThreadPoolCreation")
@Slf4j
public class ScheduledExecutorServiceTest {

    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);

    @After
    public void awaitTermination() throws InterruptedException {
        scheduledExecutorService.awaitTermination(1, TimeUnit.MINUTES);
    }

    /**
     * 延迟一定时间执行任务，只会执行1次
     */
    @Test
    public void scheduleRunnable() {
        scheduledExecutorService.schedule(() -> {
            log.info("running");
        }, 5, TimeUnit.SECONDS);
    }

    @Test
    public void scheduleCallable() throws InterruptedException, ExecutionException {
        final ScheduledFuture<String> future = scheduledExecutorService.schedule(() -> {
            log.info("running");
            TimeUnit.SECONDS.sleep(5);
            return "success";
        }, 5, TimeUnit.SECONDS);
        long delay;
//        ScheduledFuture 除了普通的future的功能外，还可以获取 当前任务还有多久会执行
        while ((delay = future.getDelay(TimeUnit.SECONDS)) > 0) {
            log.info("{}", delay);
            TimeUnit.SECONDS.sleep(1);
        }
        log.info(future.get());
    }

    /**
     * 第二个参数 initialDelay， 是延迟多久开始第一次执行任务
     * 第三个参数 period， 是后续每隔多久执行1次(从第一次执行时间开始算，不管任务执行耗时)
     * <p>
     * 执行日志：
     * 2021-03-13 13:58:08.407 |-INFO  [main]  -|scheduleAtFixedRate start
     * 2021-03-13 13:58:13.407 |-INFO  [pool-1-thread-1]  -|running
     * 2021-03-13 13:58:16.406 |-INFO  [pool-1-thread-1]  -|running
     * 2021-03-13 13:58:19.405 |-INFO  [pool-1-thread-2]  -|running
     * 2021-03-13 13:58:22.406 |-INFO  [pool-1-thread-1]  -|running
     * 2021-03-13 13:58:25.406 |-INFO  [pool-1-thread-1]  -|running
     * <p>
     * 下一次执行开始时间 = 上次开始时间 + period
     */
    @Test
    public void scheduleAtFixedRateSimple() {
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            log.info("running");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, 5, 3, TimeUnit.SECONDS);
        log.info("scheduleAtFixedRate start");
    }

    /**
     * 虽然上面说 scheduleAtFixedRate 不计算任务执行耗时
     * 但是， 如果任务执行的时间比 定期执行的间隔久
     * 虽然等过了period的时间后，触发了下一次任务的开始，但由于上个任务还在执行，所以会排进队列
     * 等上次任务执行完成后，再立即从队列中取出，开始执行
     * <p>
     * ScheduledExecutorService 是一个任务分配1个线程，并不会给其另外开线程同时执行
     */
    @Test
    public void scheduleAtFixedRateC() {
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            log.info("running start");
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("running end");
        }, 5, 3, TimeUnit.SECONDS);
        log.info("scheduleAtFixedRate start");
    }

    /**
     * 如果提交多个任务，则每个任务分配1个线程进行处理
     * 如果线程不够，就会导致共用1个线程，最终不按我们预期的时间执行
     */
    @Test
    public void scheduleAtFixedRateC2() {
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            log.info("running start 1");
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("running end 1");
        }, 5, 3, TimeUnit.SECONDS);
        log.info("scheduleAtFixedRate start 1");

        scheduledExecutorService.scheduleAtFixedRate(() -> {
            log.info("running start 2");
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("running end 2");
        }, 5, 3, TimeUnit.SECONDS);
        log.info("scheduleAtFixedRate start 2");
    }

    /**
     * 第二个参数 initialDelay， 是延迟多久开始第一次执行任务
     * 第三个参数 period， 是下一个任务在上个任务执行完成后等多久再执行下一次(需要等任务执行耗时)
     * <p>
     * 执行日志：
     * 2021-03-13 14:01:30.441 |-INFO  [main]  -|scheduleWithFixedDelay start
     * 2021-03-13 14:01:35.440 |-INFO  [pool-1-thread-1]  -|running
     * 2021-03-13 14:01:39.441 |-INFO  [pool-1-thread-1]  -|running
     * 2021-03-13 14:01:43.441 |-INFO  [pool-1-thread-2]  -|running
     * 2021-03-13 14:01:47.442 |-INFO  [pool-1-thread-1]  -|running
     * 2021-03-13 14:01:51.444 |-INFO  [pool-1-thread-1]  -|running
     * 2021-03-13 14:01:55.445 |-INFO  [pool-1-thread-1]  -|running
     * <p>
     * 下次任务执行开始时间 = 上次任务开始执行时间 + 上次任务执行耗时 + delay
     * 或者说：
     * 下次任务执行开始时间 = 上次任务结束执行时间 + delay
     */
    @Test
    public void scheduleWithFixedDelaySimple() {
        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            log.info("running");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, 5, 3, TimeUnit.SECONDS);
        log.info("scheduleWithFixedDelay start");
    }

    /**
     * 由于 scheduleWithFixedDelay 需要等上次任务执行完成，才会计时
     * 所以任务执行多久无所谓
     */
    @Test
    public void scheduleWithFixedDelayC() {
        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            log.info("running start");
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("running end");
        }, 5, 2, TimeUnit.SECONDS);
        log.info("scheduleWithFixedDelay start");
    }

}
