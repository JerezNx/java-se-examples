package xyz.jereznx.se.concurrent.aqs;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author liqilin
 * @since 2021/2/18 16:31
 */
@Slf4j
public class AqsOfCountDownLatchTest {

    @SuppressWarnings("AlibabaAvoidManuallyCreateThread")
    @Test
    public void test() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(2);
        Thread[] threads = new Thread[2];
        for (int i = 0; i < threads.length; i++) {
            int finalI = i;
            threads[i] = new Thread(() -> {
                try {
                    TimeUnit.SECONDS.sleep(2 * finalI);
                } catch (Exception e) {
                    log.error("", e);
                } finally {
                    countDownLatch.countDown();
                    log.info("countDown");
                }
            });
            threads[i].setName("t" + i);
            threads[i].start();
        }
        countDownLatch.await();
        log.info("finish");
    }

    @SuppressWarnings("AlibabaAvoidManuallyCreateThread")
    @Test
    public void test1() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(2);
        Thread[] threads = new Thread[2];
        for (int i = 0; i < threads.length; i++) {
            int finalI = i;
            threads[i] = new Thread(() -> {
                try {
                    TimeUnit.SECONDS.sleep(20 * finalI);
                } catch (Exception e) {
                    log.error("", e);
                } finally {
                    countDownLatch.countDown();
                    log.info("countDown");
                }
            });
            threads[i].setName("t" + i);
            threads[i].start();
        }
        countDownLatch.await(10,TimeUnit.SECONDS);
        log.info("finish");
    }
}
