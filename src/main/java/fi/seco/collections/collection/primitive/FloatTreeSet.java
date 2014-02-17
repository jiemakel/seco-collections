package fi.seco.collections.collection.primitive;

import java.util.Iterator;

import com.carrotsearch.hppc.cursors.FloatCursor;

import fi.seco.collections.iterator.AIterableIterator;
import fi.seco.collections.iterator.IIterableIterator;

public class FloatTreeSet implements IFloatTreeSet {

	@Override
	public int compare(float key1, float key2) {
		return (int) (key1 - key2);
	}

	private transient Entry root = null;

	@Override
	public float getFirst() {
		return keyOrZero(getFirstEntry());
	}

	@Override
	public float getLast() {
		return keyOrZero(getLastEntry());
	}

	private abstract class EntryIterator extends AIterableIterator<FloatCursor> {
		Entry next;
		Entry current;

		protected FloatCursor lc = new FloatCursor();

		private EntryIterator(Entry first) {
			next = first;
		}

		@Override
		public boolean hasNext() {
			return next != null;
		}

		protected final float nextEntry() {
			Entry e = next;
			next = successor(e);
			current = e;
			return e.getKey();
		}

		protected final float prevEntry() {
			Entry e = next;
			next = predecessor(e);
			current = e;
			return e.getKey();
		}

		@Override
		public abstract void remove();

		public void removeAscending() {
			if (current.left != null && current.right != null) next = current;
			removeEntry(current);
			current = null;
		}

		public void removeDescending() {
			removeEntry(current);
			current = null;
		}
	}

	@Override
	public IIterableIterator<FloatCursor> iterator() {
		return new EntryIterator(getFirstEntry()) {

			@Override
			public FloatCursor next() {
				lc.value = nextEntry();
				return lc;
			}

			@Override
			public void remove() {
				removeAscending();
			}
		};
	}

	@Override
	public Iterator<FloatCursor> descendingIterator() {
		return new EntryIterator(getLastEntry()) {

			@Override
			public FloatCursor next() {
				lc.value = prevEntry();
				return lc;
			}

			@Override
			public void remove() {
				removeDescending();
			}
		};
	}

	private final Entry getCeilingEntry(float key) {
		Entry p = root;
		while (p != null) {
			int cmp = compare(key, p.key);
			if (cmp < 0) {
				if (p.left != null)
					p = p.left;
				else return p;
			} else if (cmp > 0) {
				if (p.right != null)
					p = p.right;
				else {
					Entry parent = p.parent;
					Entry ch = p;
					while (parent != null && ch == parent.right) {
						ch = parent;
						parent = parent.parent;
					}
					return parent;
				}
			} else return p;
		}
		return null;
	}

	private final Entry getFloorEntry(float key) {
		Entry p = root;
		while (p != null) {
			int cmp = compare(key, p.key);
			if (cmp > 0) {
				if (p.right != null)
					p = p.right;
				else return p;
			} else if (cmp < 0) {
				if (p.left != null)
					p = p.left;
				else {
					Entry parent = p.parent;
					Entry ch = p;
					while (parent != null && ch == parent.left) {
						ch = parent;
						parent = parent.parent;
					}
					return parent;
				}
			} else return p;

		}
		return null;
	}

	private final Entry getHigherEntry(float key) {
		Entry p = root;
		while (p != null) {
			int cmp = compare(key, p.key);
			if (cmp < 0) {
				if (p.left != null)
					p = p.left;
				else return p;
			} else if (p.right != null)
				p = p.right;
			else {
				Entry parent = p.parent;
				Entry ch = p;
				while (parent != null && ch == parent.right) {
					ch = parent;
					parent = parent.parent;
				}
				return parent;
			}
		}
		return null;
	}

	private final Entry getLowerEntry(float key) {
		Entry p = root;
		while (p != null) {
			int cmp = compare(key, p.key);
			if (cmp > 0) {
				if (p.right != null)
					p = p.right;
				else return p;
			} else if (p.left != null)
				p = p.left;
			else {
				Entry parent = p.parent;
				Entry ch = p;
				while (parent != null && ch == parent.left) {
					ch = parent;
					parent = parent.parent;
				}
				return parent;
			}
		}
		return null;
	}

