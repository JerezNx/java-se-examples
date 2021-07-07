package xyz.jereznx.se.concurrent.lock;

import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.vm.VM;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * 验证 synchronized 锁升级过程
 * 参考：https://www.cnblogs.com/yuhangwang/p/11267364.html
 *
 * @author LQL
 * @since Create in 2021/2/28 16:40
 */
@SuppressWarnings("ALL")
public class SynchronizedWithJOLTest {

    public static void main(String[] args) throws Exception {
        // 直接休眠5秒，或者用 -XX:BiasedLockingStartupDelay=0 关闭偏向锁延迟
//        Thread.sleep(5000);
        // 锁对象
        final Object lock = new Object();
        System.out.println("1.无锁状态");
//        todo 疑问1： 如果 启动时加上了 -XX:BiasedLockingStartupDelay=0 参数，这边打印的是 101 ，不就是偏向锁了，吗？
//          不加参数时是正常的 001
        System.out.println(ClassLayout.parseInstance(lock).toPrintable());


        // 如果不执行hashCode方法，则对象头的中的hashCode为0，
        // 但是如果执行了hashCode（identity hashcode，重载过的hashCode方法则不受影响），会导致偏向锁的标识位变为0（不可偏向状态），
        // 且后续的加锁不会走偏向锁而是直接到轻量级锁（被hash的对象不可被用作偏向锁）
//        printf("lock.hashCode：", getLongBinaryString(lock.hashCode()));

        System.out.println("2.无锁状态");
        System.out.println(ClassLayout.parseInstance(lock).toPrintable());

        // 无锁 --> 偏向锁
        final Thread threadA = new Thread(() -> {
            synchronized (lock) {
                System.out.println("3.偏向锁 偏向线程A");
                System.out.println(ClassLayout.parseInstance(lock).toPrintable());
              /*  printf("偏向线程：", getLongBinaryString(unsafe.getLong(Thread.currentThread(), 0L)));
                printf("偏向线程hash：", getLongBinaryString(Thread.currentThread().hashCode()));
                printf("偏向线程ID：", getLongBinaryString(Thread.currentThread().getId()) + "\n");*/
                // 如果锁对象已经进入了偏向状态，再调用hashCode()，会导致锁直接膨胀为重量级锁
//                lock.hashCode();
            }

            // 再次进入同步快，lock锁还是偏向当前线程
            synchronized (lock) {
                System.out.println("4.偏向锁 偏向线程A");
                System.out.println(ClassLayout.parseInstance(lock).toPrintable());
                /*printf("偏向线程hash：", getLongBinaryString(Thread.currentThread().hashCode()));
                printf("偏向线程ID：", getLongBinaryString(Thread.currentThread().getId()) + "\n");*/
            }
        });
        threadA.start();
        threadA.join();

        // 可以看到就算偏向的线程结束，锁对象的偏向锁也不会自动撤销
        System.out.println("5.偏向锁  无线程持有锁，但偏向不会自动撤销，即还是偏向于线程A");
//        todo 疑问2： 如果不加 -XX:BiasedLockingStartupDelay=0 参数，此处打印的是 001 ,不是说锁的升级不能撤销吗？
        System.out.println(ClassLayout.parseInstance(lock).toPrintable());

        // 偏向锁 --> 轻量级锁
        synchronized (lock) {
            // 对象头为：指向线程栈中的锁记录指针
//            todo 疑问3，线程A已经运行完成了，此时主线程获取锁
//             按道理这里应该还是偏向锁，只是偏向于主线程了呀，不知道为何会升级为轻量级锁
//             难道只要不是第一个偏向的线程，就会升级为轻量级锁？
            System.out.println("6.轻量级锁 主线程持有锁");
            System.out.println(ClassLayout.parseInstance(lock).toPrintable());
            // 这里获得轻量级锁的线程是主线程
//            printf("轻量级线程hash：", getLongBinaryString(Thread.currentThread().hashCode()));
//            printf("轻量级线程ID：", getLongBinaryString(Thread.currentThread().getId()) + "\n");
        }
        final Thread threadB = new Thread(() -> {
            synchronized (lock) {
//                todo 疑问4 同上 为何这里就是轻量级锁了
                System.out.println("7.轻量级锁 线程B持有锁");
                System.out.println(ClassLayout.parseInstance(lock).toPrintable());
//                printf("轻量级线程hash：", getLongBinaryString(Thread.currentThread().hashCode()));
//                printf("轻量级线程ID：", getLongBinaryString(Thread.currentThread().getId()) + "\n");
            }
        });
        threadB.start();
        threadB.join();

        // 轻量级锁 --> 重量级锁
        Thread threadC;
        synchronized (lock) {
            System.out.println("8.轻量级锁 主线程获取锁");
            System.out.println(ClassLayout.parseInstance(lock).toPrintable());
            // 在锁已经获取了lock的轻量级锁的情况下，子线程来获取锁，则锁会膨胀为重量级锁
            threadC = new Thread(() -> {
                synchronized (lock) {
                    System.out.println("9.重量级锁 线程C获取锁");
                    System.out.println(ClassLayout.parseInstance(lock).toPrintable());
                   /* printf("重量级线程hash：", getLongBinaryString(Thread.currentThread().hashCode()));
                    printf("重量级线程ID：", getLongBinaryString(Thread.currentThread().getId()) + "\n");*/
                }
            });
            threadC.start();
            // 同步块中睡眠一会，此时主线程持有锁，线程C不停自旋直到超过限制，最终锁升级为重量级锁
            Thread.sleep(100);
        }
        threadC.join();
//        TimeUnit.SECONDS.sleep(1);
        synchronized (lock) {
            System.out.println("9.重量级锁 主线程获取锁");
//            todo 疑问5： 如果线程C执行完成后不睡1秒的话，这边打印的锁的指针为何还是和线程C处的一样
//             睡了1秒后正常按预期的，是主线程的指针
            System.out.println(ClassLayout.parseInstance(lock).toPrintable());
        }
    }


}
