package fi.seco.collections.map;

import java.util.Comparator;
import java.util.HashMap;
import java.util.SortedMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnsuredSortedMap<O, E> extends AEnsuredMap<O, E> implements IEnsuredSortedMap<O, E> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Class<?> c;
	private static final Logger log = LoggerFactory.getLogger(EnsuredSortedMap.class);

	public EnsuredSortedMap(SortedMap<O, E> map, Class<?> c) {
		super(map);
		this.c = c;
	}

	public EnsuredSortedMap(Class<?> c) {
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

	@Override
	public Comparator<? super O> comparator() {
		return ((SortedMap<O, E>) map).comparator();
	}

	@Override
	public O firstKey() {
		return ((SortedMap<O, E>) map).firstKey();
	}

	@Override
	public SortedMap<O, E> headMap(O toKey) {
		return ((SortedMap<O, E>) map).headMap(toKey);
	}

	@Override
	public O lastKey() {
		return ((SortedMap<O, E>) map).lastKey();
	}

	@Override
	public SortedMap<O, E> subMap(O fromKey, O toKey) {
		return ((SortedMap<O, E>) map).subMap(fromKey, toKey);
	}

	@Override
	public SortedMap<O, E> tailMap(O fromKey) {
		return ((SortedMap<O, E>) map).tailMap(fromKey);
	}
}
