package fi.seco.collections.map.primitive;

public abstract class AEnsuredLongObjectTreeMap<V> extends LongObjectTreeMap<V> {

	public AEnsuredLongObjectTreeMap() {
		super();
	}

	public V ensure(long key) {
		V ret = get(key);
		if (ret == null) {
			ret = createNew();
			put(key, ret);
		}
		return ret;
	}

	protected abstract V createNew();
}
