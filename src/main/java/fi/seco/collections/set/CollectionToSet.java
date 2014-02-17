package fi.seco.collections.set;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class CollectionToSet<E> implements Set<E> {

	Collection<E> col;

	public CollectionToSet(Collection<E> col) {
		this.col = col;
	}

	@Override
	public int size() {
		return col.size();
	}

	@Override
	public boolean isEmpty() {
		return col.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return col.contains(o);
	}

	@Override
	public Iterator<E> iterator() {
		return col.iterator();
	}

	@Override
	public Object[] toArray() {
		return col.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return col.toArray(a);
	}

	@Override
	public boolean add(E o) {
		return col.add(o);
	}

	@Override
	public boolean remove(Object o) {
		return col.remove(o);
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean containsAll(Collection c) {
		return col.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		return col.addAll(c);
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean removeAll(Collection c) {
		return col.removeAll(c);
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean retainAll(Collection c) {
		return col.retainAll(c);
	}

	@Override
	public void clear() {
		col.clear();
	}

	@Override
	public String toString() {
		return col.toString();
	}
}
