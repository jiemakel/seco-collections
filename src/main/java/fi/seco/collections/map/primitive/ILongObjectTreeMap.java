package fi.seco.collections.map.primitive;

import com.carrotsearch.hppc.cursors.LongObjectCursor;

import fi.seco.collections.iterator.IIterableIterator;

public interface ILongObjectTreeMap<T> {

	public long compare(long key1, long key2);

	public long firstKey();

	public long lastKey();

	public IIterableIterator<LongObjectCursor<T>> iterator();

	public IIterableIterator<LongObjectCursor<T>> descendingIterator();

	public Entry<T> getCeilingEntry(long key);

	public Entry<T> getFloorEntry(long key);

	public Entry<T> getHigherEntry(long key);

	public Entry<T> getLowerEntry(long key);

	public T put(long key, T value);

	public Entry<T> getEntry(long key);

	public T get(long key);

	public T remove(long key);

	public void clear();

	public Entry<T> removeFirstEntry();

	public Entry<T> removeLastEntry();

	public long getLowerKey(long key);

	public long getFloorKey(long key);

	public long getCeilingKey(long key);

	public long getHigherKey(long key);

	public Entry<T> getFirstEntry();

	public Entry<T> getLastEntry();

	public boolean isEmpty();

	public long size();

	public boolean containsKey(long key);

	static final boolean RED = false;
	static final boolean BLACK = true;

	static final class Entry<T> {
		long key;
		T value;
		Entry<T> left = null;
		Entry<T> right = null;
		Entry<T> parent;
		boolean color = BLACK;

		Entry(long key, T value, Entry<T> parent) {
			this.key = key;
			this.value = value;
			this.parent = parent;
		}

		public long getKey() {
			return key;
		}

		public T getValue() {
			return value;
		}

		public T setValue(T value) {
			T oldValue = this.value;
			this.value = value;
			return oldValue;
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean equals(Object o) {
			if (!(o instanceof Entry)) return false;
			Entry<T> e = (Entry<T>) o;
			return key == e.getKey() && value.equals(e.getValue());
		}

		@Override
		public int hashCode() {
			return value != null ? (int) (key ^ (key >>> 32)) * 31 + value.hashCode() : (int) (key ^ (key >>> 32));
		}

		@Override
		public String toString() {
			return key + "=" + value;
		}
	}

	public void deleteEntry(Entry<T> e);

	public ILongObjectTreeMap<T> subMap(long fromKey, boolean fromInclusive, long toKey, boolean toInclusive);

	public ILongObjectTreeMap<T> headMap(long toKey, boolean inclusive);

	public ILongObjectTreeMap<T> tailMap(long fromKey, boolean inclusive);

}