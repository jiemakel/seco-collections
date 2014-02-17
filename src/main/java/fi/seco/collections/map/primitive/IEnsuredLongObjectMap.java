package fi.seco.collections.map.primitive;

import com.carrotsearch.hppc.LongObjectMap;

public interface IEnsuredLongObjectMap<V> extends LongObjectMap<V> {
	public V ensure(long id);
}