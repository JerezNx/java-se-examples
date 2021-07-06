package xyz.jereznx.se.concurrent.aqs;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author liqilin
 * @since 2021/2/18 14:29
 */
@SuppressWarnings("ALL")
@Slf4j
public class AqsOfReentrantLockTest {

    @SuppressWarnings("AlibabaAvoidManuallyCreateThread")
    @Test
    public void test() throws InterruptedException {
        ReentrantLock lock = new ReentrantLock(true);
        Thread[] threads = new Thread[2];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                lock.lock();
                try {
                    log.info(Thread.currentThread().getName() + " lock");
                    TimeUnit.SECONDS.sleep(5);
                } catch (Exception e) {
                    log.error("", e);
                } finally {
                    lock.unlock();
                    log.info(Thread.currentThread().getName() + " unlock");
                }
            });
            threads[i].setName("t" + i);
        }

        threads[0].start();
        TimeUnit.SECONDS.sleep(1);
        threads[1].start();
        TimeUnit.SECONDS.sleep(1);
//        threads[1].interrupt();
        for (Thread thread : threads) {
            thread.join();
        }
    }

    @SuppressWarnings("AlibabaAvoidManuallyCreateThread")
    @Test
    public void test1() throws InterruptedException {
        MyReentrantLock lock = new MyReentrantLock();
        Thread[] threads = new Thread[2];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                lock.lock();
                try {
                    log.info(Thread.currentThread().getName() + " lock");
                    TimeUnit.SECONDS.sleep(5);
                } catch (Exception e) {
                    log.error("", e);
                } finally {
                    lock.unlock();
                    log.info(Thread.currentThread().getName() + " unlock");
                }
            });
            threads[i].setName("t" + i);
        }

        threads[0].start();
        TimeUnit.SECONDS.sleep(1);
        threads[1].start();
        TimeUnit.SECONDS.sleep(1);
        MyReentrantLock.exceptionSwitch = true;
        threads[1].interrupt();
        for (Thread thread : threads) {
            thread.join();
        }
    }

    /**
     * Condition 必须在 lock 和 unlock 之间调用
     * condition.await() 表示当前线程t0释放锁、阻塞自己，
     * 直到其他线程t1通过 condition.signal()/ condition.signalAll() 唤醒当前线程
     * 但此时t1需要重新争抢锁，才能继续执行
     * <p>
     * signal 是唤醒队列中第一个线程
     * signalAll 是唤醒所有，然后争抢
     */
    @SuppressWarnings("DuplicatedCode")
    @Test
    public void condition() throws InterruptedException {
        ReentrantLock lock = new ReentrantLock();
        final Condition conditionA = lock.newCondition();
        final Condition conditionB = lock.newCondition();

        lock.lock();
        final Thread t = new Thread(() -> {
//            [2] t线程获取锁失败，aqs中生成等待节点，线程阻塞
            lock.lock();
//            sleep一会，方便debug，否则主线程执行await的同时这里就在执行signal了，
//            场景会比较复杂，不直观
            try {
                TimeUnit.SECONDS.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
//            [4] 将conditionA中的第一个等待线程移出，追加到aqs的等待队列中去
            conditionA.signal();
            try {
//                [5] 释放锁，唤醒main线程，生成condition等待节点，t线程阻塞
                conditionB.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
//            [8] t线程被唤醒，执行结束
            System.out.println(Thread.currentThread().getName() + " end");
//            [9] t线程释放锁
            lock.unlock();
        }, "t");
        t.start();
//        [1] main线程获取锁成功
        TimeUnit.SECONDS.sleep(1);
//        [3] 释放锁，唤醒t线程，生成condition等待节点，main线程阻塞
        conditionA.await();
//        sleep一会，方便debug，否则t线程执行await的同时这里就在执行signalAll了，
//        场景会比较复杂，不直观
        try {
            TimeUnit.SECONDS.sleep(30);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        [6] 将conditionB中的所有等待线程移出，追加到aqs的等待队列中去
        conditionB.signalAll();
//        [7] 释放锁，主线程运行结束，唤醒t线程，t线程获取锁
        lock.unlock();
        System.out.println(Thread.currentThread().getName() + " end");
        t.join();
    }

    @Test
    public void overflow() {
//        -2147483648
        System.out.println(Integer.MAX_VALUE + 1);
    }
}
