/**
 * 
 */
package fi.seco.collections.iterator;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;

public class SortedIterableExpandingIterator<T> extends AIterableIterator<T> {

	private final Iterator<T>[] inputs;
	private final Comparator<? super T> comp;
	private final PriorityQueue<Item<T>> minHeap;

	public SortedIterableExpandingIterator(Comparator<? super T> comp, Iterator<T>... inputs) {
		this.comp = comp;
		this.minHeap = new PriorityQueue<Item<T>>(inputs.length);
		this.inputs = inputs;
		// Prime the heap
		for (int i = 0; i < inputs.length; i++)
			replaceItem(i);
	}

	public SortedIterableExpandingIterator(Comparator<? super T> comp, Collection<? extends Iterable<T>> inputsC) {
		this.comp = comp;
		this.inputs = new Iterator[inputsC.size()];
		this.minHeap = new PriorityQueue<Item<T>>(inputs.length);
		Iterator<? extends Iterable<T>> ti = inputsC.iterator();
		for (int i = 0; i < inputs.length; i++)
			this.inputs[i] = ti.next().iterator();
		// Prime the heap
		for (int i = 0; i < inputs.length; i++)
			replaceItem(i);
	}

	public SortedIterableExpandingIterator(Comparator<? super T> comp, Iterable<T>... inputs) {
		this.comp = comp;
		this.minHeap = new PriorityQueue<Item<T>>(inputs.length);
		this.inputs = new Iterator[inputs.length];
		for (int i = 0; i < inputs.length; i++)
			this.inputs[i] = inputs[i].iterator();
		// Prime the heap
		for (int i = 0; i < inputs.length; i++)
			replaceItem(i);
	}

	private void replaceItem(int index) {
		Iterator<T> it = inputs[index];
		if (it.hasNext()) {
			T tuple = it.next();
			minHeap.add(new Item<T>(index, tuple, comp));
		}
	}

	@Override
	public boolean hasNext() {
		return (minHeap.peek() != null);
	}

	@Override
	public T next() {
		if (!hasNext()) throw new NoSuchElementException();

		Item<T> curr = minHeap.poll();
		// Read replacement item
		replaceItem(curr.getIndex());

		return curr.getTuple();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("SpillSortIterator.remove");
	}

	private final class Item<U> implements Comparable<Item<U>> {
		private final int index;
		private final U tuple;
		private final Comparator<? super U> c;

		public Item(int index, U tuple, Comparator<? super U> c) {
			this.index = index;
			this.tuple = tuple;
			this.c = c;
		}

		public int getIndex() {
			return index;
		}

		public U getTuple() {
			return tuple;
		}

		@Override
		@SuppressWarnings("unchecked")
		public int compareTo(Item<U> o) {
			return (null != c) ? c.compare(tuple, o.getTuple()) : ((Comparable<U>) tuple).compareTo(o.getTuple());
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Item) return compareTo((Item<U>) obj) == 0;

			return false;
		}

		@Override
		public int hashCode() {
			return tuple.hashCode();
		}
	}

}
