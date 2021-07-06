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
        super(new URL[]{new File(filePath).toURI().toURL()});
    }

}
