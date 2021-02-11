package xyz.jereznx.se.concurrent.util;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author LQL
 * @since Create in 2021/2/11 15:15
 */
public class CountDownLatchTest {

    public static void main(String[] args) throws InterruptedException {
        int threadNum = 5;
        final CountDownLatch countDownLatch = new CountDownLatch(threadNum);
        for (int i = 0; i < threadNum; i++) {
            int finalI = i;
            new Thread(() -> {
                try {
                    TimeUnit.SECONDS.sleep(finalI);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                countDownLatch.countDown();
//                如果在这里 await，则效果就和 CyclicBarrier 相同了
//                不同的是 CyclicBarrier 是 "cyclic" 的，即可以循环重复使用的
//                而 CountDownLatch 清零后就不可使用
//                countDownLatch.await();
                System.out.println("finished " + finalI);

            }).start();
        }
        countDownLatch.await();
        System.out.println("all finished");
    }

}
