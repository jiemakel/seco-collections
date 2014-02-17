package fi.seco.collections.map.primitive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnsuredLongObjectHashMap<V> extends AEnsuredLongObjectHashMap<V> {

	private final Class<?> c;

	private static final Logger log = LoggerFactory.getLogger(EnsuredLongObjectHashMap.class);

	public EnsuredLongObjectHashMap(Class<?> c) {
		this.c = c;
	}

	public EnsuredLongObjectHashMap(Class<?> c, int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
		this.c = c;
	}

	public EnsuredLongObjectHashMap(Class<?> c, int initialCapacity) {
		super(initialCapacity);
		this.c = c;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected V createNew(long key) {
		try {
			return (V) c.newInstance();
		} catch (InstantiationException e) {
			log.error("", e);
		} catch (IllegalAccessException e) {
			log.error("", e);
		}
		return null;
	}
}
