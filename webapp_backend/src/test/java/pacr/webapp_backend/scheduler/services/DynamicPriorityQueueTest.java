package pacr.webapp_backend.scheduler.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

public class DynamicPriorityQueueTest {

    private static final int COMPARE_RESULT = 1;

    private DynamicPriorityQueue<Object> priorityQueue;

    @Mock
    private Object object1;

    @Mock
    private Object object2;

    @Mock
    private Comparator<Object> comparator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        when(comparator.compare(object1, object2)).thenReturn(-COMPARE_RESULT);
        when(comparator.compare(object2, object1)).thenReturn(COMPARE_RESULT);

        this.priorityQueue = new DynamicPriorityQueue<>(comparator);
    }

    @Test
    void peek_isEmpty() {
        Object element = priorityQueue.peek();

        assertNull(element);
    }

    @Test
    void peek_noPriorityChange() {
        priorityQueue.add(object1);
        priorityQueue.add(object2);

        Object element = priorityQueue.peek();

        assertEquals(object1, element);
    }

    @Test
    void peek_withPriorityChange() {
        priorityQueue.add(object1);
        priorityQueue.add(object2);

        when(comparator.compare(object1, object2)).thenReturn(COMPARE_RESULT);
        when(comparator.compare(object2, object1)).thenReturn(-COMPARE_RESULT);

        Object element = priorityQueue.peek();

        assertEquals(object2, element);
    }
}
