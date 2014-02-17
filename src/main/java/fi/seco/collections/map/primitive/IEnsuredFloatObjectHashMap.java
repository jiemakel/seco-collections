package fi.seco.collections.map.primitive;

import com.carrotsearch.hppc.FloatObjectMap;

public interface IEnsuredFloatObjectHashMap<V> extends FloatObjectMap<V> {
	public V ensure(float id);
}