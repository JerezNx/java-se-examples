package xyz.jereznx.se.concurrent;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author LQL
 * @since Create in 2020/8/31 23:44
 */
public class ReentrantLockTest {

    public static void main(String[] args) throws Exception {
        ReentrantLock lock = new ReentrantLock();

        new Thread(() -> {
            final int n = 3;
            for (int i = 1; i <= n; i++) {
                lock.lock();
            }

            for (int i = 1; i <= n-1; i++) {
                try {
                    System.out.println(i);
                } finally {
                    lock.unlock();
                }
            }
        }).start();

        new Thread(() -> {
            lock.lock();
            System.out.println("end");
            lock.unlock();
        }).start();
    }

}
