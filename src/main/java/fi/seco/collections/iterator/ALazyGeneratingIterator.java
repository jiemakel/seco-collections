package fi.seco.collections.iterator;

import java.util.NoSuchElementException;

public abstract class ALazyGeneratingIterator<E> extends AIterableIterator<E> {

	private E next;
	private boolean closed = false;

	@Override
	public boolean hasNext() {
		if (next != null) return true;
		if (closed) return false;
		next = generateNext();
		if (next == null) {
			closed = true;
			return false;
		}
		return true;
	}

	protected abstract E generateNext();

	@Override
	public final E next() {
		if (hasNext()) {
			E tmp = next;
			next = null;
			return tmp;
		}
		throw new NoSuchElementException();
	}

}
