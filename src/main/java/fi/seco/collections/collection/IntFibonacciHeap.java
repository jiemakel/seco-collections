package fi.seco.collections.collection;

import java.util.AbstractCollection;
import java.util.HashMap;
import java.util.Iterator;

public class IntFibonacciHeap<T> extends AbstractCollection<T> {
	private FibonacciHeapNode<T> min;
	private final HashMap<T, FibonacciHeapNode<T>> itemsToNodes;

	private static class FibonacciHeapNode<T> {
		private final T userObject;
		private int priority;

		private FibonacciHeapNode<T> parent;
		private FibonacciHeapNode<T> prevSibling;
		private FibonacciHeapNode<T> nextSibling;
		private FibonacciHeapNode<T> child;
		private int degree;
		private boolean mark;

		FibonacciHeapNode(T userObject, int priority) {
			this.userObject = userObject;
			this.priority = priority;

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

	public IntFibonacciHeap() {
		this.min = null;
		this.itemsToNodes = new HashMap<T, FibonacciHeapNode<T>>();
	}

	public void add(T item, int priority) {
		FibonacciHeapNode<T> ex = itemsToNodes.get(item);
		if (ex != null)
			decreaseKey(ex, priority);
		else {
			FibonacciHeapNode<T> newNode = new FibonacciHeapNode<T>(item, priority);
			itemsToNodes.put(item, newNode);

			if (min == null)
				min = newNode;
			else {
				concatenateSiblings(newNode, min);
				if (newNode.priority < min.priority) min = newNode;
			}
		}
	}

	@Override
	public boolean contains(Object item) {
		return itemsToNodes.containsKey(item);
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

	public int peekFirstPriority() {
		if (min == null) return 0;
		return min.priority;
	}

	public T peekFirst() {
		if (min == null) return null;
		return min.userObject;
	}

	@Override
	public int size() {
		return itemsToNodes.size();
	}

	@Override
	public Iterator<T> iterator() {
		return itemsToNodes.keySet().iterator();
	}

	public T removeFirst() {
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
		itemsToNodes.remove(oldMin.userObject);
		return oldMin.userObject;
	}

	@SuppressWarnings("unchecked")
	private void consolidate() {
		int size = size();
		FibonacciHeapNode<T>[] newRoots = new FibonacciHeapNode[size];

		FibonacciHeapNode<T> node = min;
		FibonacciHeapNode<T> start = min;
		do {
			FibonacciHeapNode<T> x = node;
			int currDegree = node.degree;
			while (newRoots[currDegree] != null) {
				FibonacciHeapNode<T> y = newRoots[currDegree];
				if (x.priority > y.priority) {
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
			if (newRoots[i] != null) if ((min == null) || (newRoots[i].priority < min.priority)) min = newRoots[i];
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

	public void addOrDecreaseKey(T item) {
		FibonacciHeapNode<T> node = itemsToNodes.get(item);
		if (node == null)
			add(item, -1);
		else decreaseKey(item, -1);
	}

	public void addOrDecreaseKey(T item, int priority) {
		FibonacciHeapNode<T> node = itemsToNodes.get(item);
		if (node == null)
			add(item, priority);
		else decreaseKey(item, priority);
	}

	public void decreaseKey(T item, int priority) {
		FibonacciHeapNode<T> node = itemsToNodes.get(item);
		if (node == null) throw new IllegalStateException("No such element: " + item);
		decreaseKey(node, priority);
	}

	@Override
	public boolean remove(Object item) {
		FibonacciHeapNode<T> node = itemsToNodes.get(item);
		if (node == null) return false;
		decreaseKey(node, Integer.MIN_VALUE);
		removeFirst();
		return true;
	}

	private void decreaseKey(FibonacciHeapNode<T> node, int priority) {
		if (node.priority < priority)
			throw new IllegalStateException("decreaseKey(" + node + ", " + priority + ") called, but priority=" + node.priority);
		node.priority = priority;
		FibonacciHeapNode<T> parent = node.parent;
		if ((parent != null) && (node.priority < parent.priority)) {
			cut(node, parent);
			cascadingCut(parent);
		}
		if (node.priority < min.priority) min = node;
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

	@Override
	public boolean isEmpty() {
		return min == null;
	}

}
