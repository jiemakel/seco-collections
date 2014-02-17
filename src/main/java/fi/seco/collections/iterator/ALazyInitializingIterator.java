package fi.seco.collections.iterator;

import java.util.Iterator;

public abstract class ALazyInitializingIterator<E> extends AIterableIterator<E> {

	private Iterator<? extends E> iter;

	@Override
	public final boolean hasNext() {
		if (iter == null) iter = initialize();
		return iter.hasNext();
	}

	@Override
	public final E next() {
		if (iter == null) iter = initialize();
		return iter.next();
	}

	@Override
	public final void remove() {
		iter.remove();
	}

	protected abstract Iterator<? extends E> initialize();

}
