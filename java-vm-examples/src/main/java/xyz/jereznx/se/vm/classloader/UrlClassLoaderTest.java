package xyz.jereznx.se.vm.classloader;

import org.junit.Test;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author liqilin
 * @since 2021/7/4 13:10
 */
public class UrlClassLoaderTest {

    @Test
    public void t1() throws Exception {

        /*动态加载指定类*/
        //类路径(包文件上一层)
        File file = new File("C:\\Users\\Jerez\\Documents\\Project\\IdeaProject\\java-se-examples\\java-concurrent-examples\\target\\test-classes");
        URL url = file.toURI().toURL();
        //创建类加载器
        ClassLoader loader = new URLClassLoader(new URL[]{url});
        //import com.sun.org.apache.bcel.internal.util.ClassLoader;
        //类路径
        //ClassLoader classLoader = new ClassLoader(new String[]{""});
        //加载指定类，注意一定要带上类的包名
        Class<?> cls = loader.loadClass("xyz.jereznx.se.concurrent.Print");
        //初始化一个实例
        Object obj = cls.newInstance();
        //方法名和对应的参数类型
        Method method = cls.getMethod("printString", String.class, String.class);
        //调用得到的上边的方法method
        Object o = method.invoke(obj, "lql", "zzm");
        System.out.println(o);

        System.out.println(cls.getClassLoader());
        System.out.println(cls.getClassLoader().getParent());
        System.out.println(A.class.getClassLoader());
        System.out.println(Object.class.getClassLoader());
    }

    @Test
    public void t2() throws Exception {
        /*动态加载指定jar包调用其中某个类的方法*/
        //jar包的路径
        File file = new File("C:\\work\\MavenRepository\\org\\apache\\commons\\commons-lang3\\3.0\\commons-lang3-3.0.jar");
        URL url = file.toURI().toURL();
        //创建类加载器
        ClassLoader loader = new URLClassLoader(new URL[]{url});
        //加载指定类，注意一定要带上类的包名
        Class<?> cls = loader.loadClass("org.apache.commons.lang3.StringUtils");
        //方法名和对应的各个参数的类型
        Method method = cls.getMethod("center", String.class, int.class, String.class);
        //调用得到的上边的方法method(静态方法，第一个参数可以为null)
        Object o = method.invoke(null, "lql", 10, "0");
        System.out.println(o);
    }

    static class A {

    }

}
