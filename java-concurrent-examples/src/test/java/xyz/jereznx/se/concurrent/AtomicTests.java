package xyz.jereznx.se.concurrent;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author liqilin
 * @since 2021/2/7 14:36
 */
public class AtomicTests {

    @Test
    public void t() throws InterruptedException {
        int threadNum = 20;
        CountDownLatch countDownLatch = new CountDownLatch(threadNum);
        for (int i = 0; i < threadNum; i++) {
            new Thread(new CountRunnable(countDownLatch)).start();
        }
        countDownLatch.await();
    }

    static class CountRunnable implements Runnable {

        private static volatile int count = 0;

        private CountDownLatch countDownLatch;

        AtomicBoolean atomicBoolean = new AtomicBoolean(false);

        public CountRunnable(CountDownLatch countDownLatch) {
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            while (true) {
                if (atomicBoolean.compareAndSet(false, true)) {
                    System.out.println(++count);
                    System.out.flush();
                    countDownLatch.countDown();
                    atomicBoolean.compareAndSet(true, false);
                    break;
                }
            }
        }
    }

    @Test
    public void t1() {
        Integer a = 1;
        Integer b = 2;
        a = b = 3;
        System.out.println(a);
        System.out.println(b);
    }

}
