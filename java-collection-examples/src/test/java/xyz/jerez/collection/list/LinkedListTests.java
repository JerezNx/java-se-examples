package xyz.jerez.collection.list;

import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author liqilin
 * @since 2021/5/16 14:41
 */
public class LinkedListTests {

    @Test
    public void test() {
        final List<Integer> list = new LinkedList<>();
        for (int i = 0; i < 11; i++) {
            list.add(i);
        }
    }

}
