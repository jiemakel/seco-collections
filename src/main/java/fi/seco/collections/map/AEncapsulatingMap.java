package fi.seco.collections.map;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public abstract class AEncapsulatingMap<O, E> implements Map<O, E>, Serializable {

	private static final long serialVersionUID = 1L;
	protected Map<O, E> map;

	AEncapsulatingMap() {}

	AEncapsulatingMap(Map<O, E> map) {
		this.map = map;
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	@Override
	public E get(Object key) {
		return map.get(key);
	}

	@Override
	public E put(O key, E value) {
		return map.put(key, value);
	}

	@Override
	public E remove(Object key) {
		return map.remove(key);
	}

	@Override
	public void putAll(Map<? extends O, ? extends E> t) {
		map.putAll(t);
	}

	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public Set<O> keySet() {
		return map.keySet();
	}

	@Override
	public Collection<E> values() {
		return map.values();
	}

	@Override
	public Set<Entry<O, E>> entrySet() {
		return map.entrySet();
	}

	@Override
	public String toString() {
		return map.toString();
	}
}
