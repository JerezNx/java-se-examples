package xyz.jerez.binary;

import org.junit.Test;

/**
 * 基础数据类型转换
 *
 * @author liqilin
 * @since 2021/1/15 14:10
 */
public class BasicTypeConversion {

    @Test
    public void test() {
        byte a = (byte) 127;
        byte b = (byte) 128;
        byte c = (byte) 100;
        int x = 0xff;//255
        byte d = (byte) x;
        x = 0x80;//128
        byte f = (byte) x;
        c = (byte) (c * 3);
        System.out.println(a + " " + b + " " + c + " " + d + " " + f);
    }

}
