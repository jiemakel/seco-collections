package fi.seco.collections.set;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.lucene.util.OpenBitSet;

import com.carrotsearch.hppc.IntArrayList;
import com.carrotsearch.hppc.IntOpenHashSet;
import com.carrotsearch.hppc.IntSet;
import com.carrotsearch.hppc.LongArrayList;
import com.carrotsearch.hppc.cursors.LongCursor;
import com.carrotsearch.hppc.predicates.LongPredicate;
import com.carrotsearch.hppc.procedures.LongProcedure;

import fi.seco.collections.iterator.primitive.IOrderedLongAdvanceIterator;

public class SparseBitSet {

	private static final int L1SHIFT = 20;
	private static final int L2SHIFT = 20;
	private static final int BSHIFT = 63 - L1SHIFT - L2SHIFT;

	private static final int L1SIZE = 1 << L1SHIFT;
	private static final int L2SIZE = 1 << L2SHIFT;
	private static final int BSIZE = 1 << BSHIFT;

	private static final int L1TSHIFT = L2SHIFT + BSHIFT;

	private static final long L2AND = (1l << (BSHIFT + L2SHIFT)) - 1;
	private static final long BAND = BSIZE - 1;

	private OpenBitSet[][] mRootNode = new OpenBitSet[L1SIZE][];

	private final LongArrayList usedIndices = new LongArrayList();
	private boolean usedIndicesSorted = false;

	private long cardinality = -1;

	public SparseBitSet() {}

	private final OpenBitSet getExistingOpenBitSetForIndex(long aBitIndex) {
		int rootIndex = (int) (aBitIndex >>> L1TSHIFT);
		int secondLevelIndex = (int) ((aBitIndex & L2AND) >>> BSHIFT);
		return mRootNode[rootIndex][secondLevelIndex];
	}

	private final OpenBitSet getOpenBitSetForIndex(long aBitIndex, boolean shouldAllocate) {
		assert aBitIndex >= 0 : "Trying to get OpenBitSet for negative index " + aBitIndex;
		int rootIndex = (int) (aBitIndex >>> L1TSHIFT);
		OpenBitSet[] secondLevelNode = mRootNode[rootIndex];
		if (secondLevelNode == null) if (shouldAllocate)
			synchronized (mRootNode) {
				secondLevelNode = mRootNode[rootIndex];
				if (secondLevelNode == null) {
					secondLevelNode = new OpenBitSet[L2SIZE];
					mRootNode[rootIndex] = secondLevelNode;
				}
			}
		else return null;
		int secondLevelIndex = (int) ((aBitIndex & L2AND) >>> BSHIFT);
		OpenBitSet leafNode = secondLevelNode[secondLevelIndex];
		if (leafNode == null && shouldAllocate) synchronized (secondLevelNode) {
			leafNode = secondLevelNode[secondLevelIndex];
			if (leafNode == null) {
				leafNode = new OpenBitSet(BSIZE);
				secondLevelNode[secondLevelIndex] = leafNode;
				usedIndices.add(aBitIndex & ~BAND);
				usedIndicesSorted = false;
			}
		}
		return leafNode;
	}

	public void set(long aBitIndex) {
		OpenBitSet obs = getOpenBitSetForIndex(aBitIndex, true);
		synchronized (obs) {
			obs.fastSet(aBitIndex & BAND);
		}
		cardinality = -1;
	}

	public void clear(long aBitIndex) {
		OpenBitSet b = getOpenBitSetForIndex(aBitIndex, false);
		if (b != null) b.fastClear(aBitIndex & BAND);
		cardinality = -1;
	}

	public boolean get(long aBitIndex) {
		OpenBitSet node = getOpenBitSetForIndex(aBitIndex, false);
		if (node == null) return false;
		return node.get(aBitIndex & BAND);
	}

	public boolean isEmpty() {
		return cardinality() == 0;
	}

