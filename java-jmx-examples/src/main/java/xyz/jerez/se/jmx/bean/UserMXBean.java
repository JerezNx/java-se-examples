package xyz.jerez.se.jmx.bean;

/**
 * @author liqilin
 * @since 2021/1/29 15:25
 */
public interface UserMXBean {

    Point getPoint();

    void setPoint(Point point);

    String getName();

    void setName(String name);

}
