package xyz.jereznx.se.concurrent.thread;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * Thread 中 interrupt 相关示例
 * <p>
 * - {@link Thread#interrupted()} 是静态方法，调用一次后会清除中断标识,见{@link InterruptTest#isInterruptedTest()}
 * <p>
 * - {@link Thread#isInterrupted()} 是实例方法，调用后不会清除中断标识,见{@link InterruptTest#interruptedTest()}
 * <p>
 * <p>
 * - {@link Thread#interrupt()} 方法只是给线程打上中断标识（并会在线程内部产生一个 {@link InterruptedException}），并不会杀死线程
 * - 见 {@link InterruptTest#interruptedExceptionTest()}
 * <p>
 * - 可以通过捕获{@link InterruptedException}这个异常，或者通过{@link Thread#isInterrupted()}来判断当前线程是否被手动中断
 * - 然后通过代码手动中断,见 {@link InterruptTest#interruptFlagTest()}
 *
 * @author LQL
 * @since Create in 2021/2/18 13:38
 */
public class InterruptTest {

    @Test
    public void isInterruptedTest() {
//        给线程打上中断标识
        Thread.currentThread().interrupt();
//        通过实例方法调用，其内部传参 ClearInterrupted 是 false ,即不会清除中断标识
        System.out.println(Thread.currentThread().isInterrupted());
//        所以第二次输出也是 true
        System.out.println(Thread.currentThread().isInterrupted());
    }

    @Test
    public void interruptedTest() {
        Thread.currentThread().interrupt();
//        通过静态方法调用，其内部传参 ClearInterrupted 是 true ,即会清除中断标识
        System.out.println(Thread.interrupted());
//        所以第二次输出是 false
        System.out.println(Thread.interrupted());
    }

    @Test
    public void interruptedExceptionTest() throws InterruptedException {
        final Thread thread = new Thread(() -> {
            System.out.println(1);
            while (true) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
//                    interrupt 会产生这个异常，我们捕获异常后 需要 手动中断
                    break;
                }
            }
        });
        thread.start();
        TimeUnit.SECONDS.sleep(3);
//        interrupt 仅仅是给线程打上一个中断标识，并不会真的停止线程
//        但他会让线程内部产生一个 InterruptedException，我们可以捕获这个 exception，然后手动中断
        thread.interrupt();
        thread.join();
        System.out.println(2);
    }

    @Test
    public void interruptFlagTest() throws InterruptedException {
        final Thread thread = new Thread(() -> {
            System.out.println(1);
            while (true) {
//                或者不用异常，而是通过 isInterrupted 方法判断是否设置 中断标识
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }
            }
        });
        thread.start();
        TimeUnit.SECONDS.sleep(3);
//        interrupt 仅仅是给线程打上一个中断标识，并不会真的停止线程
        thread.interrupt();
        thread.join();
        System.out.println(2);
    }

}