	public void clear() {
		mRootNode = new OpenBitSet[L1SIZE][];
		cardinality = 0;
		usedIndices.clear();
	}

	public long cardinality() {
		if (cardinality >= 0) return cardinality;
		cardinality = 0;
		usedIndices.forEach(new LongProcedure() {

			@Override
			public void apply(long value) {
				cardinality += getExistingOpenBitSetForIndex(value).cardinality();
			}

		});
		return cardinality;
	}

	public final void compact() {
		final IntArrayList rootsThatMightBeCleared = new IntArrayList();
		final IntSet rootsThatCannotBeCleared = new IntOpenHashSet();
		usedIndices.retainAll(new LongPredicate() {

			@Override
			public boolean apply(long aBitIndex) {
				int rootIndex = (int) (aBitIndex >>> L1TSHIFT);
				int secondLevelIndex = (int) ((aBitIndex & L2AND) >>> BSHIFT);
				OpenBitSet b = mRootNode[rootIndex][secondLevelIndex];
				if (b == null || b.isEmpty()) {
					rootsThatMightBeCleared.add(rootIndex);
					mRootNode[rootIndex][secondLevelIndex] = null;
					return false;
				} else rootsThatCannotBeCleared.add(rootIndex);
				return true;
			}
		});
		for (int i = rootsThatMightBeCleared.size(); i-- > 0;) {
			int rootIndex = rootsThatMightBeCleared.get(i);
			if (!rootsThatCannotBeCleared.contains(rootIndex)) mRootNode[rootIndex] = null;
		}
	}

	public void and(final SparseBitSet other) {
		if (usedIndices.forEach(new LongProcedure() {
			boolean compact = false;

			@Override
			public void apply(long aBitIndex) {
				int rootIndex = (int) (aBitIndex >>> L1TSHIFT);
				int secondLevelIndex = (int) ((aBitIndex & L2AND) >>> BSHIFT);
				OpenBitSet oobs = other.getOpenBitSetForIndex(aBitIndex, false);
				if (oobs == null) {
					mRootNode[rootIndex][secondLevelIndex] = null;
					compact = true;
				} else mRootNode[rootIndex][secondLevelIndex].and(oobs);
			}
		}).compact) compact();
		cardinality = -1;
	}

	public void andNot(final SparseBitSet other) {
		usedIndices.forEach(new LongProcedure() {

			@Override
			public void apply(long aBitIndex) {
				OpenBitSet oobs = other.getOpenBitSetForIndex(aBitIndex, false);
				if (oobs == null) return;
				int rootIndex = (int) (aBitIndex >>> L1TSHIFT);
				int secondLevelIndex = (int) ((aBitIndex & L2AND) >>> BSHIFT);
				mRootNode[rootIndex][secondLevelIndex].andNot(oobs);
			}
		});
		cardinality = -1;
	}

	public void or(final SparseBitSet other) {
		other.usedIndices.forEach(new LongProcedure() {

			@Override
			public void apply(long aBitIndex) {
				OpenBitSet oobs = other.getExistingOpenBitSetForIndex(aBitIndex);
				getOpenBitSetForIndex(aBitIndex, true).or(oobs);
			}

		});
		cardinality = -1;
	}

