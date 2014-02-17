package fi.seco.collections.iterator;

import java.util.Iterator;

public abstract class AIterableIterator<E> implements IIterableIterator<E> {

	@Override
	public final Iterator<E> iterator() {
		return this;
	}

	@Override
	public abstract boolean hasNext();

	@Override
	public abstract E next();

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
