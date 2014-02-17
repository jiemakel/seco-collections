package fi.seco.collections.map;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnsuredMap<O, E> extends AEnsuredMap<O, E> implements Serializable {

	private static final long serialVersionUID = 1L;
	private final Class<?> c;
	private static final Logger log = LoggerFactory.getLogger(EnsuredMap.class);

	public EnsuredMap(Map<O, E> map, Class<?> c) {
		super(map);
		this.c = c;
	}

	public EnsuredMap(Class<?> c) {
		super(new HashMap<O, E>());
		this.c = c;
	}

	@SuppressWarnings("unchecked")
	@Override
	public E createNew() {
		try {
			return (E) c.newInstance();
		} catch (InstantiationException e) {
			log.error("", e);
		} catch (IllegalAccessException e) {
			log.error("", e);
		}
		return null;
	}
}