	@Override
	public boolean add(float key) {
		Entry t = root;
		if (t == null) {
			root = new Entry(key, null);
			size = 1;
			return true;
		}
		int cmp;
		Entry parent;
		do {
			parent = t;
			cmp = compare(key, t.key);
			if (cmp < 0)
				t = t.left;
			else if (cmp > 0)
				t = t.right;
			else return false;
		} while (t != null);
		Entry e = new Entry(key, parent);
		if (cmp < 0)
			parent.left = e;
		else parent.right = e;
		fixAfterInsertion(e);
		size++;
		return true;
	}

	private final Entry getEntry(float key) {
		Entry p = root;
		while (p != null) {
			int cmp = compare(key, p.key);
			if (cmp < 0)
				p = p.left;
			else if (cmp > 0)
				p = p.right;
			else return p;
		}
		return null;
	}

	@Override
	public boolean contains(float key) {
		return getEntry(key) != null;
	}

	@Override
	public boolean remove(float key) {
		Entry p = getEntry(key);
		if (p == null) return false;
		removeEntry(p);
		return true;
	}

	@Override
	public void clear() {
		size = 0;
		root = null;
	}

	@Override
	public float removeFirst() {
		Entry p = getFirstEntry();
		if (p != null) removeEntry(p);
		return keyOrZero(p);
	}

	@Override
	public float removeLast() {
		Entry p = getLastEntry();
		if (p != null) removeEntry(p);
		return keyOrZero(p);
	}

	private static final float keyOrZero(Entry e) {
		return e != null ? e.key : Float.MIN_VALUE;
	}

	@Override
	public float getLower(float key) {
		return keyOrZero(getLowerEntry(key));
	}

	@Override
	public float getFloor(float key) {
		return keyOrZero(getFloorEntry(key));
	}

	@Override
	public float getCeiling(float key) {
		return keyOrZero(getCeilingEntry(key));
	}

