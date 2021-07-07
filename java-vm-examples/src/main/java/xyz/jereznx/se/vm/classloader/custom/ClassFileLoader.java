package xyz.jereznx.se.vm.classloader.custom;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author liqilin
 * @since 2021/7/4 17:36
 */
public class ClassFileLoader extends URLClassLoader {

    public ClassFileLoader(String filePath) throws MalformedURLException {
        this(filePath, ClassFileLoader.class.getClassLoader());
    }

    public ClassFileLoader(String filePath, ClassLoader parent) throws MalformedURLException {
        super(new URL[]{new File(filePath).toURI().toURL()}, parent);
    }

    @Override
    public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        return super.loadClass(name, resolve);
    }
}
