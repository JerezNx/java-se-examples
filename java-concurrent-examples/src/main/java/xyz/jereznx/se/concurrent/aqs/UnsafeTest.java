package xyz.jereznx.se.concurrent.aqs;

import org.junit.Test;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * @author liqilin
 * @since 2021/6/20 17:29
 */
public class UnsafeTest {

    private static Unsafe unsafe;

    private volatile int state;

    private static final long stateOffset;

    static {
        try {
            final Class<?> clazz = Class.forName("sun.misc.Unsafe");
            final Field theUnsafe = clazz.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            unsafe = (Unsafe) theUnsafe.get(null);
            stateOffset = unsafe.objectFieldOffset
                    (AbstractQueuedSynchronizer.class.getDeclaredField("state"));
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    @Test
    public void t1() {
        System.out.println(stateOffset);
        unsafe.compareAndSwapObject(this, stateOffset, 0, 1);
        System.out.println(state);
    }
}
