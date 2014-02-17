package fi.seco.collections.map.primitive;

import java.util.Iterator;

import com.carrotsearch.hppc.cursors.FloatObjectCursor;

import fi.seco.collections.iterator.AIterableIterator;
import fi.seco.collections.iterator.IIterableIterator;

public class FloatObjectTreeMap<T> implements IFloatObjectTreeMap<T> {

	@Override
	public int compare(float key1, float key2) {
		return (int) (key1 - key2);
	}

	private transient Entry<T> root = null;

	@Override
	public float getFirstKey() {
		return keyOrZero(getFirstEntry());
	}

	@Override
	public float getLastKey() {
		return keyOrZero(getLastEntry());
	}

	private abstract class EntryIterator extends AIterableIterator<FloatObjectCursor<T>> {
		Entry<T> next;
		Entry<T> current;

		protected FloatObjectCursor<T> lc = new FloatObjectCursor<T>();

		private EntryIterator(Entry<T> first) {
			next = first;
		}

		@Override
		public boolean hasNext() {
			return next != null;
		}

		protected final Entry<T> nextEntry() {
			Entry<T> e = next;
			next = successor(e);
			current = e;
			return e;
		}

		protected final Entry<T> prevEntry() {
			Entry<T> e = next;
			next = predecessor(e);
			current = e;
			return e;
		}

		@Override
		public abstract void remove();

		public void removeAscending() {
			if (current.left != null && current.right != null) next = current;
			deleteEntry(current);
			current = null;
		}

		public void removeDescending() {
			deleteEntry(current);
			current = null;
		}
	}

	@Override
	public IIterableIterator<FloatObjectCursor<T>> iterator() {
		return new EntryIterator(getFirstEntry()) {
			@Override
			public FloatObjectCursor<T> next() {
				Entry<T> e = nextEntry();
				lc.key = e.getKey();
				lc.value = e.getValue();
				return lc;
			}

			@Override
			public void remove() {
				removeAscending();
			}
		};
	}

	@Override
	public IIterableIterator<FloatObjectCursor<T>> descendingIterator() {
		return new EntryIterator(getLastEntry()) {
			@Override
			public FloatObjectCursor<T> next() {
				Entry<T> e = prevEntry();
				lc.key = e.getKey();
				lc.value = e.getValue();
				return lc;
			}

			@Override
			public void remove() {
				removeDescending();
			}
		};
	}

