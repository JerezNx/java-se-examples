package xyz.jerez.se.jmx.bean;

import javax.management.Notification;

/**
 * @author liqilin
 * @since 2021/1/29 11:35
 */
public class NotifyHandler implements NotifyHandlerMBean {

    @Override
    public void handle(Notification notification) {
        System.out.println("handle: " + notification);
    }

}
