package fi.seco.collections.collection.primitive;

import com.carrotsearch.hppc.LongObjectMap;
import com.carrotsearch.hppc.LongObjectOpenHashMap;

public class LongFibonacciHeap {
	private Entry min;
	private final LongObjectMap<Entry> itemsToNodes;
	protected static float smallestPossible = Float.NEGATIVE_INFINITY;

	protected boolean isLessThan(float priority1, float priority2) {
		return priority1 < priority2;
	}

	public static class Entry {
		private final long userObject;
		private float priority;

		private Entry parent;
		private Entry prevSibling;
		private Entry nextSibling;
		private Entry child;
		private int degree;
		private boolean mark;

		Entry(long userObject, float priority) {
			this.userObject = userObject;
			this.priority = priority;

			parent = null;
			prevSibling = this;
			nextSibling = this;
			child = null;
			degree = 0;
			mark = false;
		}

		@Override
		public String toString() {
			return "[" + userObject + ", " + degree + "]";
		}

		public long get() {
			return userObject;
		}

		public float getPriority() {
			return priority;
		}
	}

	public LongFibonacciHeap() {
		min = null;
		itemsToNodes = new LongObjectOpenHashMap<Entry>();
	}

	public boolean add(long item, float priority) {
		Entry ex = itemsToNodes.get(item);
		if (ex != null) {
			if (ex.getPriority() > priority)
				decreaseKey(ex, priority);
			else if (ex.getPriority() < priority) increaseKey(ex, priority);
			return false;
		} else {
			Entry newNode = new Entry(item, priority);
			itemsToNodes.put(item, newNode);

			if (min == null)
				min = newNode;
			else {
				concatenateSiblings(newNode, min);
				if (isLessThan(newNode.priority, min.priority)) min = newNode;
			}
			return true;
		}
	}

	public boolean contains(int item) {
		return itemsToNodes.containsKey(item);
	}

	private void removeFromSiblings(Entry x) {
		if (x.nextSibling == x) return;
		x.nextSibling.prevSibling = x.prevSibling;
		x.prevSibling.nextSibling = x.nextSibling;
		x.nextSibling = x;
		x.prevSibling = x;
	}

	private void concatenateSiblings(Entry a, Entry b) {
		a.nextSibling.prevSibling = b;
		b.nextSibling.prevSibling = a;
		Entry origAnext = a.nextSibling;
		a.nextSibling = b.nextSibling;
		b.nextSibling = origAnext;
	}

	public long peekFirst() {
		if (min == null) return 0;
		return min.userObject;
	}

	public Entry peekFirstEntry() {
		return min;
	}

	public int size() {
		return itemsToNodes.size();
	}

	public long removeFirst() {
		return removeFirstEntry().userObject;
	}

	public Entry removeFirstEntry() {
		if (min == null) return null;
		if (min.child != null) {
			Entry tmp = min.child;
			while (tmp.parent != null) {
				tmp.parent = null;
				tmp = tmp.nextSibling;
			}
			concatenateSiblings(tmp, min);
		}
		Entry oldMin = min;
		if (min.nextSibling == min)
			min = null;
		else {
			min = min.nextSibling;
			removeFromSiblings(oldMin);
			consolidate();
		}
		itemsToNodes.remove(oldMin.userObject);
		return oldMin;
	}

	private void consolidate() {
		int size = size();
		Entry[] newRoots = new Entry[size];

		Entry node = min;
		Entry start = min;
		do {
			Entry x = node;
			int currDegree = node.degree;
			while (newRoots[currDegree] != null) {
				Entry y = newRoots[currDegree];
				if (!isLessThan(x.priority, y.priority)) {
					Entry tmp = x;
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
				if ((min == null) || (isLessThan(newRoots[i].priority, min.priority))) min = newRoots[i];
	}

	private void link(Entry y, Entry x) {
		removeFromSiblings(y);
		y.parent = x;
		if (x.child == null)
			x.child = y;
		else concatenateSiblings(x.child, y);
		x.degree++;
		y.mark = false;
	}

	private void remove(Entry node) {
		decreaseKey(node, Float.NEGATIVE_INFINITY);
		removeFirstEntry();
	}

	private void increaseKey(Entry node, float priority) {
		remove(node);
		add(node.userObject, priority);
	}

	private void decreaseKey(Entry node, float priority) {
		node.priority = priority;
		Entry parent = node.parent;
		if ((parent != null) && (isLessThan(node.priority, parent.priority))) {
			cut(node, parent);
			cascadingCut(parent);
		}
		if (isLessThan(node.priority, min.priority)) min = node;
	}

	private void cut(Entry x, Entry y) {
		if (y.child == x) y.child = x.nextSibling;
		if (y.child == x) y.child = null;

		y.degree--;
		removeFromSiblings(x);
		concatenateSiblings(x, min);
		x.parent = null;
		x.mark = false;

	}

	private void cascadingCut(Entry y) {
		Entry z = y.parent;
		if (z != null) if (!y.mark)
			y.mark = true;
		else {
			cut(y, z);
			cascadingCut(z);
		}
	}

	public boolean isEmpty() {
		return min == null;
	}

}
