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
                    System.out.println("add a new connection:" + client.getRemoteAddress());
                } else if (selectionKey.isReadable()) {
//                    5.接收数据
                    final SocketChannel client = (SocketChannel) selectionKey.channel();
                    ByteBuffer buf = ByteBuffer.allocate(1024);
                    while (true) {
                        try {
//                            >0 表示有数据，为0表示没数据，客户端调 shutdownOutput 为-1
//                            客户端直接断开是直接抛异常
                            final int dataLength = client.read(buf);
                            System.out.println("read dataLength:" + dataLength);
                            if (dataLength == -1) {
                                client.close();
                                break;
                            } else if (dataLength == 0) {
                                break;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            client.close();
                            break;
                        }
                        buf.flip();
                        System.out.print(StandardCharsets.ISO_8859_1.decode(buf).toString());
                        buf.clear();
                    }
                    System.out.println();
                    System.out.println("read end");
                }
//                selector.selectedKeys() 是在jvm中维护的当前ready的事件
//                selector.select() 获取到事件后，并非是直接替换 selector.selectedKeys()，而是往内追加，
//                所以处理完成后需要移除，否则之后下一轮处理时，还会遍历到这个事件，但实际上这时候可能并没有事件
                iterator.remove();
            }
            System.out.println("wait end");
        }
    }

}
