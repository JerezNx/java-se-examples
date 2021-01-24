package xyz.jerez.nio.channel;

import org.junit.Test;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Channel 可同时读写
 *
 * @author LQL
 * @since Create in 2021/1/24 17:21
 */
public class FileChannelExamples {

    @Test
    public void test1() throws IOException {
        final FileChannel channel = new FileOutputStream("src/main/resources/1.txt").getChannel();
        channel.write(ByteBuffer.wrap("zzm".getBytes()));
//        有些像 flush
//        channel.force(false);
        channel.close();
    }

    @Test
    public void test2() throws IOException {
        final FileChannel inputChannel = new FileInputStream("src/main/resources/2.txt").getChannel();
        final FileChannel outputChannel = new FileOutputStream("src/main/resources/3.txt").getChannel();
        final ByteBuffer buf = ByteBuffer.allocate(4);
        while (true) {
            final int len = inputChannel.read(buf);
//            没有数据返回-1
            System.out.println(len);
            if (!(len > 0)) break;
//            read操作后，需要切换为读模式
            buf.flip();
            outputChannel.write(buf);
//            读完之后,需要重置
            buf.clear();
        }
        outputChannel.close();
    }

    @Test
    public void test3() throws IOException {
//        可同时读写
        final RandomAccessFile file = new RandomAccessFile("src/main/resources/4.txt", "rw");
//        另一种获取 FileChannel 的方式
        final FileChannel channel = file.getChannel();
        channel.write(ByteBuffer.wrap("zzm".getBytes()));
        channel.close();
    }

    @Test
    public void test5() throws IOException {
        final FileChannel inputChannel = new FileInputStream("src/main/resources/2.txt").getChannel();
        final FileChannel outputChannel = new FileOutputStream("src/main/resources/3.txt").getChannel();
//        拷贝文件
//        inputChannel.transferTo(0, inputChannel.size(), outputChannel);
        outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        outputChannel.close();
    }
}
