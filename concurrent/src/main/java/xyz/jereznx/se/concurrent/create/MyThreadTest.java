package xyz.jereznx.se.concurrent.create;

/**
 * @author LQL
 * @since Create in 2020/8/17 20:50
 */
public class MyThreadTest {

    public static void main(String[] args) {
        new MyThread().start();
        System.out.println("main");
    }
    static class MyThread extends Thread{
        @Override
        public void run() {
            System.out.println(this.getId()+" run");
        }
    }
}
