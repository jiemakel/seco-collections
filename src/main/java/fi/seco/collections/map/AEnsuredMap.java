package fi.seco.collections.map;

import java.util.HashMap;
import java.util.Map;

public abstract class AEnsuredMap<O, E> extends AEncapsulatingMap<O, E> implements IEnsuredMap<O, E> {

	private static final long serialVersionUID = 1L;

	public AEnsuredMap(Map<O, E> map) {
		super(map);
	}

	public AEnsuredMap() {
		super(new HashMap<O, E>());
	}

	@Override
	public E ensure(O key) {
		E ret = map.get(key);
		if (ret == null) {
			ret = createNew();
			map.put(key, ret);
		}
		return ret;
	}

	protected abstract E createNew();
}
