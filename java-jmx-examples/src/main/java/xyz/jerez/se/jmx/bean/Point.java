package xyz.jerez.se.jmx.bean;

import java.beans.ConstructorProperties;

/**
 * 注意，有3个限制条件，满足其中之一即可
 * - 有无参构造
 * - 有参构造上添加  {@link ConstructorProperties} 注解
 * - 实现接口，且接口不能有set方法
 * <p>
 * 目前看来 MXBean的复合属性好像是不能动态修改值
 *
 * @author liqilin
 * @since 2021/1/29 15:32
 */
public class Point {

    private int x;
    private int y;

    public Point() {
    }

    @ConstructorProperties({"x", "y"})
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

}
