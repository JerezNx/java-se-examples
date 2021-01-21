package xyz.jereznx.se.concurrent.thread;

/**
 * @author LQL
 * @since Create in 2020/8/21 23:49
 */
public class WaitTest {
    public static void main(String[] args) throws InterruptedException {
        Object lock = new Object();
        final Thread thread = new Thread(() -> {
            while (true) {
                synchronized (lock) {
                    System.out.println(1);
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(2);
                }
            }
        });
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();
        final Thread thread1 = new Thread(() -> {
            while (true) {
                synchronized (lock) {
                    System.out.println(3);
//                必须要notify，虽然运行完会释放锁，但thread会一直wait
                    lock.notify();
                    Thread.yield();
                    System.out.println(4);
                }
            }
        });
        thread1.setPriority(Thread.MIN_PRIORITY);
        thread1.start();
        thread.join();
        System.out.println(5);
    }
}
