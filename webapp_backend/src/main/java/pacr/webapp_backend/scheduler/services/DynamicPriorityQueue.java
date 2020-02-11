package pacr.webapp_backend.scheduler.services;

import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * A priority queue can work with changing priorities of elements already added to the queue.
 *
 * @param <E> the type of elements stored.
 */
public class DynamicPriorityQueue<E> extends PriorityQueue<E> {

    /**
     * Creates a new DynamicPriorityQueue with a comparator that is used to sort the elements.
     *
     * @param comparator comparator used to sort the elements.
     */
    public DynamicPriorityQueue(Comparator<? super E> comparator) {
        super(comparator);
    }

    @Override
    public E peek() {
        if (isEmpty()) {
            return null;
        }

        // it is save to assume that the type of the array elements is E.
        E[] elements = (E[]) super.toArray();

        super.clear();
        super.addAll(Arrays.asList(elements));

        return super.peek();
    }

}
