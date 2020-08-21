package xyz.jereznx.se.concurrent;

/**
 * @author LQL
 * @since Create in 2020/8/19 23:56
 */
public class Print {

    public static void main(String[] args) throws InterruptedException {
        Object lock = new Object();
        final Thread thread1 = new Thread(() -> {
            int[] a = {1, 3, 5, 7};
            for (int i = 0; i < 4; i++) {
                synchronized (lock) {
                    System.out.println(a[i]);
                    lock.notify();
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread1.start();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        final Thread thread2 = new Thread(() -> {
            int[] a = {2, 4, 6, 8};
            for (int i = 0; i < 4; i++) {
                synchronized (lock) {
                    System.out.println(a[i]);
                    lock.notify();
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        });
        thread2.start();
        thread1.join();
        thread2.interrupt();
    }

}
