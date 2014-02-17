package fi.seco.collections.iterator.primitive;

import fi.seco.collections.iterator.AIterableIterator;

public abstract class AIntToObjectMappingIterator<E> extends AIterableIterator<E> {

	private final int[] table;
	private int pointer = -1;

	public AIntToObjectMappingIterator(int[] table) {
		this.table = table;
	}

	@Override
	public boolean hasNext() {
		return pointer < table.length - 1;
	}

	@Override
	public E next() {
		return map(table[++pointer]);
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	public abstract E map(int l);
}
