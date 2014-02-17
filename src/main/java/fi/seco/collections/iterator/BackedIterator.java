package fi.seco.collections.iterator;

import java.util.Collection;
import java.util.Iterator;

public class BackedIterator<E> extends ALazyInitializingIterator<E> implements IBackedIterator<E> {

	private final Collection<E> collection;

	public BackedIterator(Collection<E> collection) {
		this.collection = collection;
	}

	@Override
	public Iterator<? extends E> initialize() {
		return collection.iterator();
	}

	public boolean isCollectionBacked() {
		return true;
	}

	@Override
	public Collection<E> getBackingCollection() {
		return collection;
	}

}
