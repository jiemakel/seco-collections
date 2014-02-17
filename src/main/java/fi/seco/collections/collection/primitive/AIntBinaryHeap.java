package fi.seco.collections.collection.primitive;

import com.carrotsearch.hppc.cursors.LongCursor;
import com.carrotsearch.hppc.predicates.IntPredicate;
import com.carrotsearch.hppc.procedures.IntProcedure;

import fi.seco.collections.iterator.AIterableIterator;
import fi.seco.collections.iterator.IIterableIterator;

public abstract class AIntBinaryHeap {
	private final int[] heap;
	private int lastIndex;
	private final int maxIndex;

	protected abstract boolean lessThan(int a, int b);

	public AIntBinaryHeap(int maxSize) {
		lastIndex = -1;
		heap = new int[maxSize];
		maxIndex = maxSize - 1;
	}

	public AIntBinaryHeap(int[] a) {
		lastIndex = a.length - 1;
		maxIndex = lastIndex;
		heap = a;
		heapify();
	}

	public AIntBinaryHeap(int[] a, int size) {
		lastIndex = size - 1;
		maxIndex = lastIndex;
		heap = a;
		heapify();
	}

	public final void add(int element) {
		lastIndex++;
		heap[lastIndex] = element;
		upHeap();
	}

	public final boolean putIfSpace(int element) {
		if (lastIndex < maxIndex) {
			add(element);
			return true;
		} else if (!lessThan(element, peekFirst())) {
			heap[0] = element;
			downHeap(0);
			return true;
		} else return false;
	}

	public final int peekFirst() {
		return heap[0];
	}

	public final int removeFirst() {
		int result = heap[0];
		heap[0] = heap[lastIndex];
		lastIndex--;
		downHeap(0);
		return result;
	}

	public final int size() {
		return lastIndex + 1;
	}

	public final void clear() {
		lastIndex = -1;
	}

	private final void upHeap() {
		int i = lastIndex;
		int node = heap[i];
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
		int node = heap[index];
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
		for (int i = 1; i < lastIndex; i++)
			downHeap(i);
		/*		int li = lastIndex;
				while (lastIndex >= 0) {
					int tmp = heap[0];
					heap[0] = heap[lastIndex];
					heap[lastIndex] = tmp;
					lastIndex--;
					downHeap(0);
				}
				lastIndex = li; */
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

	public <T extends IntProcedure> T forEach(T p, boolean reverse) {
		if (!reverse)
			for (int i = 0; i <= lastIndex; i++)
				p.apply(heap[i]);
		else for (int i = lastIndex; i >= 0; i--)
			p.apply(heap[i]);
		return p;
	}

	public <T extends IntPredicate> T forEach(T p, boolean reverse) {
		if (!reverse) {
			for (int i = 0; i <= lastIndex; i++)
				if (!p.apply(heap[i])) return p;
		} else for (int i = lastIndex; i >= 0; i--)
			if (!p.apply(heap[i])) return p;
		return p;
	}

}