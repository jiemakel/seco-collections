package fi.seco.collections.collection;

public abstract class AFibonacciHeap<T> {
	private FibonacciHeapNode<T> min;
	private int size = 0;

	protected abstract boolean lessThan(T first, T second);

	private static class FibonacciHeapNode<T> {
		private final T userObject;

		private FibonacciHeapNode<T> parent;
		private FibonacciHeapNode<T> prevSibling;
		private FibonacciHeapNode<T> nextSibling;
		private FibonacciHeapNode<T> child;
		private int degree;
		private boolean mark;

		FibonacciHeapNode(T userObject) {
			this.userObject = userObject;
			this.parent = null;
			this.prevSibling = this;
			this.nextSibling = this;
			this.child = null;
			this.degree = 0;
			this.mark = false;
		}

		@Override
		public String toString() {
			return "[" + userObject + ", " + degree + "]";
		}
	}

	public AFibonacciHeap() {
		this.min = null;
	}

	public void add(T item) {
		size++;
		FibonacciHeapNode<T> newNode = new FibonacciHeapNode<T>(item);
		if (min == null)
			min = newNode;
		else {
			concatenateSiblings(newNode, min);
			if (lessThan(newNode.userObject, min.userObject)) min = newNode;
		}
	}

	private void removeFromSiblings(FibonacciHeapNode<T> x) {
		if (x.nextSibling == x) return;
		x.nextSibling.prevSibling = x.prevSibling;
		x.prevSibling.nextSibling = x.nextSibling;
		x.nextSibling = x;
		x.prevSibling = x;
	}

	private void concatenateSiblings(FibonacciHeapNode<T> a, FibonacciHeapNode<T> b) {
		a.nextSibling.prevSibling = b;
		b.nextSibling.prevSibling = a;
		FibonacciHeapNode<T> origAnext = a.nextSibling;
		a.nextSibling = b.nextSibling;
		b.nextSibling = origAnext;
	}

	public T peekFirst() {
		if (min == null) return null;
		return min.userObject;
	}

	public boolean isEmpty() {
		return size == 0;
	}

	public T removeFirst() {
		size--;
		if (min == null) return null;
		if (min.child != null) {
			FibonacciHeapNode<T> tmp = min.child;
			while (tmp.parent != null) {
				tmp.parent = null;
				tmp = tmp.nextSibling;
			}
			concatenateSiblings(tmp, min);
		}
		FibonacciHeapNode<T> oldMin = min;
		if (min.nextSibling == min)
			min = null;
		else {
			min = min.nextSibling;
			removeFromSiblings(oldMin);
			consolidate();
		}
		return oldMin.userObject;
	}

	@SuppressWarnings("unchecked")
	private void consolidate() {
		FibonacciHeapNode<T>[] newRoots = new FibonacciHeapNode[size];

		FibonacciHeapNode<T> node = min;
		FibonacciHeapNode<T> start = min;
		do {
			FibonacciHeapNode<T> x = node;
			int currDegree = node.degree;
			while (newRoots[currDegree] != null) {
				FibonacciHeapNode<T> y = newRoots[currDegree];
				if (lessThan(y.userObject, x.userObject)) {
					FibonacciHeapNode<T> tmp = x;
					x = y;
					y = tmp;
				}
				if (y == start) start = start.nextSibling;
				if (y == node) node = node.prevSibling;
				link(y, x);
				newRoots[currDegree++] = null;
			}
			newRoots[currDegree] = x;
			node = node.nextSibling;
		} while (node != start);

		min = null;
		for (int i = 0; i < newRoots.length; i++)
			if (newRoots[i] != null)
				if ((min == null) || (lessThan(newRoots[i].userObject, min.userObject))) min = newRoots[i];
	}

	private void link(FibonacciHeapNode<T> y, FibonacciHeapNode<T> x) {
		removeFromSiblings(y);
		y.parent = x;
		if (x.child == null)
			x.child = y;
		else concatenateSiblings(x.child, y);
		x.degree++;
		y.mark = false;
	}

	private void cut(FibonacciHeapNode<T> x, FibonacciHeapNode<T> y) {
		if (y.child == x) y.child = x.nextSibling;
		if (y.child == x) y.child = null;

		y.degree--;
		removeFromSiblings(x);
		concatenateSiblings(x, min);
		x.parent = null;
		x.mark = false;

	}

	private void cascadingCut(FibonacciHeapNode<T> y) {
		FibonacciHeapNode<T> z = y.parent;
		if (z != null) if (!y.mark)
			y.mark = true;
		else {
			cut(y, z);
			cascadingCut(z);
		}
	}

}
