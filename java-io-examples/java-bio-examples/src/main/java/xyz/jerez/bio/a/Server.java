package xyz.jerez.bio.a;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 服务端可以接收客户端发送的数据
 *
 * @author LQL
 * @since Create in 2021/1/21 21:42
 */
public class Server {

    /**
     * 此时如果用浏览器访问 http://localhost:8888
     * 服务端会打印： GET / HTTP/1.1
     * 即HTTP协议请求第一行的文本
     */
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8888);
        final Socket socket = serverSocket.accept();
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        System.out.println(reader.readLine());
    }

}
