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
//        创建服务端socket，监听8888端口
        ServerSocket serverSocket = new ServerSocket(8888);
//        阻塞，直到客户端建立连接
        final Socket socket = serverSocket.accept();
//        获取客户端socket的输入流，可以读取客户端发送的数据
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        System.out.println(reader.readLine());
    }

}
