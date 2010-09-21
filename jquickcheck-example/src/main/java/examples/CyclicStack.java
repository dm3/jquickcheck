package examples;

/**
 * Good old boring implementation of stack...
 */
public class CyclicStack<T> {

    private static final int MAX = 10;
    private final Object[] items = new Object[MAX];
    private int current = 0;

    public void push(T item) {
        if (current < MAX) {
            current += 1;
        } else {
            current = 0;
        }
        items[current] = item;
    }

    @SuppressWarnings("unchecked")
    public T pop() {
        if (current > 0) {
            current -= 1;
        } else {
            current = MAX - 1;
        }
        return (T) items[current];
    }
}
