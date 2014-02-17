package fi.seco.collections.iterator;

import java.util.Iterator;

public class NullFilteringIterator<E> extends AFilteringIterator<E> {

	public NullFilteringIterator(IIterableIterator<? extends E> iter) {
		super(iter);
	}

	public NullFilteringIterator(Iterator<? extends E> iter) {
		super(iter);
	}

	public NullFilteringIterator(Iterable<? extends E> col) {
		super(col);
	}

	@Override
	protected boolean accept(E object) {
		return object != null;
	}

}
