package xyz.jerez.nio.channel;

import org.junit.Test;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

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
        final FileChannel inputChannel = new FileInputStream("src/main/resources/src.txt").getChannel();
        final FileChannel outputChannel = new FileOutputStream("src/main/resources/target.txt").getChannel();
//       不存在8M的限制
//        inputChannel.transferTo(0, inputChannel.size(), outputChannel);
        outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        outputChannel.close();
    }

    /**
     * windows下一次最多拷贝 8M
     */
    @Test
    public void test6() throws IOException, InterruptedException {
        //noinspection AlibabaAvoidManuallyCreateThread
        final Thread serverThread = new Thread(() -> {
            try {
                ServerSocketChannel server = ServerSocketChannel.open();
                server.bind(new InetSocketAddress(8888));
                final SocketChannel client = server.accept();
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                final FileChannel outputChannel = new FileOutputStream("src/main/resources/target.txt").getChannel();
                while (client.read(buffer) > 0) {
                    buffer.flip();
                    outputChannel.write(buffer);
                    buffer.clear();
                }
                outputChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        serverThread.start();

        final FileChannel inputChannel = new FileInputStream("src/main/resources/src.txt").getChannel();
        SocketChannel client = SocketChannel.open(new InetSocketAddress("127.0.0.1", 8888));
        inputChannel.transferTo(0, inputChannel.size(), client);
        client.shutdownOutput();
        client.close();

        serverThread.join();
    }

    /**
     * 创建一个10M的文件
     */
    @Test
    public void createFile() throws FileNotFoundException {
        PrintWriter writer = new PrintWriter(new FileOutputStream("src/main/resources/src.txt"));
        String line = "zzm\n";
        int count = 10 * 1024 * 1024 / 4;
        for (int i = 0; i < count; i++) {
            writer.print(line);
        }
        writer.close();
    }
}
