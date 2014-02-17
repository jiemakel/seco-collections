package fi.seco.collections.map;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import fi.seco.collections.iterator.IIterableIterator;

public class IteratorMap<T1, T2> extends AEncapsulatingMap<T1, T2> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public IteratorMap(IIterableIterator<? extends T1> iter) {
		this(iter, new HashMap<T1, T2>());
	}

	public IteratorMap(Iterator<? extends T1> iter) {
		this(iter, new HashMap<T1, T2>());
	}

	public IteratorMap(Iterable<? extends T1> iterable) {
		this(iterable.iterator(), new HashMap<T1, T2>());
	}

	public IteratorMap(Iterator<? extends T1> iter, Map<T1, T2> map) {
		super(map);
		T1 key = iter.next();
		for (; iter.hasNext();)
			map.put(key, null);
	}

}
