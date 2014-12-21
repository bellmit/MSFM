package com.cboe.util;

import java.util.*;
import junit.framework.*;

public class ByteArrayPool
{
	public static boolean verbose = false;
	
	private static ByteArrayPool staticPool = null;

	protected Hashtable borrowed = new Hashtable();
	
	protected Hashtable arrays = new Hashtable();
	protected IntHolder protectedSearchKey = new IntHolder();
	protected int       totalAvailable  = 0;

	protected class IntHolder
	{
		IntHolder() {}
		IntHolder(int _i) { i = _i; }
		public int hashCode() { return i; }
		public boolean equals(Object x)
		{
			return IntHolder.class.isInstance(x) && ((IntHolder)x).i == i;
		}
		int i=0;
	}

	/**
	 * Inner class for support of automated unit testing.
	 */
	static public class Test extends TestCase
	{
		public static TestSuite suite()
		{
			TestSuite result = new TestSuite();
			result.addTest(new Test("testPool"));
			return result;
		}

		public void testPool()
		{
			// Borrow four at a time: b0, b1, b1, b4, r0, b5, r1, b4 (r=return, b=borrow)
			//
			// Afer processing, there should be, in the pool, the following array sizes:
			//
			// 0 1 1 4 4 5 9 1024 2048 4096 4096
			//
			int[] sizes = { 0, 1, 1, 4, 5, 4, 1024, 4096, 4096, 2048, 9, 4, 1, 1 };

			byte[][] arrays = 
			{ 
				getInstance().borrowArray(sizes[0]),
				getInstance().borrowArray(sizes[1]),
				getInstance().borrowArray(sizes[2]),
				getInstance().borrowArray(sizes[3])
			};
			
			int nextIdx=0;
			
			for (int i=4; i < sizes.length; ++i)
			{
				nextIdx = i%4;
				assertTrue("A borrowed array was null! Can't recycle that! ", arrays[nextIdx] != null);
				getInstance().recycleArray(arrays[nextIdx]);
				arrays[nextIdx] = getInstance().borrowArray(sizes[i]);
			}

			
			assertTrue("Expected 7 arrays to be available in the pool, got " + getInstance().totalAvailable, getInstance().totalAvailable == 7);
			
			for (int i=0; i < 4; ++i)
			{
				getInstance().recycleArray(arrays[nextIdx]);
				nextIdx = (nextIdx + 1)%4;
			}
			
			assertTrue("Expected 11 arrays to be available in the pool, got " + getInstance().totalAvailable, getInstance().totalAvailable == 11);
		}
		
		public Test(String methodName)
		{
			super(methodName);
		}
		public static void main(String args[])
		{
			System.out.println("Unit testing CustomInputStream");
			junit.textui.TestRunner.run(suite());
		}
	}
/**
 * Add <code>numCopies</code> of byte arrays of <code>length</code> bytes to the pool.
 * This is a useful operation to execute before a block of code which is expected to request
 * multiple arrays of a given size (ie, it'll minimize the cost of the high-watermark
 * algorithm).
 * 
 * @author Steven Sinclair
 * @param length the length of the byte arrays to add
 * @param numCopies the number of copies of byte arrayas of length <code>length</code> to add.
 */
public synchronized void addToPool(int length, int numCopies)
{
	protectedSearchKey.i = length;
	Vector holder = (Vector)arrays.get(protectedSearchKey);
	totalAvailable += numCopies;
	if (holder == null)
	{
		holder = new Vector(Math.max(10, numCopies));
		arrays.put(new IntHolder(length), holder);
		if (verbose)
			System.out.println("addToPool: created new holder object for arrays of length " + length + " (holding 1 array)");
	}

	if (numCopies > -1)
	{
		// Since most vector operations are synchronized, wrapping this
		// for loop in a block synchronized on the holder.arrays vector
		// will minimize the cost of the 2*n synchronizations performed
	 	// by this loop.
		//
		synchronized (holder)
		{
			for (int i=0; i < numCopies; ++i)
			{
				holder.addElement(new byte[length]);
			}
			if (verbose)
				System.out.println("addToPool: added " + numCopies + " arrays of length " + length);
		}
	}
}
/**
 * Borrow an array of size <code>length</code> from the pool.
 * 
 * @author Steven Sinclair
 * @return byte[]
 * @param length int
 */
public synchronized byte[] borrowArray(int length)
{
	protectedSearchKey.i = length;
	Vector holder = (Vector)arrays.get(protectedSearchKey);
	byte[] ret;

	if (holder == null)
	{
		// If this length has never been asked for before, add to pool as 
		// unavailable and return the array.
		//
		holder = new Vector();
		arrays.put(new IntHolder(length), holder);
		if (verbose)
			System.out.println("borrowArray:  Created new holder for arrays of length " + length);
	}
	
	if (holder.size() == 0)
	{
		// No arrays are available: add another (marked as unavailable) & return it.
		//
		ret = new byte[length];
		if (verbose)
			System.out.println("borrowArray:  CREATED array[" + length + "]");
	}
	else
	{
		synchronized(holder)
		{
			ret = (byte[])holder.firstElement();
			holder.removeElementAt(0);
			--totalAvailable;
			if (verbose)
				System.out.println("borrowArray:  RE-USE array[" + length + ']');
		}
	}
	if (ret != null)
	{
		borrowed.put(ret, this);
	}
	return ret;
}
/**
 * 
 * @author Steven Sinclair
 * @return com.cboe.util.ByteArrayPool
 */
public static ByteArrayPool getInstance()
{
	if (staticPool == null)
	{
		synchronized (ByteArrayPool.class)
		{
			if (staticPool == null)
				staticPool = new ByteArrayPool();
		}
	}
	return staticPool;
}
public static void main(String[] args)
{
	Test.main(args);
}
/**
 * Release an array back to the pool (ie, make it available again).  This method will
 * never fail, though the array passed in is not guaranteed to be returned to the pool:
 * if the user of this pool returns more arrays than were borrowed, then eventually a
 * returned array will simply be discarded.  That is, the pool is guaranteed to contain
 * <i>at most</i> the total number added and/or borrowed.
 *
 * @author Steven Sinclair
 * @param array byte[]
 */
public synchronized void recycleArray(byte[] array)
{
	if (borrowed.remove(array) == null)
	{
		if (verbose)
			System.out.println("recycleArray: The array was not found in the \"borrowed\" mapping: quietly ignore it.");
		return; // The array was not found in the "borrowed" mapping: quietly ignore it.
	}
	
	protectedSearchKey.i = array.length;
	Vector holder = (Vector)arrays.get(protectedSearchKey);
	if (holder == null)
	{
		holder = new Vector();
		arrays.put(new IntHolder(array.length), holder);
		if (verbose)
			System.out.println("recycleArray: created new holder object for arrays of length " + array.length);
	}
	
	holder.addElement(array);
	++totalAvailable;
	if (verbose)
		System.out.println("recycleArray: RECYCLE array[" + array.length + "]");
}
}
