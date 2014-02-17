/*
 * Copyright (c) 2003 Helsinki Institute for Information Technology
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package fi.seco.collections.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author ssaarela
 * @created 22.9.2004
 */
public class SkipMaxIterator<E> extends AIterableIterator<E> {

	private int max;

	private Iterator<? extends E> iter;

	public SkipMaxIterator(Iterable<? extends E> c, int skip, int max) {
		this(c.iterator(), skip, max);
	}

	public SkipMaxIterator(Iterator<? extends E> iter, int skip, int max) {
		this.iter = iter;
		this.max = max;

		// roll over skip limit
		while (skip > 0) {
			iter.next();
			skip--;
		}
	}

	@Override
	public final void remove() {
		iter.remove();
	}

	@Override
	public final boolean hasNext() {
		return max != 0 && iter.hasNext();
	}

	@Override
	public final E next() {
		if (max != 0) {
			max--;
			return iter.next();
		}
		throw new NoSuchElementException();
	}

}
