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

import java.util.HashSet;
import java.util.Iterator;

public class UniqueFilteringIterator<E> extends AFilteringIterator<E> {

	public UniqueFilteringIterator(Iterator<? extends E> arg0) {
		super(arg0);
	}

	public UniqueFilteringIterator(Iterable<? extends E> arg0) {
		super(arg0);
	}

	public UniqueFilteringIterator(IIterableIterator<? extends E> arg0) {
		super(arg0);
	}

	private final HashSet<E> seen = new HashSet<E>();

	@Override
	protected final boolean accept(E e) {
		return seen.add(e);
	}
}
