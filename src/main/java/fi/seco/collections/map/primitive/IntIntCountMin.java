package fi.seco.collections.map.primitive;

public class IntIntCountMin {

	private final int[][] hashes;
	private static final long mprime = 2147483647L;
	private final int width;

	private static final int[] primes = { 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97 };

	private final int hash(long id, long a, long b) {
		long tmp = (a * id) + b;
		tmp = (tmp >>> 31) + tmp & mprime;
		return (int) tmp & width;
	}

	private final int hash(int id, int hashnum) {
		return hash(id, primes[hashnum], hashnum); // 1<=a<=p-1, 0<=b<=p
	}

	public IntIntCountMin() {
		this(0.01f, 0.00001f);
	}

	public IntIntCountMin(float accuracy, float certainty) {
		int hashcount = (int) (Math.log(1 / certainty) + 1);
		hashes = new int[hashcount][];
		int limit = (int) (Math.E / accuracy + 1);
		int j = 1;
		while (true) {
			if (1 << j > limit) {
				limit = 1 << j;
				break;
			}
			j++;
		}
		width = limit - 1;
		for (int i = 0; i < hashcount; i++)
			hashes[i] = new int[limit];
	}

	public int inc(int id, int count) {
		int min = Integer.MAX_VALUE;
		for (int i = 0; i < hashes.length; i++) {
			int loc = hash(id, i);
			hashes[i][loc] += count;
			int cur = hashes[i][loc];
			if (cur < min) min = cur;
		}
		return min;
	}

	public int get(int id) {
		int min = Integer.MAX_VALUE;
		for (int i = 0; i < hashes.length; i++) {
			int cur = hashes[i][hash(id, i)];
			if (cur < min) min = cur;
		}
		return min;
	}

}
