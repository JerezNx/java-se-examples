package xyz.jerez.bio.d;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * @author LQL
 * @since Create in 2021/1/21 22:22
 */
public class Client {

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("127.0.0.1", 8888);
        PrintStream printStream = new PrintStream(socket.getOutputStream());
        Scanner scanner = new Scanner(System.in);
        String msg;
        while ((msg = scanner.nextLine()) != null) {
            printStream.println(msg);
            printStream.flush();
            if (msg.equals("end")) {
                socket.shutdownOutput();
                break;
            }
        }
    }

}
