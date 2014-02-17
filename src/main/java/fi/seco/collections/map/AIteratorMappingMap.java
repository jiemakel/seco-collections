package fi.seco.collections.map;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import fi.seco.collections.iterator.IIterableIterator;

public abstract class AIteratorMappingMap<O1, T1, T2> extends AEncapsulatingMap<T1, T2> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AIteratorMappingMap(IIterableIterator<? extends O1> iter) {
		this(iter, new HashMap<T1, T2>());
	}

	public AIteratorMappingMap(Iterator<? extends O1> iter) {
		this(iter, new HashMap<T1, T2>());
	}

	public AIteratorMappingMap(Iterable<? extends O1> iterable) {
		this(iterable.iterator(), new HashMap<T1, T2>());
	}

	public AIteratorMappingMap(Iterator<? extends O1> iter, Map<T1, T2> map) {
		super(map);
		O1 okey = iter.next();
		T1 key = map(okey);
		for (; iter.hasNext();)
			map.put(key, getValue(okey, key));
	}

	protected abstract T1 map(O1 okey);

	protected abstract T2 getValue(O1 okey, T1 key);

}
