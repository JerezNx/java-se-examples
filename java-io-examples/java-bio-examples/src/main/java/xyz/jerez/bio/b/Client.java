package xyz.jerez.bio.b;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

/**
 * @author LQL
 * @since Create in 2021/1/21 22:02
 */
public class Client {

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("127.0.0.1", 8888);
        PrintStream printStream = new PrintStream(socket.getOutputStream());
        printStream.println("z");
        printStream.println("z");
        printStream.println("m");
        printStream.flush();
//        没有这个的话，服务端不会打印end，而是异常
        socket.shutdownOutput();
    }

}
