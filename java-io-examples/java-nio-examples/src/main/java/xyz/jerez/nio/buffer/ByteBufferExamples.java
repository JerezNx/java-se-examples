package xyz.jerez.nio.buffer;

import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;

/**
 * ByteBuffer使用样例
 * <p>
 * // Invariants: mark <= position <= limit <= capacity
 * // 标记
 * private int mark = -1;
 * // 当前位置
 * private int position = 0;
 * // 能操作的区域
 * private int limit;
 * // 最大容量
 * private int capacity;
 * <p>
 * flip: 从写模式 转为 读模式(其实核心就是 position改为0，limit为Position，所有只有在 写转读 时，调用才合理)
 *
 * @author LQL
 * @since Create in 2021/1/24 16:48
 */
public class ByteBufferExamples {

    @Test
    public void test1() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(10);
        byteBuffer.put("zzm".getBytes());
//        3 ，写了3个byte，当前位置从0移到了3
        System.out.println(byteBuffer.position());
//        10，能一直写到10
        System.out.println(byteBuffer.limit());
//        10，最大容量是10
        System.out.println(byteBuffer.capacity());

        System.out.println("-----------------------");

        byteBuffer.flip();
//        z
        System.out.println((char) byteBuffer.get());
//        1，当前读到第一个了
        System.out.println(byteBuffer.position());
//        3，最多能读到3，因为之前只写到了3，后面是空的
        System.out.println(byteBuffer.limit());
//        10
        System.out.println(byteBuffer.capacity());

        System.out.println("-----------------------");
//        flip是从写模式 转为 读模式
//        (其实核心就是 position改为0，limit为Position，所有只有在 写转读 时，调用才合理)
//        此处调用是不合理的，只是看下效果
        byteBuffer.flip();
//        0，flip后被重置了
        System.out.println(byteBuffer.position());
//        1，之前只读了1位
        System.out.println(byteBuffer.limit());
//        10
        System.out.println(byteBuffer.capacity());

        System.out.println("-----------------------");
        byteBuffer.clear();
//        0
        System.out.println(byteBuffer.position());
//        10
        System.out.println(byteBuffer.limit());
//        10
        System.out.println(byteBuffer.capacity());
//        z,clear 是逻辑上的清空，只是把 position 置为0，limit置为capacity
//        并不会把底层的数据清空，且也说明了，数据可被重复读
        System.out.println((char) byteBuffer.get());
//        还有多少能操作
        System.out.println(byteBuffer.remaining());
    }

    @Test
    public void test2() {
        final ByteBuffer byteBuffer = ByteBuffer.wrap("zzm".getBytes());
//        0
        System.out.println(byteBuffer.position());
//        3
        System.out.println(byteBuffer.limit());
//        3
        System.out.println(byteBuffer.capacity());
    }

    /**
     * 通过Java虚拟机工具查看堆内存大小
     */
    @Test
    public void test3() {
//        在Java虚拟机堆内存中分配 100M
//        ByteBuffer.allocate(1024 * 1024 * 100);
//        在外部内存中分配
        ByteBuffer.allocateDirect(1024 * 1024 * 100);
        while (true) {

        }
    }

}
