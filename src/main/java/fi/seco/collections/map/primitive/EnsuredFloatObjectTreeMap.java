package fi.seco.collections.map.primitive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnsuredFloatObjectTreeMap<V> extends FloatObjectTreeMap<V> {
	private final Class<?> c;

	private static final Logger log = LoggerFactory.getLogger(EnsuredFloatObjectTreeMap.class);

	public EnsuredFloatObjectTreeMap(Class<?> c) {
		this.c = c;
	}

	@SuppressWarnings("unchecked")
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

	public V ensure(int key) {
		V ret = get(key);
		if (ret == null) {
			ret = createNew();
			put(key, ret);
		}
		return ret;
	}
}
