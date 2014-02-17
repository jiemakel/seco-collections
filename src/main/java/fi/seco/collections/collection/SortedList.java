package fi.seco.collections.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class SortedList<E extends Comparable<? super E>> implements List<E> {

	private List<E> list;
	boolean sorted;

	public SortedList() {
		this(new ArrayList<E>(), true);
	}

	public SortedList(List<E> list) {
		this(list, false);
	}

	public SortedList(List<E> list, boolean sorted) {
		this.list = list;
		this.sorted = sorted;
	}

	public boolean isSorted() {
		return sorted;
	}

	public void sort() {
		Collections.sort(list);
		sorted = true;
	}

	public void ensureOrder() {
		if (!isSorted()) sort();
	}

	@Override
	public boolean add(E e) {
		sorted = false;
		return list.add(e);
	}

	@Override
	public void add(int index, E element) {
		sorted = false;
		list.add(index, element);
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		sorted = false;
		return list.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		sorted = false;
		return list.addAll(index, c);
	}

	@Override
	public void clear() {
		sorted = false;
		list.clear();
	}

	@Override
	public boolean contains(Object o) {
		return list.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return list.containsAll(c);
	}

	@Override
	public boolean equals(Object o) {
		return list.equals(o);
	}

	@Override
	public E get(int index) {
		return list.get(index);
	}

	@Override
	public int hashCode() {
		return list.hashCode();
	}

	@Override
	public int indexOf(Object o) {
		return list.indexOf(o);
	}

	@Override
	public boolean isEmpty() {
		return list.isEmpty();
	}

	@Override
	public Iterator<E> iterator() {
		return list.iterator();
	}

	@Override
	public int lastIndexOf(Object o) {
		return list.lastIndexOf(o);
	}

	@Override
	public ListIterator<E> listIterator() {
		return list.listIterator();
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		return list.listIterator(index);
	}

	@Override
	public E remove(int index) {
		sorted = false;
		return list.remove(index);
	}

	@Override
	public boolean remove(Object o) {
		sorted = false;
		return list.remove(o);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		sorted = false;
		return list.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		sorted = false;
		return list.retainAll(c);
	}

	@Override
	public E set(int index, E element) {
		sorted = false;
		return list.set(index, element);
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		return list.subList(fromIndex, toIndex);
	}

	@Override
	public Object[] toArray() {
		return list.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return list.toArray(a);
	}

}