	@Override
	public final Entry<T> getCeilingEntry(float key) {
		Entry<T> p = root;
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
					Entry<T> parent = p.parent;
					Entry<T> ch = p;
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

	@Override
	public final Entry<T> getFloorEntry(float key) {
		Entry<T> p = root;
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
					Entry<T> parent = p.parent;
					Entry<T> ch = p;
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

	@Override
	public final Entry<T> getHigherEntry(float key) {
		Entry<T> p = root;
		while (p != null) {
			int cmp = compare(key, p.key);
			if (cmp < 0) {
				if (p.left != null)
					p = p.left;
				else return p;
			} else if (p.right != null)
				p = p.right;
			else {
				Entry<T> parent = p.parent;
				Entry<T> ch = p;
				while (parent != null && ch == parent.right) {
					ch = parent;
					parent = parent.parent;
				}
				return parent;
			}
		}
		return null;
	}

	@Override
	public final Entry<T> getLowerEntry(float key) {
		Entry<T> p = root;
		while (p != null) {
			int cmp = compare(key, p.key);
			if (cmp > 0) {
				if (p.right != null)
					p = p.right;
				else return p;
			} else if (p.left != null)
				p = p.left;
			else {
				Entry<T> parent = p.parent;
				Entry<T> ch = p;
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
	public T put(float key, T value) {
		Entry<T> t = root;
		if (t == null) {
			root = new Entry<T>(key, value, null);
			size = 1;
			return null;
		}
		int cmp;
		Entry<T> parent;
		do {
			parent = t;
			cmp = compare(key, t.key);
			if (cmp < 0)
				t = t.left;
			else if (cmp > 0)
				t = t.right;
			else return t.setValue(value);
		} while (t != null);
		Entry<T> e = new Entry<T>(key, value, parent);
		if (cmp < 0)
			parent.left = e;
		else parent.right = e;
		fixAfterInsertion(e);
		size++;
		return null;
	}

	@Override
	public final Entry<T> getEntry(float key) {
		Entry<T> p = root;
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
	public T get(float key) {
		Entry<T> p = getEntry(key);
		return p != null ? p.value : null;
	}

	@Override
	public T remove(float key) {
		Entry<T> p = getEntry(key);
		if (p == null) return null;
		T oldValue = p.value;
		deleteEntry(p);
		return oldValue;
	}

	@Override
	public void clear() {
		size = 0;
		root = null;
	}

	@Override
	public Entry<T> removeFirstEntry() {
		Entry<T> p = getFirstEntry();
		if (p != null) deleteEntry(p);
		return p;
	}

	@Override
	public Entry<T> removeLastEntry() {
		Entry<T> p = getLastEntry();
		if (p != null) deleteEntry(p);
		return p;
	}

	private static final <T> float keyOrZero(Entry<T> e) {
		return e != null ? e.key : 0.0f;
	}

	@Override
	public float getLowerKey(float key) {
		return keyOrZero(getLowerEntry(key));
	}

	@Override
	public float getFloorKey(float key) {
		return keyOrZero(getFloorEntry(key));
	}

	@Override
	public float getCeilingKey(float key) {
		return keyOrZero(getCeilingEntry(key));
	}

	@Override
	public float getHigherKey(float key) {
		return keyOrZero(getHigherEntry(key));
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public boolean containsKey(float key) {
		return getEntry(key) != null;
	}

	@Override
	public int size() {
		return size;
	}

	private class SubMap implements IFloatObjectTreeMap<T> {
		final IFloatObjectTreeMap<T> m;

		final float lo, hi;
		final boolean fromStart, toEnd;
		final boolean loInclusive, hiInclusive;

		SubMap(IFloatObjectTreeMap<T> m, boolean fromStart, float lo, boolean loInclusive, boolean toEnd, float hi,
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
		public final Entry<T> getFirstEntry() {
			Entry<T> e = (fromStart ? m.getFirstEntry() : (loInclusive ? m.getCeilingEntry(lo) : m.getHigherEntry(lo)));
			return (e == null || tooHigh(e.key)) ? null : e;
		}

		@Override
		public final Entry<T> getLastEntry() {
			Entry<T> e = (toEnd ? m.getLastEntry() : (hiInclusive ? m.getFloorEntry(hi) : m.getLowerEntry(hi)));
			return (e == null || tooLow(e.key)) ? null : e;
		}

		@Override
		public final Entry<T> getCeilingEntry(float key) {
			if (tooLow(key)) return getFirstEntry();
			Entry<T> e = m.getCeilingEntry(key);
			return (e == null || tooHigh(e.key)) ? null : e;
		}

		@Override
		public final float getCeilingKey(float key) {
			return keyOrZero(getCeilingEntry(key));
		}

		@Override
		public final Entry<T> getHigherEntry(float key) {
			if (tooLow(key)) return getFirstEntry();
			Entry<T> e = m.getHigherEntry(key);
			return (e == null || tooHigh(e.key)) ? null : e;
		}

		@Override
		public final float getHigherKey(float key) {
			return keyOrZero(getHigherEntry(key));
		}

		@Override
		public final Entry<T> getFloorEntry(float key) {
			if (tooHigh(key)) return getLastEntry();
			Entry<T> e = m.getFloorEntry(key);
			return (e == null || tooLow(e.key)) ? null : e;
		}

		@Override
		public final float getFloorKey(float key) {
			return keyOrZero(getFloorEntry(key));
		}

		@Override
		public final Entry<T> getLowerEntry(float key) {
			if (tooHigh(key)) return getLastEntry();
			Entry<T> e = m.getLowerEntry(key);
			return (e == null || tooLow(e.key)) ? null : e;
		}

		@Override
		public final float getLowerKey(float key) {
			return keyOrZero(getLowerEntry(key));
		}

		final Entry<T> absHighFence() {
			return (toEnd ? null : (hiInclusive ? m.getHigherEntry(hi) : m.getCeilingEntry(hi)));
		}

		final Entry<T> absLowFence() {
			return (fromStart ? null : (loInclusive ? m.getLowerEntry(lo) : m.getFloorEntry(lo)));
		}

		@Override
		public boolean isEmpty() {
			if (fromStart && toEnd) return m.isEmpty();
			Entry<T> n = getFirstEntry();
			return n == null || tooHigh(n.key);
		}

		private int size = -1;

		@Override
		public int size() {
			if (fromStart && toEnd) return m.size();
			if (size != -1) return size;
			size = 0;
			Iterator<FloatObjectCursor<T>> i = iterator();
			while (i.hasNext()) {
				size++;
				i.next();
			}
			return size;
		}

		@Override
		public final boolean containsKey(float key) {
			return inRange(key) && m.containsKey(key);
		}

		@Override
		public final T put(float key, T value) {
			return m.put(key, value);
		}

		@Override
		public final T get(float key) {
			return !inRange(key) ? null : m.get(key);
		}

		@Override
		public final T remove(float key) {
			return !inRange(key) ? null : m.remove(key);
		}

		@Override
		public final float getFirstKey() {
			return keyOrZero(getFirstEntry());
		}

		@Override
		public final float getLastKey() {
			return keyOrZero(getLastEntry());
		}

		@Override
		public final Entry<T> removeFirstEntry() {
			Entry<T> e = getFirstEntry();
			if (e != null) m.deleteEntry(e);
			return e;
		}

		@Override
		public final Entry<T> removeLastEntry() {
			Entry<T> e = getLastEntry();
			if (e != null) m.deleteEntry(e);
			return e;
		}

		@Override
		public void clear() {
			m.clear();
		}

		@Override
		public int compare(float key1, float key2) {
			return m.compare(key1, key2);
		}

		@Override
		public void deleteEntry(Entry<T> e) {
			m.deleteEntry(e);
		}

		@Override
		public Entry<T> getEntry(float key) {
			return inRange(key) ? m.getEntry(key) : null;
		}

		private abstract class SubMapEntryIterator extends EntryIterator {
			private final float fenceKey;

			SubMapEntryIterator(Entry<T> first, Entry<T> fence) {
				super(first);
				fenceKey = fence.key;
			}

			@Override
			public final boolean hasNext() {
				return next != null && next.key != fenceKey;
			}

		}

		@Override
		public IIterableIterator<FloatObjectCursor<T>> iterator() {
			return new SubMapEntryIterator(getFirstEntry(), absHighFence()) {
				@Override
				public void remove() {
					removeAscending();
				}

				@Override
				public FloatObjectCursor<T> next() {
					Entry<T> e = nextEntry();
					lc.key = e.getKey();
					lc.value = e.getValue();
					return lc;
				}
			};
		}

		@Override
		public IIterableIterator<FloatObjectCursor<T>> descendingIterator() {
			return new SubMapEntryIterator(getLastEntry(), absLowFence()) {
				@Override
				public void remove() {
					removeDescending();
				}

				@Override
				public FloatObjectCursor<T> next() {
					Entry<T> e = prevEntry();
					lc.key = e.getKey();
					lc.value = e.getValue();
					return lc;
				}
			};
		}

		@Override
		public IFloatObjectTreeMap<T> subMap(float fromKey, boolean fromInclusive, float toKey, boolean toInclusive) {
			return new SubMap(m, false, fromKey, fromInclusive, false, toKey, toInclusive);
		}

		@Override
		public IFloatObjectTreeMap<T> headMap(float toKey, boolean inclusive) {
			return new SubMap(m, fromStart, lo, loInclusive, false, toKey, inclusive);
		}

		@Override
		public IFloatObjectTreeMap<T> tailMap(float fromKey, boolean inclusive) {
			return new SubMap(m, false, fromKey, inclusive, toEnd, hi, hiInclusive);
		}
	}

	@Override
	public IFloatObjectTreeMap<T> subMap(float fromKey, boolean fromInclusive, float toKey, boolean toInclusive) {
		return new SubMap(this, false, fromKey, fromInclusive, false, toKey, toInclusive);
	}

	@Override
	public IFloatObjectTreeMap<T> headMap(float toKey, boolean inclusive) {
		return new SubMap(this, true, Integer.MIN_VALUE, true, false, toKey, inclusive);
	}

	@Override
	public IFloatObjectTreeMap<T> tailMap(float fromKey, boolean inclusive) {
		return new SubMap(this, false, fromKey, inclusive, true, Integer.MAX_VALUE, true);
	}

	/**
	 * The number of entries in the tree
	 */
	private transient int size = 0;

	// Red-black mechanics

	/**
	 * Returns the first Entry<T> in the TreeMap (according to the TreeMap's
	 * key-sort function). Returns null if the TreeMap is empty.
	 */
	@Override
	public final Entry<T> getFirstEntry() {
		Entry<T> p = root;
		if (p != null) while (p.left != null)
			p = p.left;
		return p;
	}

	/**
	 * Returns the last Entry<T> in the TreeMap (according to the TreeMap's
	 * key-sort function). Returns null if the TreeMap is empty.
	 */
	@Override
	public final Entry<T> getLastEntry() {
		Entry<T> p = root;
		if (p != null) while (p.right != null)
			p = p.right;
		return p;
	}

	static <T> Entry<T> successor(Entry<T> t) {
		if (t == null)
			return null;
		else if (t.right != null) {
			Entry<T> p = t.right;
			while (p.left != null)
				p = p.left;
			return p;
		} else {
			Entry<T> p = t.parent;
			Entry<T> ch = t;
			while (p != null && ch == p.right) {
				ch = p;
				p = p.parent;
			}
			return p;
		}
	}

	static <T> Entry<T> predecessor(Entry<T> t) {
		if (t == null)
			return null;
		else if (t.left != null) {
			Entry<T> p = t.left;
			while (p.right != null)
				p = p.right;
			return p;
		} else {
			Entry<T> p = t.parent;
			Entry<T> ch = t;
			while (p != null && ch == p.left) {
				ch = p;
				p = p.parent;
			}
			return p;
		}
	}

	private static <T> boolean colorOf(Entry<T> p) {
		return (p == null ? BLACK : p.color);
	}

	private static <T> Entry<T> parentOf(Entry<T> p) {
		return (p == null ? null : p.parent);
	}

	private static <T> void setColor(Entry<T> p, boolean c) {
		if (p != null) p.color = c;
	}

	private static <T> Entry<T> leftOf(Entry<T> p) {
		return (p == null) ? null : p.left;
	}

	private static <T> Entry<T> rightOf(Entry<T> p) {
		return (p == null) ? null : p.right;
	}

	/** From CLR */
	private void rotateLeft(Entry<T> p) {
		if (p != null) {
			Entry<T> r = p.right;
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
	private void rotateRight(Entry<T> p) {
		if (p != null) {
			Entry<T> l = p.left;
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
	private void fixAfterInsertion(Entry<T> x) {
		x.color = RED;

		while (x != null && x != root && x.parent.color == RED)
			if (parentOf(x) == leftOf(parentOf(parentOf(x)))) {
				Entry<T> y = rightOf(parentOf(parentOf(x)));
				if (colorOf(y) == RED) {
					setColor(parentOf(x), BLACK);
					setColor(y, BLACK);
					setColor(parentOf(parentOf(x)), RED);
					x = parentOf(parentOf(x));
				} else {
					if (x == rightOf(parentOf(x))) {
						x = parentOf(x);
						rotateLeft(x);
					}
					setColor(parentOf(x), BLACK);
					setColor(parentOf(parentOf(x)), RED);
					rotateRight(parentOf(parentOf(x)));
				}
			} else {
				Entry<T> y = leftOf(parentOf(parentOf(x)));
				if (colorOf(y) == RED) {
					setColor(parentOf(x), BLACK);
					setColor(y, BLACK);
					setColor(parentOf(parentOf(x)), RED);
					x = parentOf(parentOf(x));
				} else {
					if (x == leftOf(parentOf(x))) {
						x = parentOf(x);
						rotateRight(x);
					}
					setColor(parentOf(x), BLACK);
					setColor(parentOf(parentOf(x)), RED);
					rotateLeft(parentOf(parentOf(x)));
				}
			}
		root.color = BLACK;
	}

	/**
	 * Delete node p, and then rebalance the tree.
	 */
	@Override
	public void deleteEntry(Entry<T> p) {
		size--;

		// If strictly internal, copy successor's element to p and then make p
		// point to successor.
		if (p.left != null && p.right != null) {
			Entry<T> s = successor(p);
			p.key = s.key;
			p.value = s.value;
			p = s;
		} // p has 2 children

		// Start fixup at replacement node, if it exists.
		Entry<T> replacement = (p.left != null ? p.left : p.right);

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
			if (p.color == BLACK) fixAfterDeletion(replacement);
		} else if (p.parent == null)
			root = null;
		else { //  No children. Use self as phantom replacement and unlink.
			if (p.color == BLACK) fixAfterDeletion(p);

			if (p.parent != null) {
				if (p == p.parent.left)
					p.parent.left = null;
				else if (p == p.parent.right) p.parent.right = null;
				p.parent = null;
			}
		}
	}

	/** From CLR */
	private void fixAfterDeletion(Entry<T> x) {
		while (x != root && colorOf(x) == BLACK)
			if (x == leftOf(parentOf(x))) {
				Entry<T> sib = rightOf(parentOf(x));

				if (colorOf(sib) == RED) {
					setColor(sib, BLACK);
					setColor(parentOf(x), RED);
					rotateLeft(parentOf(x));
					sib = rightOf(parentOf(x));
				}

				if (colorOf(leftOf(sib)) == BLACK && colorOf(rightOf(sib)) == BLACK) {
					setColor(sib, RED);
					x = parentOf(x);
				} else {
					if (colorOf(rightOf(sib)) == BLACK) {
						setColor(leftOf(sib), BLACK);
						setColor(sib, RED);
						rotateRight(sib);
						sib = rightOf(parentOf(x));
					}
					setColor(sib, colorOf(parentOf(x)));
					setColor(parentOf(x), BLACK);
					setColor(rightOf(sib), BLACK);
					rotateLeft(parentOf(x));
					x = root;
				}
			} else { // symmetric
				Entry<T> sib = leftOf(parentOf(x));

				if (colorOf(sib) == RED) {
					setColor(sib, BLACK);
					setColor(parentOf(x), RED);
					rotateRight(parentOf(x));
					sib = leftOf(parentOf(x));
				}

				if (colorOf(rightOf(sib)) == BLACK && colorOf(leftOf(sib)) == BLACK) {
					setColor(sib, RED);
					x = parentOf(x);
				} else {
					if (colorOf(leftOf(sib)) == BLACK) {
						setColor(rightOf(sib), BLACK);
						setColor(sib, RED);
						rotateLeft(sib);
						sib = leftOf(parentOf(x));
					}
					setColor(sib, colorOf(parentOf(x)));
					setColor(parentOf(x), BLACK);
					setColor(leftOf(sib), BLACK);
					rotateRight(parentOf(x));
					x = root;
				}
			}

		setColor(x, BLACK);
	}

}
