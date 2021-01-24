package xyz.jerez.nio.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 原始NIO
 * 与BIO区别： accept 和 read 都是非阻塞的
 *
 * - 优点：可以一个线程完成多个客户端的连接
 * - 缺点：accept 和 read 都是系统调用，会频繁从用户态切换为内核态，浪费cpu
 *
 * @author LQL
 * @since Create in 2021/1/24 17:55
 */
public class NioServer {

    public static void main(String[] args) throws IOException, InterruptedException {
//        1.init
        ServerSocketChannel server = ServerSocketChannel.open();
//        设置为非阻塞，否则 下面的accept会阻塞住
        server.configureBlocking(false);
        server.bind(new InetSocketAddress(8888));

        List<SocketChannel> clients = new ArrayList<>();

        while (true) {
            TimeUnit.SECONDS.sleep(1);
//            2.accept
            final SocketChannel newClient = server.accept();
            if (newClient == null) {
                System.out.println("there is not client connect");
            } else {
//                设置为非阻塞,否则下面的read会阻塞住
                newClient.configureBlocking(false);
                clients.add(newClient);
            }
//            3.read
            for (SocketChannel oldClient : clients) {
                ByteBuffer buf = ByteBuffer.allocate(1024);
                while (oldClient.read(buf) > 0) {
                    buf.flip();
                    System.out.print(StandardCharsets.ISO_8859_1.decode(buf).toString());
                    buf.clear();
                }
            }
        }
    }

}
