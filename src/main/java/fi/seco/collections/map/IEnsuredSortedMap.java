package fi.seco.collections.map;

import java.util.SortedMap;

public interface IEnsuredSortedMap<K, V> extends SortedMap<K, V> {
	public V ensure(K key);
}
