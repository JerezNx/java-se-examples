package xyz.jerez.bio;

import org.junit.Test;

import java.io.*;

/**
 * @author LQL
 * @since Create in 2021/1/21 21:11
 */
public class DataOutputStreamTest {

    @Test
    public void write() throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(out);
//        前2个字节为数据长度(无符号数)
        dataOutputStream.writeUTF("test");
        final byte[] bytes = out.toByteArray();
//        2 + 4 = 6
        System.out.println(bytes.length);
//        utf8 中文大部分占3个字节
        dataOutputStream.writeUTF("赵");
//        boolean占1个字节
        dataOutputStream.writeBoolean(true);
//        byte占1个字节
        dataOutputStream.writeByte(33);
//        char占2个字节
        dataOutputStream.writeChar('T');
//        char占2个字节
        dataOutputStream.writeChar('赵');
//        short占2个字节
        dataOutputStream.writeShort(4);
//        int占4个字节
        dataOutputStream.writeInt(6);
//        int占8个字节
        dataOutputStream.writeLong(6);
//        31
        System.out.println(out.size());
    }

    @Test
    public void read() throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(out);
//        java 中 char 是 Unicode ,占2个字节
        dataOutputStream.writeChar('赵');
        DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(out.toByteArray()));
        final char x = dataInputStream.readChar();
        System.out.println(x);
    }

}
