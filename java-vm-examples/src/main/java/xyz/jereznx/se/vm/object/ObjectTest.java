package xyz.jereznx.se.vm.object;

import org.junit.Test;
import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.vm.VM;
import xyz.jereznx.se.vm.reference.ReferenceTest;

/**
 * 对象由3部分组成： object header | 实例数据 | 补齐
 * <p>
 * 指针压缩，默认开启（顾名思义，只对指针有效，即对象引用相关）
 * -XX:+UseCompressedOops 开启指针压缩
 * -XX:-UseCompressedOops 关闭指针压缩
 * <p>
 * 1.其中对象header由3部分组成：
 * - mark word ，记录了对象和锁有关的信息。32位系统中是32位，64位系统中是64位。不受指针压缩影响。
 * - 指向当前类的指针。32位系统中是32位，64位系统中是64位。开启指针压缩后，64位系统中也只有32位。
 * - 数组长度，只有数组对象才会有。无论32位还是64位系统，都是32位。
 * <p>
 * 2.对象数据
 * - 对象引用长度等同于 object header 中的 类指针（仔细想想也是，对象头中是当前对象的类，成员变量中是指向成员变量的类）。
 * - 按成员变量的数据类型排列
 * <p>
 * 3.对齐，
 * - 无论32位还是64位，无论开不开启指针压缩，都以8字节对齐
 * - 按每8位对齐，而非最后的空缺补齐，比如有 4+2+1+4，并非是前面排11，最后补5.而是 （4+2+1）补1，4补4
 *
 * @author liqilin
 * @since 2021/7/7 19:03
 */
public class ObjectTest {

    /**
     * 前8字节是mark word
     * 按8位，倒着看的
     */
    @Test
    public void t1() {
        final A a = new A();
        System.out.println(VM.current().details());
        System.out.println(ClassLayout.parseInstance(a).toPrintable());
//
    }

    /**
     * 数组的内存分布比普通对象就是对象头中多了数组的长度
     * 数据部分十分简单，就是 元素size * 元素大小，最后补齐
     */
    @Test
    public void t2() {
        System.out.println(VM.current().details());
        System.out.println(ClassLayout.parseInstance(new int[]{1, 2, 3}).toPrintable());
        System.out.println(ClassLayout.parseInstance(new boolean[]{true, false, true}).toPrintable());
    }

    /**
     * java中最小的对象即 new Object(),无论 32还是64位，无论是否开启指针压缩，都是16 byte
     */
    @Test
    public void t3() {
        System.out.println(VM.current().details());
        System.out.println(ClassLayout.parseInstance(new Object()).toPrintable());
//        基础类型会变成包装类型
        System.out.println(ClassLayout.parseInstance(1).toPrintable());
    }

    /**
     * -XX:+PrintGCDetails
     * 第4、5位为gc年龄
     */
    @Test
    public void t4() {
        Object object = new Object();
        System.out.println(ClassLayout.parseInstance(object).toPrintable());
        System.gc();
        System.out.println(ClassLayout.parseInstance(object).toPrintable());
        System.gc();
//        todo 为何后续的gc不会增长年龄了？
        System.out.println(ClassLayout.parseInstance(object).toPrintable());
        ReferenceTest.drainMemory();
        System.out.println(ClassLayout.parseInstance(object).toPrintable());
        ReferenceTest.drainMemory();
        System.out.println(ClassLayout.parseInstance(object).toPrintable());
    }


    static class A {
        /**
         * 占1byte的boolean
         */
        boolean flag;

        /**
         * 占4个字节的int
         * 成员变量的内存分布顺序是 ： 类型 -> 代码顺序
         * 比如虽然这边的int写在boolean的下面，但实际内存分布int是在boolean上面的
         */
        int num;

        /**
         * 每不满8字节的就补齐
         * 比如已有 4 + 2 + 1，7个字节，后面还有个4字节的，并不会 7+4，后面补7字节，而是 （7+1 补1个字节），（4+4）补4个字节
         */
        char c;

        /**
         * 32位系统中对象的引用占4个字节
         * 64位系统中对象的引用占8个字节
         * 开启指针压缩的话，64位系统也只占4个字节
         */
        String s;

        /**
         * 包装类型也是引用
         */
        Double d;

        char b;
    }
}
