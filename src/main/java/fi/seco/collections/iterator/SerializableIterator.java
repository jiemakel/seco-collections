package fi.seco.collections.iterator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import fi.seco.collections.collection.IteratorList;

public class SerializableIterator<E> implements ISerializableIterableIterator<E> {

	private static final long serialVersionUID = 1L;
	private transient Iterator<E> iter;

	public SerializableIterator(Iterator<E> iter) {
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

	private Object serializedForm;

	protected Object writeReplace() {
		if (iter instanceof Serializable)
			serializedForm = iter;
		else serializedForm = new IteratorList<E>(iter, new ArrayList<E>());
		return this;
	}

	@SuppressWarnings("unchecked")
	protected Object readResolve() {
		if (serializedForm instanceof Iterator)
			iter = (Iterator<E>) serializedForm;
		else iter = ((Collection<E>) serializedForm).iterator();
		return this;
	}

}
