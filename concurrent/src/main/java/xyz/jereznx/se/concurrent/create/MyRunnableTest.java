package xyz.jereznx.se.concurrent.create;

/**
 * @author LQL
 * @since Create in 2020/8/17 20:53
 */
public class MyRunnableTest  {

    public static void main(String[] args) {
        new Thread(new MyRunnable()).start();
        new Thread(()-> System.out.println("run by lambda")).start();
        System.out.println("main");
    }

    static class MyRunnable implements Runnable {
        public void run() {
            System.out.println("run");
        }
    }
}
