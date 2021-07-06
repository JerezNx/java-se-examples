package xyz.jerez.collection;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author liqilin
 * @since 2021/5/16 17:41
 */
public class GenericTest {

    @Test
    public void test1() {
        List list = new ArrayList();
        list.add(1);
        list.add("a");
        System.out.println(list.get(0));
        System.out.println(list.get(1));
    }

    @Test
    public void test2() {
        List list = new ArrayList<Integer>();
        list.add(1);
        list.add("a");
        System.out.println(list.get(0));
        System.out.println(list.get(1));
    }

    @Test
    public void test3() {
//        只能初始化，不能 add
        List<?> list = new ArrayList(Arrays.asList(1, 2, 3));
        final Object o = list.get(0);
//        list.add(1);
//        list.add("a");
    }

    @Test
    public void test4() {
        List<? extends Number> list = new ArrayList<>(Arrays.asList(1, 2, 3));
        final Number number = list.get(0);
//        list.add(1);
//        list.add("a");
    }

    @Test
    public void test5() {
        List<Object> list = new ArrayList<>();
        list.add(1);
        list.add("a");
        System.out.println(list);
    }

}
