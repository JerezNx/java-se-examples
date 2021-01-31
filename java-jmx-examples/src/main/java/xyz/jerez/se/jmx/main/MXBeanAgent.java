package xyz.jerez.se.jmx.main;

import xyz.jerez.se.jmx.bean.Point;
import xyz.jerez.se.jmx.bean.User;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

/**
 * @author liqilin
 * @since 2021/1/29 15:27
 */
public class MXBeanAgent {

    public static void main(String[] args) throws Exception {
        MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        ObjectName objectName = new ObjectName("xyz.jerez.se.jmx.bean:name=user");
        final User user = new User();
        user.setName("lql");
        user.setPoint(new Point(1, 2));
        server.registerMBean(user, objectName);
        while (true) {

        }
    }

}
