package xyz.jereznx.se.vm.reference;

import org.junit.Test;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

/**
 * @author liqilin
 * @since 2021/7/7 19:30
 */
public class ReferenceTest {

    /**
     * 清除对象
     * date虽然设为null，但由于JVM没有执行垃圾回收操作，MyDate的finalize()方法没有被运行。
     */
    @Test
    public void t1() {
        MyDate date = new MyDate();
        date = null;
    }

    /**
     * 显式调用垃圾回收
     * 调用了System.gc()，使JVM运行垃圾回收，MyDate的finalize()方法被运行。
     */
    @Test
    public void t2() {
        MyDate date = new MyDate();
        date = null;
        System.gc();
    }

    /**
     * 隐式调用垃圾回收
     * 虽然没有显式调用垃圾回收方法System.gc()，但是由于运行了耗费大量内存的方法，触发JVM进行垃圾回收。
     */
    @Test
    public void t3() {
        MyDate date = new MyDate();
        date = null;
        ReferenceTest.drainMemory();
    }

    /**
     * java中默认引用类型就是强引用
     * 除非没有对象指向它，否则就不会被清除
     */
    @Test
    public void testStrongReference() {
        MyDate date = new MyDate();
        System.gc();
    }

    /**
     * 设置小内存：  -Xmx2m -Xmn1m
     * <p>
     * 在内存不足时，软引用被清除
     * 否则，不会被清除。
     * <p>
     * SoftReference ref = new SoftReference(new MyDate());
     * ReferenceTest.drainMemory();
     * <p>
     * 等价于
     * <p>
     * MyDate date = new MyDate();
     * <p>
     * // 由JVM决定运行
     * If(JVM.内存不足()) {
     * date = null;
     * System.gc();
     * }
     */
    @Test
    public void testSoftReference() {
        SoftReference ref = new SoftReference<>(new MyDate());
        System.out.println(ref.get());
        ReferenceTest.drainMemory();
//        内存不够时直接异常了，内存够时
        System.out.println(ref.get());
    }

    /**
     * 设置小内存：  -Xmx2m -Xmn1m
     */
    @Test
    public void testSoftReference1() {
        final MyDate myDate = new MyDate();
        SoftReference ref = new SoftReference<>(myDate);
        System.out.println(ref.get());
        ReferenceTest.drainMemory();
//        这里不明白，为何有强引用在，还是会被gc
        System.out.println(myDate);
        System.out.println(ref.get());
    }

    /**
     * 在JVM垃圾回收运行时，弱引用被终止.
     * 适合作不重要的缓存
     * {@link ThreadLocal 里也是使用的这个，每个线程中有一个 {@link java.lang.ThreadLocal.ThreadLocalMap}
     * k就是 WeakReference<ThreadLocal>, 之所以使用WeakReference ，是因为，如果不是弱引用，则在外部将 ThreadLocal = null后
     * 由于 ThreadLocalMap 中还有 ThreadLocal 的强引用，就会导致其无法被gc}
     */
    @Test
    public void testWeakReference() {
        WeakReference ref = new WeakReference<>(new MyDate());
        System.out.println(ref.get());
//        System.gc();
        ReferenceTest.drainMemory();
//        null
        System.out.println(ref.get());
    }

    /**
     * 由于还有强引用，所以就算发生gc，弱引用也不会被清除
     */
    @Test
    public void testWeakReference1() {
        final MyDate myDate = new MyDate();
        WeakReference ref = new WeakReference<>(myDate);
        System.out.println(ref.get());
        System.gc();
//        ReferenceTest.drainMemory();
//        null
        System.out.println(ref.get());
        System.out.println(myDate);
    }

    @Test
    public void testPhantomReference() {
        ReferenceQueue<MyDate> queue = new ReferenceQueue<>();
        PhantomReference<MyDate> ref = new PhantomReference<>(new MyDate(), queue);
//        System.out.println(queue.poll().get());
        System.gc();
//        System.out.println(queue.poll().get());
        System.out.println(ref.get());
    }

    /**
     * 不论gc与否，是否有强引用，都会被清除
     */
    @Test
    public void testPhantomReference1() {
        ReferenceQueue<MyDate> queue = new ReferenceQueue<>();
        final MyDate myDate = new MyDate();
        PhantomReference<MyDate> ref = new PhantomReference<>(myDate, queue);
//        System.gc();
        System.out.println(ref.get());
        System.out.println(myDate);
    }

    /**
     * 消耗大量内存
     */
    public static void drainMemory() {
        String[] array = new String[1024 * 10];
        for (int i = 0; i < 1024 * 10; i++) {
            for (int j = 'a'; j <= 'z'; j++) {
                array[i] += (char) j;
            }
        }
//        System.out.println(ClassLayout.parseInstance(array).toPrintable());
    }
}
