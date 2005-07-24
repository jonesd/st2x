/*
 * Abora-White
 * Part of the Abora hypertext project: http://www.abora.org
 * Copyright 2003 David G Jones
 * 
 * Based on the Udanax-Gold source code: http://www.udanax.com
 * Copyright 1979-1999 Udanax.com. All rights reserved
 * 
 * $Id$
 */
package org.abora.gold.collection.basic;

import java.io.PrintWriter;

import org.abora.gold.x.PrimFloatValue;
import org.abora.gold.x.PrimIEEE64;
import org.abora.gold.x.PrimSpec;
import org.abora.gold.xpp.basic.Heaper;

/**
 * Concrete fixed size array that holds elements of the Java <code>double</code>
 * floating-point primitive data type. This is conceptually associated with
 * the double-precision 64-bit format IEEE 754 values.
 */
public class IEEE64Array extends PrimFloatArray {
	private final double[] storage;

	//////////////////////////////////////////////
	// Constructors

	/** 
	 * Construct a new array of the specified size with
	 * all elements initialized to zero.
	 *
	 * Restrict public access to constructor; use suitable static
	 * factory method instead.  
	 * 
	 * @param count size of array
	 */
	protected IEEE64Array(int count) {
		super();
		storage = new double[count];
	}

	/** 
	 * Construct a new array of the specified size with
	 * all elements initialized to zero.
	 *
	 * Restrict public access to constructor; use suitable static
	 * factory method instead.  
	 * 
	 * @param count number of elements this will be able to hold
	 */

	protected IEEE64Array(int size, PrimArray from, int sourceOffset, int count, int destOffset) {
		this(size);
		int n = count;
		if (count == -1) {
			n = from.count() - sourceOffset;
		}
		copyElements(destOffset, from, sourceOffset, n);
	}

	/** 
	 * Construct a new array of the same size as the specified source
	 * and containing a copy of its content. 
	 *
	 * Restrict public access to constructor; use suitable static
	 * factory method instead.  
	 * 
	 * @param source primitive array to copy
	 */
	protected IEEE64Array(double[] source) {
		this(source.length);
		System.arraycopy(source, 0, storage, 0, source.length);
	}

	protected PrimArray makeNew(int size, PrimArray source, int sourceOffset, int count, int destOffset) {
		return make(size, (PrimFloatArray) source, sourceOffset, count, destOffset);
	}

	//////////////////////////////////////////////
	// Static Factory Methods

	/** 
	 * Return a new IEEE64Array of the specified size suitable for
	 * holding IEEE64 values, initially filled with zeros.
	 *  
	 * @param count number of elements this will be able to hold
	 */
	public static IEEE64Array make(int count) {
		return new IEEE64Array(count);
	}

	/** create an IEEE64Array filled with the indicated data in 'from' */
	public static IEEE64Array make(int size, PrimArray from, int sourceOffset, int count, int destOffset) {
		return new IEEE64Array(size, from, sourceOffset, count, destOffset);
	}

	public static IEEE64Array make(int size, PrimArray from, int sourceOffset, int count) {
		return make(size, from, sourceOffset, count, 0);
	}

	public static IEEE64Array make(int size, PrimArray from, int sourceOffset) {
		return make(size, from, sourceOffset, -1);
	}

	public static IEEE64Array make(int size, PrimArray from) {
		return make(size, from, 0);
	}

	public static IEEE64Array make(PrimArray from) {
		return make(from.count(), from);
	}

	/** create an IEEE64Array filled with the data at 'buffer' */
	public static IEEE64Array make(double[] buffer) {
		return new IEEE64Array(buffer);
	}

	//////////////////////////////////////////////
	// accessing

	/** Store an actual floating point value */
	public void storeIEEE64(int index, double value) {
		storage[index] = value;
	}

	/** Get an actual floating point number */
	public double iEEE64At(int index) {
		return storage[index];
	}

	public void storeFloat(int index, double value) {
		storeIEEE64(index, value);
	}

	public double floatAt(int index) {
		return iEEE64At(index);
	}

	public void storeValue(int index, Heaper value) {
		if (value == null) {
			throw new NullPointerException();
		}
		storeIEEE64(index, ((PrimFloatValue) value).asIEEE64());
	}

	public Heaper fetchValue(int index) {
		return PrimIEEE64.make(iEEE64At(index));
	}

	public int count() {
		return storage.length;
	}

	public PrimSpec spec() {
		return PrimSpec.iEEE64();
	}

	public int bitCount() {
		return 64;
	}

	//////////////////////////////////////////////
	// Bulk Storing

	public void storeAll(Heaper value, int count, int start) {
		int n = count() - start;
		if (count > n) {
			throw new IndexOutOfBoundsException();
		}
		if (count >= 0) {
			n = count;
		}
		double floatValue;
		if (value == null) {
			floatValue = 0.0;
		} else {
			floatValue = ((PrimFloatValue) value).asIEEE64();
		}
		for (int i = 0; i < n; i += 1) {
			storeIEEE64(start + i, floatValue);
		}
	}

	public void copyToBuffer(double[] buffer, int count, int start) {
		int n;
		if (count >= 0) {
			n = count;
		} else {
			n = count() - start;
		}
		if (n > buffer.length) {
			n = buffer.length;
		}
		System.arraycopy(storage, start, buffer, 0, n);
	}

	protected void copyElements(int to, PrimArray source, int from, int count) {
		int n = count;
		if (n == -1) {
			n = source.count() - from;
		}
		double[] sourceStorage = ((IEEE64Array) source).storage;
		System.arraycopy(sourceStorage, from, storage, to, n);
	}

	//////////////////////////////////////////////
	// Comparing and Hashing

	protected int compareData(int start, PrimDataArray other, int otherStart, int count) {
		if (other instanceof IEEE64Array) {
			IEEE64Array o = (IEEE64Array) other;
			for (int i = 0; i < count; i += 1) {
				double cmp = iEEE64At(i + start) - o.iEEE64At(i + otherStart);
				if (cmp != 0.0) {
					return cmp < 0.0 ? -1 : 1;
				}
			}
			return 0;
		} else {
			return super.compareData(start, other, otherStart, count);
		}
	}

	protected int signOfNonZeroAfter(int start) {
		for (int i = start; i < count(); i += 1) {
			double val = iEEE64At(i);
			if (val < 0.0) {
				return -1;
			}
			if (val > 0.0) {
				return +1;
			}
		}
		return 0;
	}

	//////////////////////////////////////////////
	// Arithmetic Manipulations

	protected void addData(int start, PrimDataArray other, int otherStart, int count) {
		if (other instanceof IEEE64Array) {
			IEEE64Array o = (IEEE64Array) other;
			for (int i = 0; i < count; i += 1) {
				storeIEEE64(i + start, iEEE64At(i + start) + o.iEEE64At(i + otherStart));
			}
		} else {
			super.addData(start, other, otherStart, count);
		}
	}

	protected void subtractData(int start, PrimDataArray other, int otherStart, int count) {
		if (other instanceof IEEE64Array) {
			IEEE64Array o = (IEEE64Array) other;
			for (int i = 0; i < count; i += 1) {
				storeIEEE64(i + start, iEEE64At(i + start) - o.iEEE64At(i + otherStart));
			}
		} else {
			super.subtractData(start, other, otherStart, count);
		}
	}

	//////////////////////////////////////////////
	// Printing

	protected void printElementOn(int index, PrintWriter oo) {
		oo.print(iEEE64At(index));
	}
}
