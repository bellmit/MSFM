package com.cboe.domain.util;

import java.io.*;
import junit.framework.*;

/**
 *  Provide a static-sized pool of custom input streams having a replaceable
 *  byte array input stream as the underlying stream.  This allows message and
 *  field classes to cheaply access the various byte-level parsing abilities of
 *  the <code>CustomOutputStream</code> class.
 *
 *  <p>Note that the <code>borrowStream()</code> method is guaranteed to always
 *  return an appropriate <code>CustomInputStream</code>, and that only borrowed
 *  streams will be reinserted into the pool.  Further note that the recycleStream
 *  method never reports any failure: if a stream cannot be recycled, then it is
 *  quietly ignored.
 */
public class CustomOutputStreamPool
{
	/**
	 *   The size of the pool which will be returned by calling 
	 *   <code>getInstance()</code>.
	 */
	public static final int DEFAULT_POOL_SIZE = 4;
	
	private static CustomOutputStreamPool staticInstance;
	private int numStreamsAvailable;
	private final byte[] emptyByteArray = new byte[0];
	private CustomOutputStream[] customOutPool = null;

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
			CustomOutputStreamPool pool = new CustomOutputStreamPool(3);	// max. of 3 streams in the pool.
			
			CustomOutputStream[] streams = { null, null, null };

			assertTrue("Expected 3 streams to be initially available", pool.numStreamsAvailable == 3);
			
			streams[0] = pool.borrowStream();
			assertTrue("Borrowed stream was null!", streams[0] != null);
			assertTrue("Expected 2 streams to be available.", pool.numStreamsAvailable == 2);
			streams[1] = pool.borrowStream();
			assertTrue("Borrowed stream was null!", streams[1] != null);
			assertTrue("Expected 1 streams to be available.", pool.numStreamsAvailable == 1);

			pool.recycleStream(streams[0]);
			assertTrue("Expected 2 stream to be available.", pool.numStreamsAvailable == 2);
			pool.recycleStream(streams[1]);
			assertTrue("Expected 3 streams to be available.", pool.numStreamsAvailable == 3);
			streams[2] = pool.borrowStream();
			assertTrue("Borrowed stream was null!", streams[2] != null);
			assertTrue("Expected 2 streams to be available.", pool.numStreamsAvailable == 2);

			pool.recycleStream(new CustomOutputStream(new PipedOutputStream()));
			assertTrue("Expected 2 stream to be available (recycled unknown stream)", pool.numStreamsAvailable == 2);

			pool.recycleStream(streams[2]);
			assertTrue("Expected 3 streams to be available.", pool.numStreamsAvailable == 3);

			CustomOutputStream another = pool.borrowStream();
			pool.recycleStream(another); 
			assertTrue("Expected 3 streams to be available, not " + pool.numStreamsAvailable, pool.numStreamsAvailable == 3);
		}
		
		public Test(String methodName)
		{
			super(methodName);
		}
		public static void main(String args[])
		{
			System.out.println("Unit testing CustomOutputStreamPool");
			junit.textui.TestRunner.run(suite());
		}
	}
/**
 * Create a pool of the given size.
 */
public CustomOutputStreamPool(int poolSize)
{
	customOutPool = new CustomOutputStream[poolSize];

	for (int i=0; i < customOutPool.length; ++i)
	{
		customOutPool[i] = new CustomOutputStream(new ByteArrayOutputStream());
	}
	numStreamsAvailable = poolSize;
}
/**
 * Get a byte array from the pool.  If no arrays are available from the pool then a new instance
 * will be created & returned.  This method is guaranteed to always return a CustomInputStream.
 * 
 * @author Steven Sinclair
 * @return com.cboe.util.CustomInputStream
 * @param bytes byte[]
 * @param offset int
 * @param len int
 */
public synchronized CustomOutputStream borrowStream()
{
	if (numStreamsAvailable == 0)
		return new CustomOutputStream(new ByteArrayOutputStream());
	
	CustomOutputStream customOut = customOutPool[--numStreamsAvailable];
	((ByteArrayOutputStream)customOut.getUnderlying()).reset();
	return customOut;
}
/**
 * 
 * @author Steven Sinclair
 * @return com.cboe.externalIntegrationServices.tipsAdapter.message.CustomInputStreamPool
 */
public static CustomOutputStreamPool getInstance()
{
	if (staticInstance == null)
	{
		synchronized(CustomOutputStream.class)
		{
			if (staticInstance == null)
			{
				staticInstance = new CustomOutputStreamPool(DEFAULT_POOL_SIZE);
			}
		}
	}
	return staticInstance;
}
/**
 * 
 * @author Steven Sinclair
 * @param args java.lang.String[]
 */
public static void main(String args[])
{
	Test.main(args);
}
/**
 * 
 * @author Steven Sinclair
 * @param customIn com.cboe.util.CustomInputStream
 */
public synchronized void recycleStream(CustomOutputStream customOut)
{
	for (int i=numStreamsAvailable; i < customOutPool.length; ++i)
	{
		if (customOutPool[i] == customOut)
		{
			if (i != numStreamsAvailable)
			{
				// Swap array value so that all available streams are in the first n array elements.
				//
				customOutPool[i] = customOutPool[numStreamsAvailable];
				customOutPool[numStreamsAvailable] = customOut;
			}
			++numStreamsAvailable;
			break;
		}
	}
}
}
