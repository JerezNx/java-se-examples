package xyz.jerez.bio.a;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @author LQL
 * @since Create in 2021/1/21 21:48
 */
public class Client {

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("127.0.0.1",8888);
        PrintStream printStream = new PrintStream(socket.getOutputStream());
        printStream.println("zzm");
        printStream.flush();
    }

}
