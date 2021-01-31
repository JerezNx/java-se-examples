package xyz.jerez.se.jmx.bean;

/**
 * @author liqilin
 * @since 2021/1/29 10:37
 */
public interface HelloMBean {

    String getName();

    void setName(String name);

    int getAge();

    void setAge(int age);

    void helloWorld();

    void helloWorld(String str);

    void getTelephone();

}
