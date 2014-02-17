package fi.seco.collections.map;

import java.util.Map;

public abstract class AMappingMap<O1, O2, E1, E2> extends AEncapsulatingMap<E1, E2> {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	public AMappingMap(Map<? extends O1, ? extends O2> map) {
		try {
			this.map = map.getClass().newInstance();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		}
		for (Map.Entry<? extends O1, ? extends O2> e : map.entrySet())
			this.map.put(mapKey(e.getKey()), mapValue(e.getValue()));
	}

	public abstract E1 mapKey(O1 key);

	public abstract E2 mapValue(O2 value);

}
