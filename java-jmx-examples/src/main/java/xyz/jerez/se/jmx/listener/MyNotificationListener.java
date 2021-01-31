package xyz.jerez.se.jmx.listener;

import xyz.jerez.se.jmx.bean.NotifyHandler;

import javax.management.Notification;
import javax.management.NotificationListener;

/**
 * @author liqilin
 * @since 2021/1/29 11:26
 */
public class MyNotificationListener implements NotificationListener {

    @Override
    public void handleNotification(Notification notification, Object handback) {
        if (handback instanceof NotifyHandler) {
            NotifyHandler notifyHandler = (NotifyHandler) handback;
            notifyHandler.handle(notification);
        }
    }
}
