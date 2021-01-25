package xyz.jerez.nio.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * 多线程 + 多路复用NIO 实现2
 * 思路：
 * 一个listener线程使用阻塞io 进行accept,获取到新的连接后分配给worker线程
 * 多个worker线程 使用selector，处理多个连接
 * <p>
 * 实现1中存在死锁问题，所以考虑 listener获取到socket之后，将socket传给worker进行操作
 * worker进行selector 的 register 、selector 操作
 *
 * @author LQL
 * @since Create in 2021/1/24 22:02
 */
public class NioMultiplexingThread2Server {

    public static void main(String[] args) throws IOException {
//        1.init
        ServerSocketChannel server = ServerSocketChannel.open();
//        server.configureBlocking(false);
        server.bind(new InetSocketAddress(8888));

    }

    static class Listener implements Runnable {

        private ServerSocketChannel server;

        private List<Worker> workerList = new ArrayList<>();

        public Listener(ServerSocketChannel server, List<Worker> workerList) {
            if (server == null || workerList == null || workerList.size() < 2) {
                throw new IllegalArgumentException();
            }
            this.server = server;
            this.workerList = workerList;
        }

        @Override
        public void run() {
            int count = 0;
            while (true) {
                try {
                    final SocketChannel client = server.accept();
                    System.out.println("add a new connection : " + client.getRemoteAddress());
                    client.configureBlocking(false);
                    int index = count++ % workerList.size();
                    workerList.get(index).addClient(client);
                    System.out.println("dispatch to " + index);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class Worker implements Runnable {

        private Selector selector;

        private BlockingQueue<SocketChannel> clientQueue = new LinkedBlockingDeque<>();

        public Worker(Selector selector) {
            this.selector = selector;
        }

        @Override
        public void run() {
            while (true) {
                try {
//                    1.遍历queue，进行 register
//                    register之后就移除
                    final Iterator<SocketChannel> queueIterator = clientQueue.iterator();
                    while (queueIterator.hasNext()) {
                        final SocketChannel client = queueIterator.next();
                        client.register(selector, SelectionKey.OP_READ);
                        queueIterator.remove();
                    }
//                    2.select read
                    while (selector.select(100) > 0) {
                        final Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                        while (iterator.hasNext()) {
                            final SelectionKey key = iterator.next();
                            if (key.isReadable()) {
                                final SocketChannel client = (SocketChannel) key.channel();
                                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                                try {
                                    int length;
                                    while ((length = client.read(byteBuffer)) > 0) {
                                        byteBuffer.flip();
                                        System.out.print(StandardCharsets.UTF_8.decode(byteBuffer).toString());
                                        byteBuffer.clear();
                                    }
                                    if (length == -1) {
                                        client.close();
                                    }
                                    System.out.println();
                                    System.out.println(Thread.currentThread().getId() + " read finish");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    client.close();
                                }
                            }
                            iterator.remove();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * 添加新的socket client
         * @param client client
         */
        public void addClient(SocketChannel client) {
            clientQueue.add(client);
        }
    }
}
