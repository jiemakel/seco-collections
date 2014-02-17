package fi.seco.collections.collection.primitive;

import java.util.Iterator;

import com.carrotsearch.hppc.cursors.FloatCursor;

public interface IFloatTreeSet {

	public int compare(float key1, float key2);

	public Entry getFirstEntry();

	public Entry getLastEntry();

	public float getFirst();

	public float getLast();

	public Iterator<FloatCursor> iterator();

	public Iterator<FloatCursor> descendingIterator();

	public boolean add(float key);

	public boolean contains(float key);

	public boolean remove(float key);

	public void clear();

	public float removeFirst();

	public float removeLast();

	public float getLower(float key);

	public float getFloor(float key);

	public float getCeiling(float key);

	public float getHigher(float key);

	public boolean isEmpty();

	public int size();

	static final class Entry {
		static final boolean RED = false;
		static final boolean BLACK = true;

		float key;
		Entry left = null;
		Entry right = null;
		Entry parent;
		boolean color = BLACK;

		Entry(float key, Entry parent) {
			this.key = key;
			this.parent = parent;
		}

		public float getKey() {
			return key;
		}

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof Entry)) return false;
			Entry e = (Entry) o;
			return key == e.getKey();
		}

		@Override
		public int hashCode() {
			return Float.floatToIntBits(key);
		}

		@Override
		public String toString() {
			return "" + key;
		}
	}

	public IFloatTreeSet subSet(float fromKey, boolean fromInclusive, float toKey, boolean toInclusive);

	public IFloatTreeSet headSet(float toKey, boolean inclusive);

	public IFloatTreeSet tailSet(float fromKey, boolean inclusive);

	public void removeEntry(Entry e);
}