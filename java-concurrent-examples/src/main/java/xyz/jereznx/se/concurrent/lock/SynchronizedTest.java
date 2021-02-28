package xyz.jereznx.se.concurrent.lock;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

/**
 * 验证 synchronized 锁升级过程
 *
 * @author LQL
 * @since Create in 2021/2/28 16:40
 */
public class SynchronizedTest {

    public static void main(String[] args) throws Exception {
        // 直接休眠5秒，或者用 -XX:BiasedLockingStartupDelay=0 关闭偏向锁延迟
//        Thread.sleep(5000);
        // 反射获取sun.misc的Unsafe对象，用来查看锁的对象头的信息
        Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
        theUnsafe.setAccessible(true);
        final Unsafe unsafe = (Unsafe) theUnsafe.get(null);

        // 锁对象
        final Object lock = new Object();
        // TODO 64位JDK对象头为 64bit = 8Byte，如果是32位JDK则需要换成unsafe.getInt
        System.out.println("1.无锁状态");
//        todo  如果 启动时加上了 -XX:BiasedLockingStartupDelay=0 参数，这边打印的是 101 ，即会默认就是偏向锁
//          不加参数时是正常的 001
        printf("lock：", getLongBinaryString(unsafe.getLong(lock, 0L)));


        // 如果不执行hashCode方法，则对象头的中的hashCode为0，
        // 但是如果执行了hashCode（identity hashcode，重载过的hashCode方法则不受影响），会导致偏向锁的标识位变为0（不可偏向状态），
        // 且后续的加锁不会走偏向锁而是直接到轻量级锁（被hash的对象不可被用作偏向锁）
//        printf("lock.hashCode：", getLongBinaryString(lock.hashCode()));

        System.out.println("2.无锁状态");
        printf("lock：", getLongBinaryString(unsafe.getLong(lock, 0L)));

        // 无锁 --> 偏向锁
        final Thread threadA = new Thread(() -> {
            synchronized (lock) {
                System.out.println("3.偏向锁 偏向线程A");
                printf("lock：", getLongBinaryString(unsafe.getLong(lock, 0L)));
              /*  printf("偏向线程：", getLongBinaryString(unsafe.getLong(Thread.currentThread(), 0L)));
                printf("偏向线程hash：", getLongBinaryString(Thread.currentThread().hashCode()));
                printf("偏向线程ID：", getLongBinaryString(Thread.currentThread().getId()) + "\n");*/
                // 如果锁对象已经进入了偏向状态，再调用hashCode()，会导致锁直接膨胀为重量级锁
//                lock.hashCode();
            }

            // 再次进入同步快，lock锁还是偏向当前线程
            synchronized (lock) {
                System.out.println("4.偏向锁 偏向线程A");
                printf("lock：", getLongBinaryString(unsafe.getLong(lock, 0L)));
                /*printf("偏向线程hash：", getLongBinaryString(Thread.currentThread().hashCode()));
                printf("偏向线程ID：", getLongBinaryString(Thread.currentThread().getId()) + "\n");*/
            }
        });
        threadA.start();
        threadA.join();

        // 可以看到就算偏向的线程结束，锁对象的偏向锁也不会自动撤销
        System.out.println("5.偏向锁  无线程持有锁，但偏向不会自动撤销，即还是偏向于线程A");
//        todo 疑问2： 如果不加 -XX:BiasedLockingStartupDelay=0 参数，此处打印的是 001 ,不是说锁的升级不能撤销吗？
        printf("lock：", getLongBinaryString(unsafe.getLong(lock, 0L)) + "\n");

        // 偏向锁 --> 轻量级锁
        synchronized (lock) {
            // 对象头为：指向线程栈中的锁记录指针
//            todo 疑问3，线程A已经运行完成了，此时主线程获取锁
//             按道理这里应该还是偏向锁，只是偏向于主线程了呀，不知道为何会升级为轻量级锁
//             难道只要不是第一个偏向的线程，就会升级为轻量级锁？
            System.out.println("6.轻量级锁 主线程持有锁");
            printf("lock：", getLongBinaryString(unsafe.getLong(lock, 0L)));
            // 这里获得轻量级锁的线程是主线程
//            printf("轻量级线程hash：", getLongBinaryString(Thread.currentThread().hashCode()));
//            printf("轻量级线程ID：", getLongBinaryString(Thread.currentThread().getId()) + "\n");
        }
        final Thread threadB = new Thread(() -> {
            synchronized (lock) {
//                todo 疑问4 同上 为何这里就是轻量级锁了
                System.out.println("7.轻量级锁 线程B持有锁");
                printf("lock：", getLongBinaryString(unsafe.getLong(lock, 0L)));
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
            printf("lock：", getLongBinaryString(unsafe.getLong(lock, 0L)));
            // 在锁已经获取了lock的轻量级锁的情况下，子线程来获取锁，则锁会膨胀为重量级锁
            threadC = new Thread(() -> {
                synchronized (lock) {
                    System.out.println("9.重量级锁 线程C获取锁");
                    printf("lock：", getLongBinaryString(unsafe.getLong(lock, 0L)));
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
            printf("lock：", getLongBinaryString(unsafe.getLong(lock, 0L)));
        }

        TimeUnit.SECONDS.sleep(1);
        System.out.println("9.重量级锁 无线程持有锁");
//        重量级锁的释放需要时间
//        todo 疑问，睡了1秒后（无论启动时加不加参数），发现无线程持有锁时，锁的状态又变成 01 即无锁状态了，难道 锁是可以降级的？
        printf("lock：", getLongBinaryString(unsafe.getLong(lock, 0L)));
    }

    private static String getLongBinaryString(long num) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 64; i++) {
            if ((num & 1) == 1) {
                sb.append(1);
            } else {
                sb.append(0);
            }
            num = num >> 1;
        }
        return sb.reverse().toString();
    }

    private static void printf(String desc, String value) {
        System.out.printf("%s%s%n", desc, value);
    }
}
