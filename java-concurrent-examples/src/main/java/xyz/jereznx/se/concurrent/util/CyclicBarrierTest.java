package xyz.jereznx.se.concurrent.util;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

/**
 * {@link CyclicBarrier} 用法示例
 * 主要用于控制多个线程在某一时间点完成某操作后才继续执行
 *
 * @author LQL
 * @since Create in 2021/2/11 13:40
 */
public class CyclicBarrierTest {

    public static void main(String[] args) {
        int threadNum = 5;
//        第一个参数即线程数，第二个是所有线程完成后的回调
        CyclicBarrier cyclicBarrier = new CyclicBarrier(threadNum, () -> System.out.println("all finished"));

        for (int i = 0; i < threadNum; i++) {
            int finalI = i;
            new Thread(() -> {
                try {
                    TimeUnit.SECONDS.sleep(threadNum);
//                    调用await后，当前线程就会阻塞
//                    直到有构造方法处定义数量的线程都调用 await后才会唤醒 继续执行
                    cyclicBarrier.await();
                    System.out.println(finalI + " finished");
                    TimeUnit.SECONDS.sleep(threadNum);
//                    可以重复使用
                    cyclicBarrier.await();
                    System.out.println(finalI + " finished again");
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

}