	@Override
	public float getHigher(float key) {
		return keyOrZero(getHigherEntry(key));
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public int size() {
		return size;
	}

	private class SubSet implements IFloatTreeSet {
		final FloatTreeSet m;

		final float lo, hi;
		final boolean fromStart, toEnd;
		final boolean loInclusive, hiInclusive;

		SubSet(FloatTreeSet m, boolean fromStart, float lo, boolean loInclusive, boolean toEnd, float hi,
				boolean hiInclusive) {
			if (!fromStart && !toEnd) {
				if (m.compare(lo, hi) > 0) throw new IllegalArgumentException("fromKey > toKey");
			} else {
				if (!fromStart) // type check
					m.compare(lo, lo);
				if (!toEnd) m.compare(hi, hi);
			}

			this.m = m;
			this.fromStart = fromStart;
			this.lo = lo;
			this.loInclusive = loInclusive;
			this.toEnd = toEnd;
			this.hi = hi;
			this.hiInclusive = hiInclusive;
		}

		// internal utilities

		final boolean tooLow(float key) {
			if (!fromStart) {
				int c = m.compare(key, lo);
				if (c < 0 || (c == 0 && !loInclusive)) return true;
			}
			return false;
		}

		final boolean tooHigh(float key) {
			if (!toEnd) {
				int c = m.compare(key, hi);
				if (c > 0 || (c == 0 && !hiInclusive)) return true;
			}
			return false;
		}

		final boolean inRange(float key) {
			return !tooLow(key) && !tooHigh(key);
		}

		final boolean inClosedRange(float key) {
			return (fromStart || m.compare(key, lo) >= 0) && (toEnd || m.compare(hi, key) >= 0);
		}

		final boolean inRange(float key, boolean inclusive) {
			return inclusive ? inRange(key) : inClosedRange(key);
		}

		@Override
		public final Entry getFirstEntry() {
			Entry e = (fromStart ? m.getFirstEntry() : (loInclusive ? m.getCeilingEntry(lo) : m.getHigherEntry(lo)));
			return (e == null || tooHigh(e.key)) ? null : e;
		}

		@Override
		public final float getFirst() {
			return keyOrZero(getFirstEntry());
		}

		@Override
		public final Entry getLastEntry() {
			Entry e = (toEnd ? m.getLastEntry() : (hiInclusive ? m.getFloorEntry(hi) : m.getLowerEntry(hi)));
			return (e == null || tooLow(e.key)) ? null : e;
		}

		@Override
		public final float getLast() {
			return keyOrZero(getLastEntry());
		}

		private final Entry getCeilingEntry(float key) {
			if (tooLow(key)) return getFirstEntry();
			Entry e = m.getCeilingEntry(key);
			return (e == null || tooHigh(e.key)) ? null : e;
		}

		@Override
		public final float getCeiling(float key) {
			return keyOrZero(getCeilingEntry(key));
		}

		private final Entry getHigherEntry(float key) {
			if (tooLow(key)) return getFirstEntry();
			Entry e = m.getHigherEntry(key);
			return (e == null || tooHigh(e.key)) ? null : e;
		}

		@Override
		public final float getHigher(float key) {
			return keyOrZero(getHigherEntry(key));
		}

		public final Entry getFloorEntry(float key) {
			if (tooHigh(key)) return getLastEntry();
			Entry e = m.getFloorEntry(key);
			return (e == null || tooLow(e.key)) ? null : e;
		}

		@Override
		public final float getFloor(float key) {
			return keyOrZero(getFloorEntry(key));
		}

		private final Entry getLowerEntry(float key) {
			if (tooHigh(key)) return getLastEntry();
			Entry e = m.getLowerEntry(key);
			return (e == null || tooLow(e.key)) ? null : e;
		}

		@Override
		public final float getLower(float key) {
			return keyOrZero(getLowerEntry(key));
		}

		private final Entry absHighFence() {
			return (toEnd ? null : (hiInclusive ? m.getHigherEntry(hi) : m.getCeilingEntry(hi)));
		}

		private final Entry absLowFence() {
			return (fromStart ? null : (loInclusive ? m.getLowerEntry(lo) : m.getFloorEntry(lo)));
		}

		@Override
		public boolean isEmpty() {
			if (fromStart && toEnd) return m.isEmpty();
			Entry n = getFirstEntry();
			return n == null || tooHigh(n.key);
		}

		private int size = -1;

		@Override
		public int size() {
			if (fromStart && toEnd) return m.size();
			if (size != -1) return size;
			size = 0;
			Iterator<FloatCursor> i = iterator();
			while (i.hasNext()) {
				size++;
				i.next();
			}
			return size;
		}

		@Override
		public final boolean contains(float key) {
			return inRange(key) && m.contains(key);
		}

		@Override
		public final boolean add(float key) {
			return m.add(key);
		}

		@Override
		public final boolean remove(float key) {
			return inRange(key) && m.remove(key);
		}

		public final float firstKey() {
			return keyOrZero(getFirstEntry());
		}

		public final float lastKey() {
			return keyOrZero(getLastEntry());
		}

		@Override
		public final float removeFirst() {
			Entry e = getFirstEntry();
			if (e != null) m.removeEntry(e);
			return keyOrZero(e);
		}

		@Override
		public final float removeLast() {
			Entry e = getLastEntry();
			if (e != null) m.removeEntry(e);
			return keyOrZero(e);
		}

		@Override
		public void clear() {
			m.clear();
		}

		@Override
		public int compare(float key1, float key2) {
			return m.compare(key1, key2);
		}

		private abstract class SubMapEntryIterator extends EntryIterator {
			private final float fenceKey;

			SubMapEntryIterator(Entry first, Entry fence) {
				super(first);
				fenceKey = fence.key;
			}

			@Override
			public final boolean hasNext() {
				return next != null && next.key != fenceKey;
			}

		}

		@Override
		public Iterator<FloatCursor> iterator() {
			return new SubMapEntryIterator(getFirstEntry(), absHighFence()) {

				@Override
				public void remove() {
					removeAscending();
				}

				@Override
				public FloatCursor next() {
					lc.value = nextEntry();
					return lc;
				}
			};
		}

		@Override
		public Iterator<FloatCursor> descendingIterator() {
			return new SubMapEntryIterator(getLastEntry(), absLowFence()) {
				@Override
				public void remove() {
					removeDescending();
				}

				@Override
				public FloatCursor next() {
					lc.value = prevEntry();
					return lc;
				}
			};
		}

		@Override
		public IFloatTreeSet subSet(float fromKey, boolean fromInclusive, float toKey, boolean toInclusive) {
			return new SubSet(m, false, fromKey, fromInclusive, false, toKey, toInclusive);
		}

		@Override
		public IFloatTreeSet headSet(float toKey, boolean inclusive) {
			return new SubSet(m, fromStart, lo, loInclusive, false, toKey, inclusive);
		}

		@Override
		public IFloatTreeSet tailSet(float fromKey, boolean inclusive) {
			return new SubSet(m, false, fromKey, inclusive, toEnd, hi, hiInclusive);
		}

		@Override
		public void removeEntry(Entry e) {
			m.removeEntry(e);
		}
	}

	@Override
	public IFloatTreeSet subSet(float fromKey, boolean fromInclusive, float toKey, boolean toInclusive) {
		return new SubSet(this, false, fromKey, fromInclusive, false, toKey, toInclusive);
	}

	@Override
	public IFloatTreeSet headSet(float toKey, boolean inclusive) {
		return new SubSet(this, true, Integer.MIN_VALUE, true, false, toKey, inclusive);
	}

	@Override
	public IFloatTreeSet tailSet(float fromKey, boolean inclusive) {
		return new SubSet(this, false, fromKey, inclusive, true, Integer.MAX_VALUE, true);
	}

	/**
	 * The number of entries in the tree
	 */
	private transient int size = 0;

	// Red-black mechanics

	/**
	 * Returns the first Entry in the TreeMap (according to the TreeMap's
	 * key-sort function). Returns null if the TreeMap is empty.
	 */
	@Override
	public final Entry getFirstEntry() {
		Entry p = root;
		if (p != null) while (p.left != null)
			p = p.left;
		return p;
	}

	/**
	 * Returns the last Entry in the TreeMap (according to the TreeMap's
	 * key-sort function). Returns null if the TreeMap is empty.
	 */
	@Override
	public final Entry getLastEntry() {
		Entry p = root;
		if (p != null) while (p.right != null)
			p = p.right;
		return p;
	}

	static Entry successor(Entry t) {
		if (t == null)
			return null;
		else if (t.right != null) {
			Entry p = t.right;
			while (p.left != null)
				p = p.left;
			return p;
		} else {
			Entry p = t.parent;
			Entry ch = t;
			while (p != null && ch == p.right) {
				ch = p;
				p = p.parent;
			}
			return p;
		}
	}

	static Entry predecessor(Entry t) {
		if (t == null)
			return null;
		else if (t.left != null) {
			Entry p = t.left;
			while (p.right != null)
				p = p.right;
			return p;
		} else {
			Entry p = t.parent;
			Entry ch = t;
			while (p != null && ch == p.left) {
				ch = p;
				p = p.parent;
			}
			return p;
		}
	}

	private static boolean colorOf(Entry p) {
		return (p == null ? Entry.BLACK : p.color);
	}

	private static Entry parentOf(Entry p) {
		return (p == null ? null : p.parent);
	}

	private static void setColor(Entry p, boolean c) {
		if (p != null) p.color = c;
	}

	private static Entry leftOf(Entry p) {
		return (p == null) ? null : p.left;
	}

	private static Entry rightOf(Entry p) {
		return (p == null) ? null : p.right;
	}

	/** From CLR */
	private void rotateLeft(Entry p) {
		if (p != null) {
			Entry r = p.right;
			p.right = r.left;
			if (r.left != null) r.left.parent = p;
			r.parent = p.parent;
			if (p.parent == null)
				root = r;
			else if (p.parent.left == p)
				p.parent.left = r;
			else p.parent.right = r;
			r.left = p;
			p.parent = r;
		}
	}

	/** From CLR */
	private void rotateRight(Entry p) {
		if (p != null) {
			Entry l = p.left;
			p.left = l.right;
			if (l.right != null) l.right.parent = p;
			l.parent = p.parent;
			if (p.parent == null)
				root = l;
			else if (p.parent.right == p)
				p.parent.right = l;
			else p.parent.left = l;
			l.right = p;
			p.parent = l;
		}
	}

	/** From CLR */
	private void fixAfterInsertion(Entry x) {
		x.color = Entry.RED;

		while (x != null && x != root && x.parent.color == Entry.RED)
			if (parentOf(x) == leftOf(parentOf(parentOf(x)))) {
				Entry y = rightOf(parentOf(parentOf(x)));
				if (colorOf(y) == Entry.RED) {
					setColor(parentOf(x), Entry.BLACK);
					setColor(y, Entry.BLACK);
					setColor(parentOf(parentOf(x)), Entry.RED);
					x = parentOf(parentOf(x));
				} else {
					if (x == rightOf(parentOf(x))) {
						x = parentOf(x);
						rotateLeft(x);
					}
					setColor(parentOf(x), Entry.BLACK);
					setColor(parentOf(parentOf(x)), Entry.RED);
					rotateRight(parentOf(parentOf(x)));
				}
			} else {
				Entry y = leftOf(parentOf(parentOf(x)));
				if (colorOf(y) == Entry.RED) {
					setColor(parentOf(x), Entry.BLACK);
					setColor(y, Entry.BLACK);
					setColor(parentOf(parentOf(x)), Entry.RED);
					x = parentOf(parentOf(x));
				} else {
					if (x == leftOf(parentOf(x))) {
						x = parentOf(x);
						rotateRight(x);
					}
					setColor(parentOf(x), Entry.BLACK);
					setColor(parentOf(parentOf(x)), Entry.RED);
					rotateLeft(parentOf(parentOf(x)));
				}
			}
		root.color = Entry.BLACK;
	}

	/**
	 * Delete node p, and then rebalance the tree.
	 */
	@Override
	public void removeEntry(Entry p) {
		size--;

		// If strictly internal, copy successor's element to p and then make p
		// point to successor.
		if (p.left != null && p.right != null) {
			Entry s = successor(p);
			p.key = s.key;
			p = s;
		} // p has 2 children

		// Start fixup at replacement node, if it exists.
		Entry replacement = (p.left != null ? p.left : p.right);

		if (replacement != null) {
			// Link replacement to parent
			replacement.parent = p.parent;
			if (p.parent == null)
				root = replacement;
			else if (p == p.parent.left)
				p.parent.left = replacement;
			else p.parent.right = replacement;

			// Null out links so they are OK to use by fixAfterDeletion.
			p.left = p.right = p.parent = null;

			// Fix replacement
			if (p.color == Entry.BLACK) fixAfterDeletion(replacement);
		} else if (p.parent == null)
			root = null;
		else { //  No children. Use self as phantom replacement and unlink.
			if (p.color == Entry.BLACK) fixAfterDeletion(p);

			if (p.parent != null) {
				if (p == p.parent.left)
					p.parent.left = null;
				else if (p == p.parent.right) p.parent.right = null;
				p.parent = null;
			}
		}
	}

	/** From CLR */
	private void fixAfterDeletion(Entry x) {
		while (x != root && colorOf(x) == Entry.BLACK)
			if (x == leftOf(parentOf(x))) {
				Entry sib = rightOf(parentOf(x));

				if (colorOf(sib) == Entry.RED) {
					setColor(sib, Entry.BLACK);
					setColor(parentOf(x), Entry.RED);
					rotateLeft(parentOf(x));
					sib = rightOf(parentOf(x));
				}

				if (colorOf(leftOf(sib)) == Entry.BLACK && colorOf(rightOf(sib)) == Entry.BLACK) {
					setColor(sib, Entry.RED);
					x = parentOf(x);
				} else {
					if (colorOf(rightOf(sib)) == Entry.BLACK) {
						setColor(leftOf(sib), Entry.BLACK);
						setColor(sib, Entry.RED);
						rotateRight(sib);
						sib = rightOf(parentOf(x));
					}
					setColor(sib, colorOf(parentOf(x)));
					setColor(parentOf(x), Entry.BLACK);
					setColor(rightOf(sib), Entry.BLACK);
					rotateLeft(parentOf(x));
					x = root;
				}
			} else { // symmetric
				Entry sib = leftOf(parentOf(x));

				if (colorOf(sib) == Entry.RED) {
					setColor(sib, Entry.BLACK);
					setColor(parentOf(x), Entry.RED);
					rotateRight(parentOf(x));
					sib = leftOf(parentOf(x));
				}

				if (colorOf(rightOf(sib)) == Entry.BLACK && colorOf(leftOf(sib)) == Entry.BLACK) {
					setColor(sib, Entry.RED);
					x = parentOf(x);
				} else {
					if (colorOf(leftOf(sib)) == Entry.BLACK) {
						setColor(rightOf(sib), Entry.BLACK);
						setColor(sib, Entry.RED);
						rotateLeft(sib);
						sib = leftOf(parentOf(x));
					}
					setColor(sib, colorOf(parentOf(x)));
					setColor(parentOf(x), Entry.BLACK);
					setColor(leftOf(sib), Entry.BLACK);
					rotateRight(parentOf(x));
					x = root;
				}
			}

		setColor(x, Entry.BLACK);
	}

}
