package fi.seco.collections.map.primitive;

import com.carrotsearch.hppc.cursors.FloatObjectCursor;

import fi.seco.collections.iterator.IIterableIterator;

public interface IFloatObjectTreeMap<T> {

	public int compare(float key1, float key2);

	public float getFirstKey();

	public float getLastKey();

	public IIterableIterator<FloatObjectCursor<T>> iterator();

	public IIterableIterator<FloatObjectCursor<T>> descendingIterator();

	public Entry<T> getCeilingEntry(float key);

	public Entry<T> getFloorEntry(float key);

	public Entry<T> getHigherEntry(float key);

	public Entry<T> getLowerEntry(float key);

	public T put(float key, T value);

	public Entry<T> getEntry(float key);

	public T get(float key);

	public T remove(float key);

	public void clear();

	public Entry<T> removeFirstEntry();

	public Entry<T> removeLastEntry();

	public float getLowerKey(float key);

	public float getFloorKey(float key);

	public float getCeilingKey(float key);

	public float getHigherKey(float key);

	public Entry<T> getFirstEntry();

	public Entry<T> getLastEntry();

	public boolean isEmpty();

	public int size();

	public boolean containsKey(float key);

	static final boolean RED = false;
	static final boolean BLACK = true;

	static final class Entry<T> {
		float key;
		T value;
		Entry<T> left = null;
		Entry<T> right = null;
		Entry<T> parent;
		boolean color = BLACK;

		Entry(float key, T value, Entry<T> parent) {
			this.key = key;
			this.value = value;
			this.parent = parent;
		}

		public float getKey() {
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
			return value != null ? Float.floatToIntBits(key) ^ value.hashCode() : Float.floatToIntBits(key);
		}

		@Override
		public String toString() {
			return key + "=" + value;
		}
	}

	public void deleteEntry(Entry<T> e);

	public IFloatObjectTreeMap<T> subMap(float fromKey, boolean fromInclusive, float toKey, boolean toInclusive);

	public IFloatObjectTreeMap<T> headMap(float toKey, boolean inclusive);

	public IFloatObjectTreeMap<T> tailMap(float fromKey, boolean inclusive);

}