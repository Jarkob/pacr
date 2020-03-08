package pacr.webapp_backend.scheduler.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
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
    public DynamicPriorityQueue(final Comparator<? super E> comparator) {
        super(comparator);
    }

    @Override
    public E peek() {
        if (isEmpty()) {
            return null;
        }

        // it is save to assume that the type of the array elements is E.
        final E[] elements = (E[]) super.toArray();

        super.clear();
        super.addAll(Arrays.asList(elements));

        return super.peek();
    }

    /**
     * Returns the element at the given index.
     * @param index the index of the returned item.
     * @return the item at the given index or null if the index is invalid.
     */
    public E get(int index) {
        if (index >= 0 && index < this.size()) {
            List<E> allElements = new ArrayList<>(this);
            allElements.sort(this.comparator());

            return allElements.get(index);
        }

        return null;
    }

}
