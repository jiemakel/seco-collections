package fi.seco.collections.collection.primitive;

import com.carrotsearch.hppc.cursors.FloatCursor;
import com.carrotsearch.hppc.predicates.FloatPredicate;
import com.carrotsearch.hppc.procedures.FloatProcedure;

import fi.seco.collections.iterator.AIterableIterator;
import fi.seco.collections.iterator.IIterableIterator;

public abstract class AFloatBinaryHeap {
	private final float[] heap;
	private int lastIndex;
	private final int maxIndex;

	protected abstract boolean lessThan(float a, float b);

	public AFloatBinaryHeap(int maxSize) {
		lastIndex = -1;
		heap = new float[maxSize];
		maxIndex = maxSize - 1;
	}

	public AFloatBinaryHeap(float[] a) {
		lastIndex = a.length - 1;
		maxIndex = lastIndex;
		heap = a;
		heapify();
	}

	public AFloatBinaryHeap(float[] a, int size) {
		lastIndex = size - 1;
		maxIndex = lastIndex;
		heap = a;
		heapify();
	}

	public final void add(float element) {
		lastIndex++;
		heap[lastIndex] = element;
		upHeap();
	}

	public final boolean putIfSpace(float element) {
		if (lastIndex < maxIndex) {
			add(element);
			return true;
		} else if (!lessThan(element, peekFirst())) {
			heap[0] = element;
			downHeap(0);
			return true;
		} else return false;
	}

	public final float peekFirst() {
		return heap[0];
	}

	public final float removeFirst() {
		float result = heap[0];
		heap[0] = heap[lastIndex];
		lastIndex--;
		downHeap(0);
		return result;
	}

	public final float size() {
		return lastIndex + 1;
	}

	public final void clear() {
		lastIndex = -1;
	}

	private final void upHeap() {
		int i = lastIndex;
		float node = heap[i];
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
		float node = heap[index];
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
		/*		float li = lastIndex;
				while (lastIndex >= 0) {
					float tmp = heap[0];
					heap[0] = heap[lastIndex];
					heap[lastIndex] = tmp;
					lastIndex--;
					downHeap(0);
				}
				lastIndex = li; */
	}

	public IIterableIterator<FloatCursor> iterator() {
		return new AIterableIterator<FloatCursor>() {

			int ci = 0;

			@Override
			public boolean hasNext() {
				return ci <= lastIndex;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

			private final FloatCursor lc = new FloatCursor();

			@Override
			public FloatCursor next() {
				lc.index = ci;
				lc.value = heap[ci++];
				return lc;
			}

		};
	}

	public IIterableIterator<FloatCursor> reverseIterator() {
		return new AIterableIterator<FloatCursor>() {

			int ci = lastIndex;

			@Override
			public boolean hasNext() {
				return ci >= 0;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

			private final FloatCursor lc = new FloatCursor();

			@Override
			public FloatCursor next() {
				lc.index = ci;
				lc.value = heap[ci--];
				return lc;
			}

		};
	}

	public <T extends FloatProcedure> T forEach(T p, boolean reverse) {
		if (!reverse)
			for (int i = 0; i <= lastIndex; i++)
				p.apply(heap[i]);
		else for (int i = lastIndex; i >= 0; i--)
			p.apply(heap[i]);
		return p;
	}

	public <T extends FloatPredicate> T forEach(T p, boolean reverse) {
		if (!reverse) {
			for (int i = 0; i <= lastIndex; i++)
				if (!p.apply(heap[i])) return p;
		} else for (int i = lastIndex; i >= 0; i--)
			if (!p.apply(heap[i])) return p;
		return p;
	}

}