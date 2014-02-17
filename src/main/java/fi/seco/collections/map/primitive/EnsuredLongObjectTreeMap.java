package fi.seco.collections.map.primitive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnsuredLongObjectTreeMap<V> extends AEnsuredLongObjectTreeMap<V> {
	private static final Logger log = LoggerFactory.getLogger(EnsuredLongObjectTreeMap.class);

	private final Class<?> c;

	public EnsuredLongObjectTreeMap(Class<?> c) {
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
