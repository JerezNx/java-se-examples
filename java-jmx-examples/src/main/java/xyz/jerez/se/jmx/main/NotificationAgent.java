package xyz.jerez.se.jmx.main;

import xyz.jerez.se.jmx.bean.Notifier;
import xyz.jerez.se.jmx.bean.NotifyHandler;
import xyz.jerez.se.jmx.listener.MyNotificationListener;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

/**
 * @author liqilin
 * @since 2021/1/29 15:20
 */
public class NotificationAgent {

    public static void main(String[] args) throws Exception {
        MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        NotifyHandler handler = new NotifyHandler();
        server.registerMBean(handler, new ObjectName("xyz.jerez.se.jmx.bean:name=notifyHandler"));
        final Notifier notifier = new Notifier();
        server.registerMBean(notifier, new ObjectName("xyz.jerez.se.jmx.bean:name=notifier"));

        notifier.addNotificationListener(new MyNotificationListener(), null, handler);
        while (true) {

        }
    }

}
