package xyz.jerez.collection.queue;

import org.junit.Test;
import xyz.jerez.collection.queue.LimitFifoQueue;

import java.util.*;

/**
 * @author liqilin
 * @since 2021/5/1 11:07
 */
public class QueueTest {

    @Test
    public void test() {
        Queue<Integer> queue = new ArrayDeque<>(10);
        queue.offer(1);
        queue.offer(2);
        queue.offer(3);
        queue.offer(4);
        queue.offer(5);

        final LimitFifoQueue<Integer> limitQueue = new LimitFifoQueue<>(queue, 3);
        System.out.println(limitQueue.size());
        System.out.println(limitQueue);
        limitQueue.add(6);
        limitQueue.add(7);
        System.out.println(limitQueue);
    }

    @Test
    public void test1() {
        Deque deque = new LinkedList();
        Stack stack = new Stack();
    }

}
