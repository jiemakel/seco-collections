package fi.seco.collections.iterator;

import java.util.Iterator;
import java.util.LinkedList;

public class CompoundIterator<E> extends ALazyGeneratingIterator<E> {

	Iterator<? extends E> iter;
	LinkedList<Iterator<? extends E>> iterStack = new LinkedList<Iterator<? extends E>>();

	public CompoundIterator(Iterator<E> iter) {
		this.iter = iter;
	}

	public CompoundIterator(Iterable<E> col) {
		this.iter = col.iterator();
	}

	public CompoundIterator() {

	}

	public CompoundIterator<E> addIterator(Iterator<? extends E> iter) {
		if (this.iter == null)
			this.iter = iter;
		else iterStack.add(iter);
		return this;
	}

	public CompoundIterator<E> addIterable(Iterable<? extends E> i) {
		return addIterator(i.iterator());
	}

	@Override
	public final void remove() {
		iter.remove();
	}

	@Override
	protected final E generateNext() {
		if (iter == null) return null;
		if (iter.hasNext()) return iter.next();
		while (!iter.hasNext() && !iterStack.isEmpty())
			iter = iterStack.removeFirst();
		if (!iter.hasNext()) return null;
		return iter.next();
	}

}
