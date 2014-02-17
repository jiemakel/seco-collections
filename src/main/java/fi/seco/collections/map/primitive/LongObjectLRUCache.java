package fi.seco.collections.map.primitive;

public class LongObjectLRUCache<V> extends ALongObjectLRUCache<V> {

	private final int maxEntries;
	private int size = 0;

	public LongObjectLRUCache(int initialCapacity, float loadFactor, int maxEntries) {
		super(initialCapacity, loadFactor);
		this.maxEntries = maxEntries;
	}

	public LongObjectLRUCache(int initialCapacity, int maxEntries) {
		super(initialCapacity);
		this.maxEntries = maxEntries;
	}

	public LongObjectLRUCache(int maxEntries) {
		super();
		this.maxEntries = maxEntries;
	}

	@Override
	protected boolean isTooLarge() {
		return size > maxEntries;
	}

	@Override
	protected void removed(V content) {
		size--;
	}

	@Override
	protected void added(V content) {
		size++;
	}

	@Override
	protected void cleared() {
		size = 0;
	}

}
