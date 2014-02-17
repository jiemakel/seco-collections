package fi.seco.collections.iterator;

public class SingletonIterator<E> extends AIterableIterator<E> {

	private E singleton;

	public SingletonIterator(E singleton) {
		this.singleton = singleton;
	}

	@Override
	public final boolean hasNext() {
		if (singleton != null) return true;
		return false;
	}

	@Override
	public final E next() {
		E tmp = singleton;
		singleton = null;
		return tmp;
	}

	@Override
	public final void remove() {}

}
