package fi.seco.collections.iterator;

import java.util.Collection;

public interface IBackedIterator<E> {
	public Collection<E> getBackingCollection();
}
