package xyz.jerez.collection.list;

import org.junit.Test;

import java.util.ArrayList;

/**
 * @author liqilin
 * @since 2021/5/16 14:35
 */
public class ArrayListTests {

    @Test
    public void test() {
        final ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            list.add(i);
        }
    }

}
