package fi.seco.collections.map.primitive;

import com.carrotsearch.hppc.FloatObjectOpenHashMap;

public abstract class AEnsuredFloatObjectHashMap<V> extends FloatObjectOpenHashMap<V> implements IEnsuredFloatObjectHashMap<V> {

	public AEnsuredFloatObjectHashMap() {
		super();
	}

	public AEnsuredFloatObjectHashMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	public AEnsuredFloatObjectHashMap(int initialCapacity) {
		super(initialCapacity);
	}

	@Override
	public V ensure(float key) {
		V ret = get(key);
		if (ret == null) {
			ret = createNew();
			put(key, ret);
		}
		return ret;
	}

	protected abstract V createNew();
}
