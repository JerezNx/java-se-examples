package xyz.jerez.bio.d;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 服务端使用线程池处理
 *
 * @author LQL
 * @since Create in 2021/1/21 22:18
 */
public class Server {

    private static final ExecutorService threadPool = new ThreadPoolExecutor(4, 8,
            60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10));

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8888);
        Socket socket;
        while ((socket = serverSocket.accept()) != null) {
            Socket finalSocket = socket;
            threadPool.execute(() -> {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(finalSocket.getInputStream()));
                    String msg;
                    while ((msg = reader.readLine()) != null) {
                        System.out.println(msg);
                    }
                    System.out.println("end");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

}
