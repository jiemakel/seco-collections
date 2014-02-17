package fi.seco.collections.collection.primitive;

import java.util.AbstractList;

public class LongArrayToList extends AbstractList<Long> {

	long[] array;

	public LongArrayToList(long[] array) {
		this.array = array;
	}

	@Override
	public Long get(int index) {
		return array[index];
	}

	@Override
	public Long set(int index, Long object) {
		long tmp = array[index];
		array[index] = object;
		return tmp;
	}

	@Override
	public int size() {
		return array.length;
	}

}
