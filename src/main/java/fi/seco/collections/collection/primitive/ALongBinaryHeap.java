package fi.seco.collections.collection.primitive;

import com.carrotsearch.hppc.cursors.LongCursor;
import com.carrotsearch.hppc.predicates.LongPredicate;
import com.carrotsearch.hppc.procedures.LongProcedure;

import fi.seco.collections.iterator.AIterableIterator;
import fi.seco.collections.iterator.IIterableIterator;

public abstract class ALongBinaryHeap implements Iterable<LongCursor> {
	private final long[] heap;
	private int lastIndex;
	private final int maxIndex;

	protected abstract boolean lessThan(long a, long b);

	public ALongBinaryHeap(int maxSize) {
		lastIndex = -1;
		heap = new long[maxSize];
		maxIndex = maxSize - 1;
	}

	public ALongBinaryHeap(long[] a) {
		lastIndex = a.length - 1;
		maxIndex = lastIndex;
		heap = a;
		heapify();
	}

	public ALongBinaryHeap(long[] a, int size) {
		lastIndex = size - 1;
		maxIndex = lastIndex;
		heap = a;
		heapify();
	}

	public final void add(long element) {
		lastIndex++;
		heap[lastIndex] = element;
		upHeap();
	}

	public final boolean putIfSpace(long element) {
		if (lastIndex < maxIndex) {
			add(element);
			return true;
		} else if (!lessThan(element, peekFirst())) {
			heap[0] = element;
			downHeap(0);
			return true;
		} else return false;
	}

	public final long peekFirst() {
		return heap[0];
	}

	public final long removeFirst() {
		long result = heap[0];
		heap[0] = heap[lastIndex];
		lastIndex--;
		downHeap(0);
		return result;
	}

	public final long size() {
		return lastIndex + 1;
	}

	public final void clear() {
		lastIndex = -1;
	}

	private final void upHeap() {
		int i = lastIndex;
		long node = heap[i];
		int parent = (i - 1) >> 1;
		while (parent >= 0 && lessThan(node, heap[parent])) {
			heap[i] = heap[parent];
			i = parent;
			parent = (parent - 1) >> 1;
		}
		heap[i] = node;
	}

	private void heapify() {
		for (int i = (lastIndex >> 1); i >= 0; i--)
			downHeap(i);
	}

	private final void downHeap(int index) {
		long node = heap[index];
		int childIndex = (index << 1) + 1;
		int otherChildIndex = childIndex + 1;
		if (otherChildIndex <= lastIndex && lessThan(heap[otherChildIndex], heap[childIndex]))
			childIndex = otherChildIndex;
		while (childIndex <= lastIndex && lessThan(heap[childIndex], node)) {
			heap[index] = heap[childIndex];
			index = childIndex;
			childIndex = (index << 1) + 1;
			otherChildIndex = childIndex + 1;
			if (otherChildIndex <= lastIndex && lessThan(heap[otherChildIndex], heap[childIndex]))
				childIndex = otherChildIndex;
		}
		heap[index] = node;
	}

	public final boolean isEmpty() {
		return lastIndex == -1;
	}

	public void sort() {
		int li = lastIndex;
		while (lastIndex >= 0) {
			long tmp = heap[0];
			heap[0] = heap[lastIndex];
			heap[lastIndex] = tmp;
			lastIndex--;
			downHeap(0);
		}
		lastIndex = li;
	}

	public IIterableIterator<LongCursor> iterator() {
		return new AIterableIterator<LongCursor>() {

			int ci = 0;

			@Override
			public boolean hasNext() {
				return ci <= lastIndex;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

			private final LongCursor lc = new LongCursor();

			@Override
			public LongCursor next() {
				lc.index = ci;
				lc.value = heap[ci++];
				return lc;
			}

		};
	}

	public IIterableIterator<LongCursor> reverseIterator() {
		return new AIterableIterator<LongCursor>() {

			private final LongCursor lc = new LongCursor();

			int ci = lastIndex;

			@Override
			public boolean hasNext() {
				return ci >= 0;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

			@Override
			public LongCursor next() {
				lc.index = ci;
				lc.value = heap[ci--];
				return lc;
			}

		};
	}

	public <T extends LongProcedure> T forEach(T p, boolean reverse) {
		if (!reverse)
			for (int i = 0; i <= lastIndex; i++)
				p.apply(heap[i]);
		else for (int i = lastIndex; i >= 0; i--)
			p.apply(heap[i]);
		return p;
	}

	public <T extends LongPredicate> T forEach(T p, boolean reverse) {
		if (!reverse) {
			for (int i = 0; i <= lastIndex; i++)
				if (!p.apply(heap[i])) return p;
		} else for (int i = lastIndex; i >= 0; i--)
			if (!p.apply(heap[i])) return p;
		return p;
	}

}