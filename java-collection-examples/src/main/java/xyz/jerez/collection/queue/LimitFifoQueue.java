package xyz.jerez.collection.queue;

import org.apache.commons.collections4.queue.AbstractQueueDecorator;

import java.util.Collection;
import java.util.Queue;

/**
 * 定长队列,功能类似于{@link org.apache.commons.collections4.queue.CircularFifoQueue}
 * 装饰某个队列，限定其长度，FIFO
 *
 * @author liqilin
 * @since 2021/5/1 10:49
 */
public class LimitFifoQueue<E> extends AbstractQueueDecorator<E> {

    private int limitSize;

    public LimitFifoQueue(Queue<E> queue) {
        this(queue, 1024);
    }

    public LimitFifoQueue(Queue<E> queue, int limitSize) {
        super(queue);
        this.limitSize = limitSize;
        ensureCapacity();
    }

    @Override
    protected void setCollection(Collection<E> coll) {
        super.setCollection(coll);
        ensureCapacity();
    }

    @Override
    public boolean add(E object) {
        final boolean res = super.add(object);
        ensureCapacity();
        return res;
    }

    @Override
    public boolean addAll(Collection<? extends E> coll) {
        final boolean res = super.addAll(coll);
        ensureCapacity();
        return res;
    }

    /**
     * 判断是否超出容量限制，超出就移除
     */
    private void ensureCapacity() {
        if (super.size() > limitSize) {
            for (int i = 0; i <= super.size() - limitSize; i++) {
                super.poll();
            }
        }
    }

}
