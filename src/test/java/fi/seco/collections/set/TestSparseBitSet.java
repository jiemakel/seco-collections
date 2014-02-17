package fi.seco.collections.set;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.Test;

import com.carrotsearch.hppc.LongOpenHashSet;
import com.carrotsearch.hppc.LongSet;
import com.carrotsearch.hppc.cursors.LongCursor;

import fi.seco.collections.iterator.primitive.IOrderedLongAdvanceIterator;

public class TestSparseBitSet {

	@Test
	public void testAddAndRemove() {
		SparseBitSet sbs = new SparseBitSet();
		assertEquals(0, sbs.cardinality());
		assertTrue(sbs.isEmpty());
		sbs.set(17132);
		assertTrue(sbs.get(17132));
		assertEquals(1, sbs.cardinality());
		sbs.set(17132);
		assertEquals(1, sbs.cardinality());
		sbs.set(17);
		sbs.set(132);
		sbs.set(19);
		sbs.clear(17);
		assertEquals(3, sbs.cardinality());
	}

	@Test
	public void testIterator() {
		SparseBitSet sbs = new SparseBitSet();
		sbs.set(17);
		sbs.set(132);
		sbs.set(19);
		sbs.set(2217132);
		sbs.set(Long.MAX_VALUE);
		sbs.set(17132);
		Iterator<LongCursor> ti = sbs.iterator();
		assertEquals(17, ti.next().value);
		assertEquals(19, ti.next().value);
		assertEquals(132, ti.next().value);
		assertEquals(17132, ti.next().value);
		assertEquals(2217132, ti.next().value);
		assertEquals(Long.MAX_VALUE, ti.next().value);
		assertThat(ti.hasNext(), is(false));
	}

	@Test
	public void testOrderedLongAdvanceIterator() {
		SparseBitSet sbs = new SparseBitSet();
		sbs.set(17);
		sbs.set(132);
		sbs.set(19);
		sbs.set(2217132);
		sbs.set(29772402);
		sbs.set(Long.MAX_VALUE / 2);
		sbs.set(17132);
		IOrderedLongAdvanceIterator ti = sbs.orderedAdvanceIterator();
		assertEquals(17, ti.next());
		assertEquals(19, ti.next());
		assertEquals(132, ti.next());
		assertEquals(17132, ti.next());
		assertEquals(2217132, ti.next());
		assertEquals(29772402, ti.next());
		assertEquals(Long.MAX_VALUE / 2, ti.next());
		assertEquals(Long.MAX_VALUE, ti.next());
		ti = sbs.orderedAdvanceIterator();
		assertEquals(17, ti.next());
		assertEquals(132, ti.advance(20));
		assertEquals(2217132, ti.advance(2217132));
		assertEquals(2217132, ti.advance(2217132));
		assertEquals(29772402, ti.advance(2217133));
		assertEquals(Long.MAX_VALUE / 2, ti.next());
		ti = sbs.orderedAdvanceIterator();
		assertEquals(Long.MAX_VALUE, ti.advance(Long.MAX_VALUE / 2 + 1));
		sbs.clear();
		sbs.set(938388);
		sbs.set(1485229);
		sbs.set(1487326);
		sbs.set(1487962);
		sbs.set(1488269);
		sbs.set(1488752);
		sbs.set(1489710);
		sbs.set(1490287);
		sbs.set(1490954);
		sbs.set(1491705);
		sbs.set(6580060);
		sbs.set(19351326);
		sbs.set(19353360);
		sbs.set(20311136);
		sbs.set(20314366);
		sbs.set(20316287);
		sbs.set(20319441);
		ti = sbs.orderedAdvanceIterator();
		assertThat(ti.advance(0), is(938388l));
		assertThat(ti.advance(9146696), is(19351326l));
	}

	@Test
	public void testAnd() {
		SparseBitSet sbs1 = new SparseBitSet();
		sbs1.set(17);
		sbs1.set(132);
		sbs1.set(19);
		sbs1.set(2217132);
		sbs1.set(Long.MAX_VALUE);
		sbs1.set(17132);
		assertTrue(sbs1.get(17));
		assertTrue(sbs1.get(19));
		assertTrue(sbs1.get(132));
		assertTrue(sbs1.get(17132));
		assertTrue(sbs1.get(2217132));
		assertTrue(sbs1.get(Long.MAX_VALUE));
		SparseBitSet sbs2 = new SparseBitSet();
		sbs2.set(17);
		sbs2.set(19);
		sbs2.set(Long.MAX_VALUE);
		sbs2.set(17132);
		sbs2.set(1234687);
		sbs1.and(sbs2);
		assertEquals(4, sbs1.cardinality());
		assertTrue(sbs1.get(17));
		assertTrue(sbs1.get(19));
		assertTrue(!sbs1.get(132));
		assertTrue(sbs1.get(17132));
		assertTrue(!sbs1.get(2217132));
		assertTrue(sbs1.get(Long.MAX_VALUE));
		LongSet ts = new LongOpenHashSet();
		Iterator<LongCursor> ti = sbs1.iterator();
		while (ti.hasNext())
			ts.add(ti.next().value);
		LongSet comp = new LongOpenHashSet();
		comp.add(17);
		comp.add(19);
		comp.add(17132);
		comp.add(Long.MAX_VALUE);
		assertEquals(comp, ts);
	}

	@Test
	public void testOr() {
		SparseBitSet sbs1 = new SparseBitSet();
		sbs1.set(17);
		sbs1.set(132);
		sbs1.set(2217132);
		sbs1.set(17132);
		SparseBitSet sbs2 = new SparseBitSet();
		sbs2.set(17);
		sbs2.set(19);
		sbs2.set(Long.MAX_VALUE);
		sbs2.set(17132);
		sbs2.set(1234687);
		sbs1.or(sbs2);
		assertEquals(7, sbs1.cardinality());
		assertTrue(sbs1.get(17));
		assertTrue(sbs1.get(19));
		assertTrue(sbs1.get(132));
		assertTrue(sbs1.get(17132));
		assertTrue(sbs1.get(1234687));
		assertTrue(sbs1.get(2217132));
		assertTrue(sbs1.get(Long.MAX_VALUE));
	}

}
