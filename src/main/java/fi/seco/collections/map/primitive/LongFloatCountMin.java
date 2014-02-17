package fi.seco.collections.map.primitive;

public class LongFloatCountMin {

	private final float[][] hashes;
	private static final long prime = 2147483659L;
	private final int width;

	private static final int[] primes = { 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97 };

	private final int hash(long id, long a, long b) {
		int r = (int) ((a * id + b) % prime) % width;
		return (r < 0) ? -r : r;
	}

	private final int hash(long id, int hashnum) {
		return hash(id, primes[hashnum], hashnum); // 1<=a<=p-1, 0<=b<=p
	}

	public LongFloatCountMin() {
		this(0.01f, 0.00001f);
	}

	public LongFloatCountMin(float accuracy, float certainty) {
		int hashcount = (int) (Math.log(1 / certainty) + 1);
		hashes = new float[hashcount][];
		width = (int) (Math.E / accuracy + 1);
		for (int i = 0; i < hashcount; i++)
			hashes[i] = new float[width];
	}

	public float inc(long id, float count) {
		float min = Float.POSITIVE_INFINITY;
		for (int i = 0; i < hashes.length; i++) {
			int loc = hash(id, i);
			hashes[i][loc] += count;
			float cur = hashes[i][loc];
			if (cur < min) min = cur;
		}
		return min;
	}

	public float get(long id) {
		float min = Float.POSITIVE_INFINITY;
		for (int i = 0; i < hashes.length; i++) {
			float cur = hashes[i][hash(id, i)];
			if (cur < min) min = cur;
		}
		return min;
	}

}
