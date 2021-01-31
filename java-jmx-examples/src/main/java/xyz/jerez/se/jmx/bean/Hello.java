package xyz.jerez.se.jmx.bean;

/**
 * @author liqilin
 * @since 2021/1/29 10:37
 */
public class Hello implements HelloMBean {

    private String name;

    private int age;

    @Override
    public void helloWorld() {
        System.out.println("hello world");
    }

    @Override
    public void helloWorld(String str) {
        System.out.println("helloWorld:" + str);
    }

    @Override
    public void getTelephone() {
        System.out.println("get Telephone");
    }

    @Override
    public String getName() {
        System.out.println(name);
        return name;
    }

    @Override
    public void setName(String name) {
        System.out.println(name);
        this.name = name;
    }

    @Override
    public int getAge() {
        System.out.println(age);
        return age;
    }

    @Override
    public void setAge(int age) {
        System.out.println(age);
        this.age = age;
    }
}
