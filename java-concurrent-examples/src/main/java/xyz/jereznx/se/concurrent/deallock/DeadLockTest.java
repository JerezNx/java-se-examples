package xyz.jereznx.se.concurrent.deallock;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author liqilin
 * @since 2021/7/4 11:08
 */
@Slf4j
@SuppressWarnings("ALL")
public class DeadLockTest {

    private Object lock1 = new Object();
    private Object lock2 = new Object();

    private Lock lockA = new ReentrantLock();
    private Lock lockB = new ReentrantLock();

    /**
     * synchronized 引起的死锁
     * Jconsole 和 JVisual 都能检测到
     *
     * JConsole里 线程状态是 BLOCKED
     * JVisual里 是 监视
     *
     * "t2":
     *   waiting to lock monitor 0x000000001812f0c8 (object 0x00000000d7da0178, a java.lang.Object),
     *   which is held by "t1"
     * "t1":
     *   waiting to lock monitor 0x000000001b2b6808 (object 0x00000000d7da0188, a java.lang.Object),
     *   which is held by "t2"
     */
    @Test
    public void t1() throws InterruptedException {
        final Thread t1 = new Thread(() -> {
            synchronized (lock1) {
                log.info("get lock 1");
                sleep(1);
                synchronized (lock2) {
                    sleep(1);
                    log.info("finish");
                }
            }
        }, "t1");
        final Thread t2 = new Thread(() -> {
            synchronized (lock2) {
                log.info("get lock 2");
                sleep(1);
                synchronized (lock1) {
                    sleep(1);
                    log.info("finish");
                }
            }
        }, "t2");
        t1.start();
        t2.start();
        t1.join();
    }

    /**
     * Lock 引起的死锁
     * Jconsole 和 JVisual 都能检测到
     *
     *  JConsole里 线程状态是 WAITING
     *  JVisual里 是 驻留
     *
     * "t2":
     *   waiting for ownable synchronizer 0x00000000d7da03e8, (a java.util.concurrent.locks.ReentrantLock$NonfairSync),
     *   which is held by "t1"
     * "t1":
     *   waiting for ownable synchronizer 0x00000000d7da0600, (a java.util.concurrent.locks.ReentrantLock$NonfairSync),
     *   which is held by "t2"
     */
    @Test
    public void t2() throws InterruptedException {
        final Thread t1 = new Thread(() -> {
            lockA.lock();
            log.info("get lock A");
            sleep(1);
            lockB.lock();
            sleep(1);
            log.info("finish");
            lockB.unlock();
            lockA.unlock();
        }, "t1");
        final Thread t2 = new Thread(() -> {
            lockB.lock();
            log.info("get lock B");
            sleep(1);
            lockA.lock();
            sleep(1);
            log.info("finish");
            lockA.unlock();
            lockB.unlock();
        }, "t2");
        t1.start();
        t2.start();
        t1.join();
    }

    /**
     * 这种wait的是无法检测的
     *
     * JConsole里 线程状态是 WAITING
     * JVisual里 是 等待
     */
    @Test
    public void t3() throws InterruptedException {
        final Thread t1 = new Thread(() -> {
            synchronized (lock1) {
                log.info("get lock 1");
                sleep(1);
                try {
                    lock1.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (lock2) {
                    sleep(1);
                    lock2.notifyAll();
                    log.info("finish");
                }
            }
        }, "t1");
        final Thread t2 = new Thread(() -> {
            synchronized (lock2) {
                log.info("get lock 2");
                sleep(1);
                try {
                    lock2.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (lock1) {
                    sleep(1);
                    lock1.notifyAll();
                    log.info("finish");
                }
            }
        }, "t2");
        t1.start();
        t2.start();
        t1.join();
    }

    private static void sleep(int timeout) {
        try {
            TimeUnit.SECONDS.sleep(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
