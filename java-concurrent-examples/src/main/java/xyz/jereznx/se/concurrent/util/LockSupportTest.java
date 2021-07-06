package xyz.jereznx.se.concurrent.util;

import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * LockSupport 使用demo
 *
 * @author LQL
 * @since Create in 2021/2/16 18:42
 */
@SuppressWarnings("AlibabaAvoidManuallyCreateThread")
public class LockSupportTest {

    @Test
    public void test() throws InterruptedException {
        final Thread thread = new Thread(() -> {
            System.out.println("start");
//            当前线程睡眠
            LockSupport.park();
            System.out.println("end");
        });
        thread.start();

        TimeUnit.SECONDS.sleep(5);
//        唤醒指定线程
        LockSupport.unpark(thread);
    }

    @Test
    public void test1() throws InterruptedException {
        final Thread thread = new Thread(() -> {
            System.out.println("start");
//            当前线程睡眠
            LockSupport.park();
            System.out.println("end");
        });
        thread.start();

        TimeUnit.SECONDS.sleep(5);
//        interrupt 也会终止 park
        thread.interrupt();
    }

    @Test
    public void t3() throws InterruptedException {
        final Thread thread = new Thread(() -> {
            try {
                for (;;){
                    LockSupport.park(this);
                    System.out.println(Thread.interrupted());
                }
            } finally {
                System.out.println("end");
            }
        });
        thread.start();
        TimeUnit.SECONDS.sleep(1);
//        interrupt 会打断park，但不会产生异常
        thread.interrupt();
        thread.join();
    }
}
