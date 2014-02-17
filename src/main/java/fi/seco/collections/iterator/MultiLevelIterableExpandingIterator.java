package fi.seco.collections.iterator;

import java.util.Iterator;
import java.util.LinkedList;

public class MultiLevelIterableExpandingIterator<E> extends ALazyGeneratingIterator<E> {

	Iterator<?> iter;
	LinkedList<Iterator<?>> iterStack = new LinkedList<Iterator<?>>();

	public MultiLevelIterableExpandingIterator(IIterableIterator<?> iter) {
		this.iter = iter;
	}

	public MultiLevelIterableExpandingIterator(Iterator<?> iter) {
		this.iter = iter;
	}

	public MultiLevelIterableExpandingIterator(Iterable<?> col) {
		this.iter = col.iterator();
	}

	@Override
	public final void remove() {
		iter.remove();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected final E generateNext() {
		while (iter.hasNext() || !iterStack.isEmpty()) {
			while (!iter.hasNext() && !iterStack.isEmpty())
				iter = iterStack.removeFirst();
			if (!iter.hasNext()) return null;
			Object tmp = iter.next();
			if (tmp instanceof Iterable) {
				iterStack.add(iter);
				iter = ((Iterable<E>) tmp).iterator();
			} else if (tmp instanceof Iterator) {
				iterStack.add(iter);
				iter = (Iterator<E>) tmp;
			} else return (E) tmp;
		}
		return null;
	}

}
