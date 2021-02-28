package xyz.jereznx.se.concurrent.aqs;

import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author LQL
 * @since Create in 2021/2/18 14:29
 */
public class AqsWithReentrantLock {

    @Test
    public void test() throws InterruptedException {
        ReentrantLock lock = new ReentrantLock();
        Thread[] threads = new Thread[2];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                try {
                    lock.lock();
                    System.out.println(Thread.currentThread().getName());
                    TimeUnit.SECONDS.sleep(5);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            });
            threads[i].setName("t" + i);
        }

        threads[0].start();
//        threads[1].start();

        for (Thread thread : threads) {
            thread.join();
        }
//        while (true) {
//
//        }
    }

    public static void main(String[] args) throws InterruptedException {
        new AqsWithReentrantLock().test();

    }

}
