package xyz.jerez.se.jmx.bean;

import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;

/**
 * @author liqilin
 * @since 2021/1/29 11:18
 */
public class Notifier extends NotificationBroadcasterSupport implements NotifierMBean {

    private int seq;

    @Override
    public void notify(String msg) {
        Notification notify =
                //通知名称；谁发起的通知；序列号；发起通知时间；发送的消息
                new Notification("notify.test", this, ++seq, System.currentTimeMillis(), "lql");
        sendNotification(notify);
    }
}
