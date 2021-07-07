package xyz.jereznx.se.vm.classloader;

import org.junit.Test;
import xyz.jereznx.se.vm.classloader.custom.ClassFileLoader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;

/**
 * @author liqilin
 * @since 2021/7/7 18:51
 */
public class ClassLoadTest {

    /**
     * 类的加载过程是： 加载 -> 链接 -> 初始化
     * Class.forName 第二个参数是指定是否执行初始化，默认是true
     */
    @Test
    public void t1() throws ClassNotFoundException {
        Class.forName("xyz.jereznx.se.vm.classloader.code.Info");
    }

    @Test
    public void t2() throws ClassNotFoundException {
        Class.forName("xyz.jereznx.se.vm.classloader.code.Info", false, ClassLoadTest.class.getClassLoader());
    }

    @Test
    public void t3() throws ClassNotFoundException {
        ClassLoadTest.class.getClassLoader().loadClass("xyz.jereznx.se.vm.classloader.code.Info");
    }

    @Test
    public void t4() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final ClassLoader classLoader = ClassLoadTest.class.getClassLoader();
        final Method method = classLoader.getClass().getDeclaredMethod("loadClass", String.class, boolean.class);
        method.setAccessible(true);
        method.invoke(classLoader, "xyz.jereznx.se.vm.classloader.code.Info", true);
//        classLoader.loadClass("xyz.jereznx.se.vm.classloader.code.Info", true);
    }

    @Test
    public void t5() throws MalformedURLException, ClassNotFoundException {
        final ClassFileLoader loaderA = new ClassFileLoader("C:\\Users\\Jerez\\Documents\\Project\\IdeaProject\\java-se-examples\\java-vm-examples\\src\\main\\codeA", null);
        loaderA.loadClass("xyz.jereznx.se.vm.classloader.code.Info", true);
    }

}
