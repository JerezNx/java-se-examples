package xyz.jereznx.se.concurrent;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author LQL
 * @since Create in 2020/8/31 23:12
 */
public class Print3 {

    public static void main(String[] args) {
        Lock lock = new ReentrantLock();
        final Condition conditionA = lock.newCondition();
        final Condition conditionB = lock.newCondition();
        final Condition conditionC = lock.newCondition();

        final Thread threadA = new Thread(() -> {
            String[] a = {"a1", "a2", "a3", "a4"};
            for (int i = 0; i < 4; i++) {
                lock.lock();
                System.out.println(a[i]);
                conditionB.signal();
                if (i == 3) {
                    lock.unlock();
                    return;
                }
                try {
                    conditionA.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
//                    重入锁获取几次。就要释放几次
                    lock.unlock();
                }
            }
        },"A");
        threadA.start();
        final Thread threadB = new Thread(() -> {
            String[] b = {"b1", "b2", "b3", "b4"};
            for (int i = 0; i < 4; i++) {
                lock.lock();
                System.out.println(b[i]);
                conditionC.signal();
                if (i == 3) {
                    lock.unlock();
                    return;
                }
                try {
                    conditionB.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    lock.unlock();
                }
            }
        },"B");
        threadB.start();
        final Thread threadC = new Thread(() -> {
            String[] c = {"c1", "c2", "c3", "c4"};
            for (int i = 0; i < 4; i++) {
                lock.lock();
                System.out.println(c[i]);
                conditionA.signal();
                if (i == 3) {
                    lock.unlock();
                    return;
                }
                try {
                    conditionC.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"C");
        threadC.start();
    }

}
