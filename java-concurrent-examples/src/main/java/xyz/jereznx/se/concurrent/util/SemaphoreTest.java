package xyz.jereznx.se.concurrent.util;

import org.junit.Test;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * {@link Semaphore} 信号量工具类 使用demo
 * 其主要用于控制资源的有限访问及排队缓冲等待
 *
 * @author LQL
 * @since Create in 2021/2/11 13:56
 */
@SuppressWarnings("AlibabaAvoidManuallyCreateThread")
public class SemaphoreTest {

    @Test
    public void apiExample() throws InterruptedException {
        final int permits = 5;
//        初始化资源为5个
        final Semaphore semaphore = new Semaphore(permits);

//        获取一个
        semaphore.acquire();

//        打印当前剩余可获取的数量
        System.out.println(semaphore.availablePermits());
//        把剩余的资源都占用了
        System.out.println(semaphore.drainPermits());

//        说是释放占用的资源，但其实可以超过初始化的数量
        semaphore.release();
        semaphore.release();
        semaphore.release();
        semaphore.release();
        semaphore.release();
        semaphore.release();

        System.out.println(semaphore.availablePermits());
        System.out.println(semaphore.drainPermits());
    }

    public static void main(String[] args) throws InterruptedException {
        final int permits = 5;
//        初始化资源为5个
        final Semaphore semaphore = new Semaphore(permits);
        AtomicInteger count = new AtomicInteger(0);
        for (int i = 0; i < 7; i++) {
            new Thread(() -> {
                try {
                    TimeUnit.SECONDS.sleep(count.incrementAndGet());
                    System.out.println("当前剩余资源数：" + semaphore.availablePermits());
                    System.out.println("当前排队等待数：" + semaphore.getQueueLength());
                    semaphore.acquire();
                    System.out.println("success");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
        TimeUnit.SECONDS.sleep(5);
        semaphore.release();
    }

}
