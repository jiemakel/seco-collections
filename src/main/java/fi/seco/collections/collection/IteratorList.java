package fi.seco.collections.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.seco.collections.iterator.ALazyInitializingIterator;
import fi.seco.collections.iterator.BackedIterator;
import fi.seco.collections.iterator.IIterableIterator;

public class IteratorList<E> implements List<E> {

	private List<E> col;
	private Iterator<? extends E> iter;
	private E notAdded;

	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(IteratorList.class);

	public IteratorList(IIterableIterator<? extends E> iter) {
		this(iter, new ArrayList<E>());
	}

	public IteratorList(Iterator<? extends E> iter) {
		this(iter, new ArrayList<E>());
	}

	public IteratorList(Iterable<? extends E> iterable) {
		this(iterable.iterator(), new ArrayList<E>());
	}

	@SuppressWarnings("unchecked")
	public IteratorList(Iterator<? extends E> iter, List<E> col) {
		if (iter instanceof BackedIterator && ((BackedIterator) iter).getBackingCollection() instanceof List)
			this.col = (List<E>) ((BackedIterator) iter).getBackingCollection();
		else {
			this.iter = iter;
			this.col = col;
		}
	}

	public void collapse() {
		if (notAdded != null) col.add(notAdded);
		for (; iter.hasNext();)
			col.add(iter.next());
		iter = null;
	}

	@Override
	public boolean add(E o) {
		if (!(iter == null)) collapse();
		return col.add(o);
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		if (!(iter == null)) collapse();
		return col.addAll(c);
	}

	@Override
	public void clear() {
		iter = null;
		col.clear();
	}

	@Override
	public boolean contains(Object o) {
		if (!(iter == null)) collapse();
		return col.contains(o);
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean containsAll(Collection c) {
		if (!(iter == null)) collapse();
		return col.containsAll(c);
	}

	@Override
	public boolean equals(Object o) {
		if (!(iter == null)) collapse();
		return col.equals(o);
	}

	@Override
	public int hashCode() {
		if (!(iter == null)) collapse();
		return col.hashCode();
	}

	@Override
	public boolean isEmpty() {
		if (!(iter == null)) return !iter.hasNext() || col.isEmpty();
		return col.isEmpty();
	}

	@Override
	public Iterator<E> iterator() {
		if (!(iter == null) && col.isEmpty())
			return new ALazyInitializingIterator<E>() {
				@Override
				public Iterator<? extends E> initialize() {
					if (iter == null) return col.iterator();
					return new Iterator<E>() {

						@Override
						public boolean hasNext() {
							boolean hasNext = IteratorList.this.iter.hasNext();
							if (!hasNext) {
								if (notAdded != null) col.add(notAdded);
								IteratorList.this.iter = null;
							}
							return hasNext;
						}

						@Override
						public E next() {
							if (notAdded != null) col.add(notAdded);
							notAdded = IteratorList.this.iter.next();
							return notAdded;
						}

						@Override
						public void remove() {
							notAdded = null;
						}

					};
				}

			};
		else if (!(iter == null)) collapse();
		return col.iterator();
	}

	@Override
	public boolean remove(Object o) {
		if (!(iter == null)) collapse();
		return col.remove(o);
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean removeAll(Collection c) {
		if (!(iter == null)) collapse();
		return col.removeAll(c);
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean retainAll(Collection c) {
		if (!(iter == null)) collapse();
		return col.retainAll(c);
	}

	@Override
	public int size() {
		if (!(iter == null)) collapse();
		return col.size();
	}

	@Override
	public Object[] toArray() {
		if (!(iter == null)) collapse();
		return col.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		if (!(iter == null)) collapse();
		return col.toArray(a);
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		if (!(iter == null)) collapse();
		return col.addAll(index, c);
	}

	@Override
	public E get(int index) {
		if (!(iter == null) && col.size() < index + 1) collapse();
		return col.get(index);
	}

	@Override
	public E set(int index, E element) {
		if (!(iter == null)) collapse();
		return col.set(index, element);
	}

	@Override
	public void add(int index, E element) {
		if (!(iter == null) && col.size() < index + 1) collapse();
		col.add(index, element);
	}

	@Override
	public E remove(int index) {
		if (!(iter == null) && col.size() < index + 1) collapse();
		return col.remove(index);
	}

	@Override
	public int indexOf(Object o) {
		if (!(iter == null)) collapse();
		return col.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		if (!(iter == null)) collapse();
		return col.lastIndexOf(o);
	}

	@Override
	public ListIterator<E> listIterator() {
		if (!(iter == null)) collapse();
		return col.listIterator();
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		if (!(iter == null)) collapse();
		return col.listIterator(index);
	}

	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		if (!(iter == null) && col.size() < toIndex + 1) collapse();
		return col.subList(fromIndex, toIndex);
	}

}
