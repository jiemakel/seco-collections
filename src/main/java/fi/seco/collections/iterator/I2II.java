package fi.seco.collections.iterator;

import java.util.Iterator;

public class I2II<E> implements IIterableIterator<E> {

	private final Iterator<E> iter;

	public I2II(Iterator<E> iter) {
		this.iter = iter;
	}

	@Override
	public final Iterator<E> iterator() {
		return iter;
	}

	@Override
	public final boolean hasNext() {
		return iter.hasNext();
	}

	@Override
	public final E next() {
		return iter.next();
	}

	@Override
	public final void remove() {
		iter.remove();
	}

}
