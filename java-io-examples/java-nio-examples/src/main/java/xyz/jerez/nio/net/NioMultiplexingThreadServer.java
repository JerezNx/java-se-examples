package xyz.jerez.nio.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

/**
 * 多线程 + 多路复用NIO
 * 思路：
 * 一个listener线程使用阻塞io 进行accept,获取到新的连接后分配给worker线程
 * 多个worker线程 使用selector，处理多个连接
 *
 * 此方案考虑给selector register socket 由listener操作
 * worker只进行select read操作
 * 但没想到 selector 的 register 和 select 存在死锁问题，虽然添加超时能解决，但由于争抢，不是很ok
 * 考虑实现2
 *
 * @author LQL
 * @since Create in 2021/1/24 22:02
 */
public class NioMultiplexingThreadServer {

    public static void main(String[] args) throws IOException {
//        1.init
        ServerSocketChannel server = ServerSocketChannel.open();
//        server.configureBlocking(false);
        server.bind(new InetSocketAddress(8888));
        final List<Selector> selectors = Arrays.asList(Selector.open(), Selector.open(), Selector.open());

        new Thread(new Listener(server, selectors)).start();
        for (Selector selector : selectors) {
            new Thread(new Worker(selector)).start();
        }
    }

    static class Listener implements Runnable {

        private ServerSocketChannel server;

        List<Selector> selectors;

        public Listener(ServerSocketChannel server, List<Selector> selectors) {
            if (server == null || selectors == null || selectors.size() < 2) {
                throw new IllegalArgumentException();
            }
            this.server = server;
            this.selectors = selectors;
        }

        @Override
        public void run() {
            int count = 0;
            while (true) {
                try {
                    final SocketChannel client = server.accept();
                    System.out.println("add a new connection : " + client.getRemoteAddress());
                    client.configureBlocking(false);
                    int index = count++ % selectors.size();
                    final Selector selector = selectors.get(index);
//                    Listener 和 Worker 会对同一个 selector进行操作
//                    register 和 select 会产生死锁，所以给两个操作都添加超时，
//                    且进行register之前进行唤醒操作
//                    就是两个会争抢，看实际场景，如果连接多的话，把 register超时调长。如果写数据多的话，就把select时间调长
                    selector.wakeup();
                    client.register(selector, SelectionKey.OP_READ, 1000);
                    System.out.println("dispatch to " + index);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class Worker implements Runnable {

        private Selector selector;

        public Worker(Selector selector) {
            this.selector = selector;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    final boolean b = selector.select(100) > 0;
                    if (!b) {
//                        睡眠不会释放锁
//                        TimeUnit.MILLISECONDS.sleep(100);
                        continue;
                    }
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
}