	public Iterator<LongCursor> iterator() {
		return new Iterator<LongCursor>() {

			private final Iterator<LongCursor> ui = usedIndices.iterator();

			private OpenBitSet current;
			private long curBase = 0;
			private final LongCursor lc = new LongCursor();
			private long next = -1;
			private long cindex = -1;

			private void generateNext() {
				do {
					if (cindex == -1) {
						if (!ui.hasNext()) return;
						curBase = ui.next().value;
						current = getExistingOpenBitSetForIndex(curBase);
					}
					cindex = current.nextSetBit(cindex + 1);
				} while (cindex == -1);
				next = curBase + cindex;
			}

			@Override
			public final boolean hasNext() {
				if (next != -1) return true;
				generateNext();
				return next != -1;
			}

			@Override
			public final LongCursor next() {
				if (hasNext()) {
					lc.value = next;
					next = -1;
					return lc;
				}
				throw new NoSuchElementException();
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

		};
	}

	@Override
	public String toString() {
		final long[] array = new long[(int) cardinality()];
		forEach(new LongProcedure() {
			int i = 0;

			@Override
			public void apply(long value) {
				array[i++] = value;
			}

		});
		return Arrays.toString(array);
	}

	public <T extends LongProcedure> T forEach(final T procedure) {
		usedIndices.forEach(new LongProcedure() {

			@Override
			public void apply(long aBitIndex) {
				OpenBitSet set = getExistingOpenBitSetForIndex(aBitIndex);
				long cbase = aBitIndex & ~BAND;
				for (int i = set.nextSetBit(0); i >= 0; i = set.nextSetBit(i + 1))
					procedure.apply(cbase + i);
			}

		});
		return procedure;
	}

	public <T extends LongPredicate> T forEach(final T procedure) {
		usedIndices.forEach(new LongPredicate() {

			@Override
			public boolean apply(long aBitIndex) {
				OpenBitSet set = getExistingOpenBitSetForIndex(aBitIndex);
				long cbase = aBitIndex & ~BAND;
				for (int i = set.nextSetBit(0); i >= 0; i = set.nextSetBit(i + 1))
					if (!procedure.apply(cbase + i)) return false;
				return true;
			}

		});
		return procedure;

	}

	public long retainAll(final LongPredicate predicate) {
		long c = usedIndices.forEach(new LongProcedure() {

			private long c = 0;

			@Override
			public void apply(long aBitIndex) {
				OpenBitSet set = getExistingOpenBitSetForIndex(aBitIndex);
				long cbase = aBitIndex & ~BAND;
				for (int i = set.nextSetBit(0); i >= 0; i = set.nextSetBit(i + 1))
					if (!predicate.apply(cbase + i)) {
						c++;
						set.clear(i);
					}
			}

		}).c;
		cardinality -= c;
		return c;

	}

	public IOrderedLongAdvanceIterator orderedAdvanceIterator() {
		if (!usedIndicesSorted) {
			Arrays.sort(usedIndices.buffer, 0, usedIndices.size());
			usedIndicesSorted = true;
		}
		return new IOrderedLongAdvanceIterator() {

			private OpenBitSet current;
			private long cindex = -1;
			private int cuiindex = -1;
			private long curBase = Long.MIN_VALUE;

			@Override
			public long next() {
				do {
					if (cindex == -1) {
						if (++cuiindex == usedIndices.elementsCount) return Long.MAX_VALUE;
						curBase = usedIndices.get(cuiindex);
						current = getExistingOpenBitSetForIndex(curBase);
					}
					cindex = current.nextSetBit(cindex + 1);
				} while (cindex == -1);
				return curBase + cindex;
			}

			@Override
			public long advance(long value) {
				if (curBase <= value - BSIZE) {
					cuiindex = Arrays.binarySearch(usedIndices.buffer, cuiindex + 1, usedIndices.elementsCount, value & ~BAND);
					if (cuiindex < 0) {
						if (cuiindex == -usedIndices.elementsCount - 1) return Long.MAX_VALUE;
						cuiindex = -cuiindex - 2;
						if (cuiindex == -1) { //sought value is less than first value in whole set
							cindex = -1;
							return next();
						}
					}
					curBase = usedIndices.get(cuiindex);
					current = getExistingOpenBitSetForIndex(curBase);
					if (curBase <= value - BSIZE) { //sought value is greater than the max value in the set found 
						cindex = -1;
						return next();
					}
				}
				cindex = current.nextSetBit(value & BAND);
				if (cindex == -1) return next();
				return curBase + cindex;
			}
		};
	}
}
