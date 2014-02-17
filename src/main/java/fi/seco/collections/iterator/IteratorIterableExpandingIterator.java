package fi.seco.collections.iterator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class IteratorIterableExpandingIterator<E> extends ALazyGeneratingIterator<E> {

	private Iterator<? extends E> iter;
	private Iterator<Iterator<? extends E>> iterStack;
	private final Collection<Iterator<? extends E>> iterCol;

	public IteratorIterableExpandingIterator(Iterable<Iterator<? extends E>> iterStack) {
		this.iterStack = iterStack.iterator();
		iter = this.iterStack.next();
		iterCol = null;
	}

	public IteratorIterableExpandingIterator(IIterableIterator<Iterator<? extends E>> iterStack) {
		this.iterStack = iterStack;
		iter = this.iterStack.next();
		iterCol = null;
	}

	public IteratorIterableExpandingIterator(Iterator<Iterator<? extends E>> iterStack) {
		this.iterStack = iterStack;
		iter = this.iterStack.next();
		iterCol = null;
	}

	public IteratorIterableExpandingIterator() {
		iterCol = new ArrayList<Iterator<? extends E>>();
	}

	public IteratorIterableExpandingIterator(Collection<? extends E>... cols) {
		iterCol = new ArrayList<Iterator<? extends E>>(cols.length);
		for (Collection<? extends E> t : cols)
			iterCol.add(t.iterator());
	}

	public IteratorIterableExpandingIterator(Iterator<? extends E>... cols) {
		iterCol = new ArrayList<Iterator<? extends E>>(cols.length);
		for (Iterator<? extends E> t : cols)
			iterCol.add(t);
	}

	public IteratorIterableExpandingIterator<E> add(Iterator<? extends E> iter) {
		iterCol.add(iter);
		return this;
	}

	public IteratorIterableExpandingIterator<E> add(Iterable<? extends E> iter) {
		iterCol.add(iter.iterator());
		return this;
	}

	public IteratorIterableExpandingIterator<E> add(IIterableIterator<? extends E> iter) {
		iterCol.add(iter);
		return this;
	}

	@Override
	public final void remove() {
		iter.remove();
	}

	@Override
	protected final E generateNext() {
		if (iterStack == null) {
			iterStack = iterCol.iterator();
			iter = iterStack.next();
		}
		if (iter == null) return null;
		if (iter.hasNext()) return iter.next();
		while (!iter.hasNext() && iterStack.hasNext())
			iter = iterStack.next();
		if (!iter.hasNext()) return null;
		return iter.next();
	}

}
