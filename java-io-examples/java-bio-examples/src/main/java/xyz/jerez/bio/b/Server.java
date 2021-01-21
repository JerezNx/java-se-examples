package xyz.jerez.bio.b;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 服务端可以不断接收客户端发送的消息
 *
 * @author LQL
 * @since Create in 2021/1/21 21:53
 */
public class Server {

    /**
     * 此时如果用浏览器访问 http://localhost:8888
     * 服务端会打印：
     *
     * GET / HTTP/1.1
     * Host: localhost:8888
     * Connection: keep-alive
     * Cache-Control: max-age=0
     * Upgrade-Insecure-Requests: 1
     * User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.141 Safari/537.36
     * Sec-Fetch-Site: none
     * Sec-Fetch-Mode: navigate
     * Sec-Fetch-User: ?1
     * Sec-Fetch-Dest: document
     * Accept-Encoding: gzip, deflate, br
     * Accept-Language: zh-CN,zh;q=0.9
     * Cookie: Idea-604185a7=d2357b71-2a9d-4b75-8358-675aea55de4c
     *
     * 即HTTP协议的完整请求信息
     * 此时浏览器会阻塞住，等待服务端响应。
     * 而此时服务端阻塞在readline。
     * 当把浏览器请求中断后，会给服务端发送结束请求信号，服务端 readline为null，结束循环
     * 程序结束
     */
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8888);
        final Socket socket = serverSocket.accept();
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String msg;
        while ((msg = reader.readLine()) != null) {
            System.out.println(msg);
        }
        System.out.println("end");
    }

}
