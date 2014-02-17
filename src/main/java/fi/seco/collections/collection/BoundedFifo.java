package fi.seco.collections.collection;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Bounded FIFO backed by LinkedList.
 */

public class BoundedFifo<E> implements Iterable<E> {

	private final LinkedList<E> queue = new LinkedList<E>();
	private final int capacity;

	public BoundedFifo(int capacity) {
		if (capacity < 1) throw new IllegalArgumentException("Capacity can't be less than 1.");
		this.capacity = capacity;
	}

	public E peekLast() {
		return queue.peekLast();
	}

	/**
	 * @param e
	 *            element to be added
	 * @return returns the element that was removed from the head or null if
	 *         FIFO capacity was not exceeded
	 */
	public E addLast(E e) {
		if (queue.size() >= capacity) {
			E removed = removeFirst();
			queue.addLast(e);
			return removed;
		} else {
			queue.addLast(e);
			return null;
		}
	}

	public E peekFirst() {
		return queue.peekFirst();
	}

	/**
	 * @return the first element in the FIFO or null if the FIFO is empty
	 */
	public E removeFirst() {
		if (queue.isEmpty()) return null;
		return queue.removeFirst();
	}

	public boolean isEmpty() {
		return queue.isEmpty();
	}

	public int size() {
		return queue.size();
	}

	public int capacity() {
		return capacity;
	}

	@Override
	public Iterator<E> iterator() {
		return queue.iterator();
	}

	@Override
	public String toString() {
		return queue.toString();
	}

}
