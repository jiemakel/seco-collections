package fi.seco.collections.map.primitive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnsuredFloatObjectHashMap<V> extends AEnsuredFloatObjectHashMap<V> {

	private final Class<?> c;

	private static final Logger log = LoggerFactory.getLogger(EnsuredFloatObjectHashMap.class);

	public EnsuredFloatObjectHashMap(Class<?> c) {
		this.c = c;
	}

	public EnsuredFloatObjectHashMap(Class<?> c, int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
		this.c = c;
	}

	public EnsuredFloatObjectHashMap(Class<?> c, int initialCapacity) {
		super(initialCapacity);
		this.c = c;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected V createNew() {
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
