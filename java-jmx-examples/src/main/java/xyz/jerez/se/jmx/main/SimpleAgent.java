package xyz.jerez.se.jmx.main;

import xyz.jerez.se.jmx.bean.Hello;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

/**
 * @author liqilin
 * @since 2021/1/29 10:41
 */
public class SimpleAgent {

    public static void main(String[] args) throws Exception {
        MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        ObjectName helloName = new ObjectName("xyz.jerez.se.jmx.bean:name=hello");
        //create mbean and register mbean
        server.registerMBean(new Hello(), helloName);
        while (true) {

        }
    }

}
