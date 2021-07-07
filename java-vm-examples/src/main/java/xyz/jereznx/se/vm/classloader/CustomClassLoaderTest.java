package xyz.jereznx.se.vm.classloader;

import org.junit.Test;
import xyz.jereznx.se.vm.classloader.code.Message;
import xyz.jereznx.se.vm.classloader.custom.ClassFileLoader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;

/**
 * @author liqilin
 * @since 2021/7/4 16:51
 */
@SuppressWarnings("ALL")
public class CustomClassLoaderTest {

    @Test
    public void t1() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        final Class<?> clazz = Class.forName("xyz.jereznx.se.vm.classloader.code.MessageImpl");
        final Message message = (Message) clazz.newInstance();
        System.out.println(message.get());
    }

    /**
     * 如果当前classpath中有 MessageImpl，则在自定义classloader loadClass 时，调用parenet的 loadClass，就能成功load，所以输出的都是main
     * 如果没有，则parent loadClass 就会失败，此时只能通过自定义classLoader 进行加载，就能成功加载到指定版本的class
     * <p>
     * 或者像t4中的一样，自定义classLoader设置parent为null，则可以打破双亲委派机制(伪，还是基于双亲委派的逻辑，只是没有双亲了，自然无法委派...)，从而由自定义classLoader加载，也能实现
     * <p>
     * 但两者的区别由t3可以看出
     * 如果不设置自定义classLoader的parent为 AppClassLoader，则无法将自定义加载的实现类强转为接口
     * <p>
     * 说明parent加载器load的class，在子加载器中认为是同一个loader加载的
     * （仔细想想也是，由于双亲委派原则（除非重写loadClass）同名的class是不会被父子都加载一遍的）
     * <p>
     * 所以，如果当前classpath中不包含指定类，则可以设置自定义classLoader的parent为 AppClassLoader ,便于接口强转之类
     * 否则，只能设置 parent 为null，或者重写 loadClass
     */
    @Test
    public void t2() throws ClassNotFoundException, IllegalAccessException, InstantiationException, MalformedURLException {

        final ClassFileLoader loaderA = new ClassFileLoader("C:\\Users\\Jerez\\Documents\\Project\\IdeaProject\\java-se-examples\\java-vm-examples\\src\\main\\codeA");
        final Class<? extends Message> clazzA = (Class<? extends Message>) loaderA.loadClass("xyz.jereznx.se.vm.classloader.code.MessageImpl");
        System.out.println(Message.class.getClassLoader());
        final Message messageA = clazzA.newInstance();
        System.out.println(messageA.get());

        final ClassFileLoader jarLoaderB = new ClassFileLoader("C:\\Users\\Jerez\\Documents\\Project\\IdeaProject\\java-se-examples\\java-vm-examples\\src\\main\\codeB");
        final Class<?> clazzB = jarLoaderB.loadClass("xyz.jereznx.se.vm.classloader.code.MessageImpl");
        final Message messageB = (Message) clazzB.newInstance();
        System.out.println(messageB.get());

        System.out.println(clazzA.equals(clazzB));
        System.out.println(Message.class.isAssignableFrom(clazzA));
        System.out.println(Message.class.isAssignableFrom(clazzB));
    }

    @Test
    public void t3() throws ClassNotFoundException, IllegalAccessException, InstantiationException, MalformedURLException {

        final ClassFileLoader loaderA = new ClassFileLoader("C:\\Users\\Jerez\\Documents\\Project\\IdeaProject\\java-se-examples\\java-vm-examples\\src\\main\\codeA", null);
        final Class<? extends Message> clazzA = (Class<? extends Message>) loaderA.loadClass("xyz.jereznx.se.vm.classloader.code.MessageImpl");
//        loaderA.loadClass("xyz.jereznx.se.vm.classloader.code.Message");
//        Class.forName("xyz.jereznx.se.vm.classloader.code.Message", true, loaderA);
        System.out.println(Message.class.getClassLoader());
        System.out.println(Message.class.isAssignableFrom(clazzA));
//        Message 是由 AppClassLoader 加载的
        final Message messageA = clazzA.newInstance();
        System.out.println(messageA.get());

        final ClassFileLoader jarLoaderB = new ClassFileLoader("C:\\Users\\Jerez\\Documents\\Project\\IdeaProject\\java-se-examples\\java-vm-examples\\src\\main\\codeB", null);
        final Class<?> clazzB = jarLoaderB.loadClass("xyz.jereznx.se.vm.classloader.code.MessageImpl");
        final Message messageB = (Message) clazzB.newInstance();
        System.out.println(messageB.get());

        System.out.println(clazzA.equals(clazzB));
    }

    @Test
    public void t4() throws ClassNotFoundException, IllegalAccessException, InstantiationException, MalformedURLException, NoSuchMethodException, InvocationTargetException {

        final ClassFileLoader jarLoaderA = new ClassFileLoader("C:\\Users\\Jerez\\Documents\\Project\\IdeaProject\\java-se-examples\\java-vm-examples\\src\\main\\codeA", null);
        final Class<?> clazzA = jarLoaderA.loadClass("xyz.jereznx.se.vm.classloader.code.MessageImpl");
        final Object messageA = clazzA.newInstance();
        final Method getA = clazzA.getDeclaredMethod("get");
        System.out.println(getA.invoke(messageA));

        final ClassFileLoader jarLoaderB = new ClassFileLoader("C:\\Users\\Jerez\\Documents\\Project\\IdeaProject\\java-se-examples\\java-vm-examples\\src\\main\\codeB", null);
        final Class<?> clazzB = jarLoaderB.loadClass("xyz.jereznx.se.vm.classloader.code.MessageImpl");
        final Object messageB = clazzB.newInstance();
        final Method getB = clazzB.getDeclaredMethod("get");
        System.out.println(getB.invoke(messageB));

        System.out.println(clazzA.equals(clazzB));
    }

    @Test
    public void t5() throws MalformedURLException, ClassNotFoundException {
        final ClassFileLoader loaderA = new ClassFileLoader("C:\\Users\\Jerez\\Documents\\Project\\IdeaProject\\java-se-examples\\java-vm-examples\\src\\main\\codeA", null);
        final ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
//        这个方法并不意味着后续该线程加载class都使用 loaderA 了
//        只是将其设置到线程上下文而已，必须显示的通过 Thread.currentThread().getContextClassLoader().loadClass(...) 才可以
//        该方法作用就是方便一些地方，同一个线程上下文，跨各种类和方法，有一个标准的api，方便使用同一classloader
//        事实上自己定义一个threadlocal，存储classloader 也能实现同样的效果，只是不标准
        Thread.currentThread().setContextClassLoader(loaderA);
        try {
            Class.forName("xyz.jereznx.se.vm.classloader.code.MessageImpl");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println(Class.forName("xyz.jereznx.se.vm.classloader.code.MessageImpl", true, loaderA));;
        Thread.currentThread().setContextClassLoader(oldClassLoader);
    }
}
