package fi.seco.collections.map.primitive;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrotsearch.hppc.LongCollection;
import com.carrotsearch.hppc.LongContainer;
import com.carrotsearch.hppc.LongObjectAssociativeContainer;
import com.carrotsearch.hppc.LongObjectMap;
import com.carrotsearch.hppc.LongObjectOpenHashMap;
import com.carrotsearch.hppc.ObjectContainer;
import com.carrotsearch.hppc.cursors.LongObjectCursor;
import com.carrotsearch.hppc.predicates.LongPredicate;
import com.carrotsearch.hppc.procedures.LongObjectProcedure;

import fi.seco.collections.iterator.AMappingIterator;

public abstract class ALongObjectLRUCache<V> implements LongObjectMap<V> {

	private static final Logger log = LoggerFactory.getLogger(ALongObjectLRUCache.class);

	protected final LongObjectMap<Entry<V>> hashmap;

	protected transient final Entry<V> header = new Entry<V>(-1, null);

	protected abstract boolean isTooLarge();

	protected static final class Entry<V2> {
		public Entry(long key, V2 content) {
			this.key = key;
			this.content = content;
		}

		public static final int OVERHEAD_BYTES = 48;
		public static final int OVERHEAD_SHORTS = OVERHEAD_BYTES >>> 1;
		public static final int OVERHEAD_INTS = OVERHEAD_BYTES >>> 2;
		public static final int OVERHEAD_LONGS = OVERHEAD_BYTES >>> 3;

		public final long key;
		private final V2 content;

		public final V2 getContent() {
			return content;
		}

		public Entry<V2> before = this, after = this;

		public final void remove() {
			before.after = after;
			after.before = before;
		}

		public final void addBefore(Entry<V2> existingEntry) {
			after = existingEntry;
			before = existingEntry.before;
			before.after = this;
			after.before = this;
		}

	}

	public ALongObjectLRUCache() {
		hashmap = new LongObjectOpenHashMap<Entry<V>>();
	}

	public ALongObjectLRUCache(int initialCapacity) {
		hashmap = new LongObjectOpenHashMap<Entry<V>>(initialCapacity);
	}

	public ALongObjectLRUCache(int initialCapacity, float loadFactor) {
		hashmap = new LongObjectOpenHashMap<Entry<V>>(initialCapacity, loadFactor);
	}

	@Override
	public synchronized void clear() {
		hashmap.clear();
		header.before = header.after = header;
		cleared();
	}

	protected abstract void cleared();

	@Override
	public synchronized V remove(long key) {
		Entry<V> toRemove = hashmap.get(key);
		if (toRemove == null) return null;
		toRemove.remove();
		hashmap.remove(toRemove.key);
		removed(toRemove.content);
		return toRemove.getContent();
	}

	protected abstract void removed(V content);

	protected abstract void added(V content);

	@Override
	public synchronized V put(long key, V object) {
		added(object);
		while (isTooLarge()) {
			Entry<V> toRemove = header.after;
			toRemove.remove();
			hashmap.remove(toRemove.key);
			removed(toRemove.content);
		}
		Entry<V> neu = new Entry<V>(key, object);
		neu.addBefore(header);
		Entry<V> e = hashmap.put(key, neu);
		if (e != null) {
			e.remove();
			removed(e.content);
			return e.getContent();
		}
		return null;
	}

	@Override
	public synchronized V get(long key) {
		Entry<V> g = hashmap.get(key);
		if (g != null) {
			g.remove();
			g.addBefore(header);
			return g.getContent();
		}
		return null;
	}

	@Override
	public boolean containsKey(long arg0) {
		return hashmap.containsKey(arg0);
	}

	@Override
	public boolean isEmpty() {
		return hashmap.isEmpty();
	}

	@Override
	public int size() {
		return hashmap.size();
	}

	@Override
	public Iterator<LongObjectCursor<V>> iterator() {
		return new AMappingIterator<LongObjectCursor<Entry<V>>, LongObjectCursor<V>>(hashmap.iterator()) {

			private final LongObjectCursor<V> c = new LongObjectCursor<V>();

			@Override
			protected LongObjectCursor<V> map(LongObjectCursor<Entry<V>> src) {
				c.index = src.index;
				c.key = src.key;
				c.value = src.value.content;
				return c;
			}
		};
	}

	@Override
	public int removeAll(LongContainer container) {
		return hashmap.removeAll(container);
	}

	@Override
	public int removeAll(LongPredicate predicate) {
		return hashmap.removeAll(predicate);
	}

	@Override
	public <T extends LongObjectProcedure<? super V>> T forEach(final T procedure) {
		hashmap.forEach(new LongObjectProcedure<Entry<V>>() {

			@Override
			public void apply(long key, Entry<V> value) {
				procedure.apply(key, value.content);
			}
		});
		return procedure;
	}

	@Override
	public LongCollection keys() {
		return hashmap.keys();
	}

	@Override
	public ObjectContainer<V> values() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int putAll(LongObjectAssociativeContainer<? extends V> container) {
		return container.forEach(new LongObjectProcedure<V>() {

			private int count = 0;

			@Override
			public void apply(long key, V value) {
				V old = put(key, value);
				if (old == null || !old.equals(value)) count++;
			}
		}).count;
	}

	@Override
	public int putAll(Iterable<? extends LongObjectCursor<? extends V>> iterable) {
		int count = 0;
		for (LongObjectCursor<? extends V> lc : iterable) {
			V old = put(lc.key, lc.value);
			if (old == null || !old.equals(lc.value)) count++;
		}
		return count;
	}

}
