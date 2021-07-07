package xyz.jereznx.se.vm.classloader.code;

/**
 * @author liqilin
 * @since 2021/7/7 18:40
 */
public class Info {

    private MessageImpl message;

    static {
        System.out.println("invoke static code");
    }

}
