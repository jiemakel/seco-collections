package fi.seco.collections.map.primitive;

import com.carrotsearch.hppc.LongObjectOpenHashMap;

public abstract class AEnsuredLongObjectHashMap<V> extends LongObjectOpenHashMap<V> implements IEnsuredLongObjectMap<V> {

	public AEnsuredLongObjectHashMap() {
		super();
	}

	public AEnsuredLongObjectHashMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	public AEnsuredLongObjectHashMap(int initialCapacity) {
		super(initialCapacity);
	}

	@Override
	public V ensure(long key) {
		V ret = get(key);
		if (ret == null) {
			ret = createNew(key);
			put(key, ret);
		}
		return ret;
	}

	protected abstract V createNew(long key);
}
