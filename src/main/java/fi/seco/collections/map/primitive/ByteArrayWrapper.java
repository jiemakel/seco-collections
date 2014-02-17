package fi.seco.collections.map.primitive;

import java.io.Serializable;
import java.util.Arrays;

public class ByteArrayWrapper implements Serializable {

	private static final long serialVersionUID = 1L;

	public final byte[] array;
	private final int hashCode;

	@Override
	public int hashCode() {
		return hashCode;
	}

	@Override
	public boolean equals(Object obj) {
		return Arrays.equals(array, ((ByteArrayWrapper) obj).array);
	}

	public ByteArrayWrapper(byte[] array) {
		this.array = array;
		hashCode = Arrays.hashCode(array);
	}

}
