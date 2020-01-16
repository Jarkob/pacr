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
    public DynamicPriorityQueue(Comparator<? super E> comparator) {
        super(comparator);
    }

    @Override
    public E poll() {
        if (isEmpty()) {
            return null;
        }

        // it is save to assume that the type of the array elements is E.
        E[] elements = (E[]) super.toArray();

        super.clear();
        super.addAll(Arrays.asList(elements));

        return super.poll();
    }

    /**
     * @return a list sorted by the priority of the elements.
     */
    public List<E> getSortedList() {
        PriorityQueue<E> queue = new PriorityQueue<>(comparator());

        // it is save to assume that the type of the array elements is E.
        queue.addAll(Arrays.asList((E[]) super.toArray()));

        List<E> sortedList = new ArrayList<>();

        // PriorityQueue is not necessarily sorted
        while (!queue.isEmpty()) {
            E element = queue.poll();

            sortedList.add(element);
        }

        return sortedList;
    }

}
