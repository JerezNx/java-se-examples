package xyz.jerez.se.jmx.bean;

/**
 * @author liqilin
 * @since 2021/1/29 15:23
 */
public class User implements UserMXBean {

    private String name;

    private Point point;

    @Override
    public Point getPoint() {
        return point;
    }

    @Override
    public void setPoint(Point point) {
        this.point = point;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

}
