package xyz.jerez.nio.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * @author LQL
 * @since Create in 2021/1/25 19:49
 */
public class NioClient {

    public static void main(String[] args) throws IOException, InterruptedException {
//        SocketChannel client = SocketChannel.open();
//        client.configureBlocking(false);
//        client.connect(new InetSocketAddress("127.0.0.1", 8888));
////        由于非阻塞，有可能执行到后面，还没建立连接
//        可能导致 NotYetConnectedException
//        while (!client.finishConnect()) {
//            TimeUnit.MILLISECONDS.sleep(100);
//        }
//        也可以这样，在open的时候就进行连接了，连接成功再设置非阻塞
        SocketChannel client = SocketChannel.open(new InetSocketAddress("127.0.0.1", 8888));
        client.configureBlocking(false);

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            final String msg = scanner.nextLine();
            if (msg.equals("end")) {
                client.shutdownOutput();
                break;
            }
            client.write(ByteBuffer.wrap(msg.getBytes()));
        }

    }

}
