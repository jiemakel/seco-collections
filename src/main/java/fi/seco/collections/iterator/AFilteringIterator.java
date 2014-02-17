package fi.seco.collections.iterator;

import java.util.Iterator;

public abstract class AFilteringIterator<E> extends ALazyGeneratingIterator<E> {

	Iterator<? extends E> iter;

	public AFilteringIterator(IIterableIterator<? extends E> iter) {
		this.iter = iter;
	}

	public AFilteringIterator(Iterator<? extends E> iter) {
		this.iter = iter;
	}

	public AFilteringIterator(Iterable<? extends E> col) {
		this(col.iterator());
	}

	@Override
	public final void remove() {
		iter.remove();
	}

	@Override
	protected final E generateNext() {
		while (iter.hasNext()) {
			E tmp = iter.next();
			if (accept(tmp)) return tmp;
		}
		return null;
	}

	protected abstract boolean accept(E object);

}
