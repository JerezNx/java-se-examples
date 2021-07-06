package xyz.jereznx.se.vm.classloader;

import org.junit.Test;
import xyz.jereznx.se.vm.classloader.code.Message;
import xyz.jereznx.se.vm.classloader.custom.ClassFileLoader;
import xyz.jereznx.se.vm.classloader.custom.JarLoader;

import java.net.MalformedURLException;
import java.net.URLClassLoader;

/**
 * @author liqilin
 * @since 2021/7/4 16:51
 */
public class ClassLoaderTest {

    @Test
    public void t1() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        final Class<?> clazz = Class.forName("xyz.jereznx.se.vm.classloader.code.MessageImpl");
        final Message message = (Message) clazz.newInstance();
        System.out.println(message.get());
    }

    @Test
    public void t2() throws ClassNotFoundException, IllegalAccessException, InstantiationException, MalformedURLException {
        final ClassFileLoader jarLoaderA = new ClassFileLoader("C:\\Users\\Jerez\\Documents\\Project\\IdeaProject\\java-se-examples\\java-vm-examples\\src\\main\\codeA");
        final Class<?> clazzA = jarLoaderA.loadClass("xyz.jereznx.se.vm.classloader.code.MessageImpl");
        final Message messageA = (Message) clazzA.newInstance();
        System.out.println(messageA.get());

        final ClassFileLoader jarLoaderB = new ClassFileLoader("C:\\Users\\Jerez\\Documents\\Project\\IdeaProject\\java-se-examples\\java-vm-examples\\src\\main\\codeB");
        final Class<?> clazzB = jarLoaderB.loadClass("xyz.jereznx.se.vm.classloader.code.MessageImpl");
        final Message messageB = (Message) clazzB.newInstance();
        System.out.println(messageB.get());

        System.out.println(clazzA.equals(clazzB));
    }
}
