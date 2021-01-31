package xyz.jerez.se.jmx.bean;

import javax.management.Notification;

/**
 * 通知处理者
 *
 * @author liqilin
 * @since 2021/1/29 11:35
 */
public interface NotifyHandlerMBean {

    void handle(Notification notification);

}
