package xyz.jerez.nio.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

/**
 * 多路复用NIO
 *
 * @author LQL
 * @since Create in 2021/1/24 22:02
 */
public class NioMultiplexingServer {

    public static void main(String[] args) throws IOException {
//        1.init
        ServerSocketChannel server = ServerSocketChannel.open();
        server.configureBlocking(false);
        server.bind(new InetSocketAddress(8888));
//        2.epoll_ctl add server_socket
        final Selector selector = Selector.open();
        server.register(selector, SelectionKey.OP_ACCEPT);

//        会阻塞
//        3.epoll_wait
        while (selector.select() > 0) {
            final Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                final SelectionKey selectionKey = iterator.next();
//                4.连接事件
                if (selectionKey.isAcceptable()) {
//                    System.out.println(selectionKey.channel().equals(server));
                    final SocketChannel client = server.accept();
                    client.configureBlocking(false);
                    client.register(selector, SelectionKey.OP_READ);
                    System.out.println("add a new connection");
                } else if (selectionKey.isReadable()) {
//                    5.接收数据
                    final SocketChannel client = (SocketChannel) selectionKey.channel();
                    ByteBuffer buf = ByteBuffer.allocate(1024);
                    while (client.read(buf) > 0) {
                        buf.flip();
                        System.out.print(StandardCharsets.ISO_8859_1.decode(buf).toString());
                        buf.clear();
                    }
                    System.out.println("read end");
                }
                iterator.remove();
            }
            System.out.println("wait end");
        }
    }

}
