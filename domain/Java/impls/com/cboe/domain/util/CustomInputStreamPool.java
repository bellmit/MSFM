package com.cboe.domain.util;

import com.cboe.util.ReplaceableByteArrayInputStream;
import java.io.*;
import junit.framework.*;

/**
 *  Provide a static-sized pool of custom input streams having a replaceable
 *  byte array input stream as the underlying stream.  This allows message and
 *  field classes to cheaply access the various byte-level parsing abilities of
 *  the <code>CustomInputStream</code> class.
 *
 *  <p>Note that the <code>borrowStream()</code> method is guaranteed to always
 *  return an appropriate <code>CustomInputStream</code>, and that only borrowed
 *  streams will be reinserted into the pool.  Further note that the recycleStream
 *  method never reports any failure: if a stream cannot be recycled, then it is
 *  quietly ignored.
 */
public class CustomInputStreamPool
{
	/**
	 *   The size of the pool which will be returned by calling 
	 *   <code>getInstance()</code>.
	 */
	public static final int DEFAULT_POOL_SIZE = 4;
	
	private static CustomInputStreamPool staticInstance;
	private int numStreamsAvailable;
	private final byte[] emptyByteArray = new byte[0];
	private ReplaceableByteArrayInputStream[] bytesInPool = null;
	private CustomInputStream[] customInPool = null;

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
			CustomInputStreamPool pool = new CustomInputStreamPool(3);	// max. of 3 streams in the pool.
			
			CustomInputStream[] streams = { null, null, null };

			assertTrue("Expected 3 streams to be initially available", pool.numStreamsAvailable == 3);
			
			streams[0] = pool.borrowStream(new byte[12]);
			assertTrue("Borrowed stream was null!", streams[0] != null);
			assertTrue("Expected 2 streams to be available.", pool.numStreamsAvailable == 2);
			streams[1] = pool.borrowStream(new byte[12]);
			assertTrue("Borrowed stream was null!", streams[1] != null);
			assertTrue("Expected 1 streams to be available.", pool.numStreamsAvailable == 1);

			pool.recycleStream(streams[0]);
			assertTrue("Expected 2 stream to be available.", pool.numStreamsAvailable == 2);
			pool.recycleStream(streams[1]);
			assertTrue("Expected 3 streams to be available.", pool.numStreamsAvailable == 3);
			streams[2] = pool.borrowStream(new byte[12]);
			assertTrue("Borrowed stream was null!", streams[2] != null);
			assertTrue("Expected 2 streams to be available.", pool.numStreamsAvailable == 2);

			pool.recycleStream(new CustomInputStream(new PipedInputStream()));
			assertTrue("Expected 2 stream to be available (recycled unknown stream)", pool.numStreamsAvailable == 2);

			pool.recycleStream(streams[2]);
			assertTrue("Expected 3 streams to be available.", pool.numStreamsAvailable == 3);

			CustomInputStream another = pool.borrowStream(new byte[12]);
			pool.recycleStream(another); 
			assertTrue("Expected 3 streams to be available, not " + pool.numStreamsAvailable, pool.numStreamsAvailable == 3);
		}
		
		public Test(String methodName)
		{
			super(methodName);
		}
		public static void main(String args[])
		{
			System.out.println("Unit testing CustomInputStreamPool");
			junit.textui.TestRunner.run(suite());
		}
	}
/**
 * Create a pool of the given size.
 */
public CustomInputStreamPool(int poolSize)
{
	bytesInPool  = new ReplaceableByteArrayInputStream[poolSize];
	customInPool = new CustomInputStream[poolSize];

	for (int i=0; i < customInPool.length; ++i)
	{
		bytesInPool[i] = new ReplaceableByteArrayInputStream(emptyByteArray);
		customInPool[i] = new CustomInputStream(bytesInPool[i]);
	}
	numStreamsAvailable = poolSize;
}
/**
 * 
 * @author Steven Sinclair
 * @return com.cboe.util.CustomInputStream
 * @param bytes byte[]
 * @see #borrowStream(bytes, int, int)
 */
public CustomInputStream borrowStream(byte[] bytes)
{
	return borrowStream(bytes, 0, bytes.length);
}
/**
 * 
 * @author Steven Sinclair
 * @return com.cboe.util.CustomInputStream
 * @param bytes byte[]
 * @param offset int
 * @see #borrowStream(bytes, int, int)
 */
public CustomInputStream borrowStream(byte[] bytes, int offset)
{
	return borrowStream(bytes, offset, bytes.length-offset);
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
public synchronized CustomInputStream borrowStream(byte[] bytes, int offset, int len)
{
	if (numStreamsAvailable == 0)
		return new CustomInputStream(new ReplaceableByteArrayInputStream(bytes, offset, len));
	
	CustomInputStream customIn = customInPool[--numStreamsAvailable];
	ReplaceableByteArrayInputStream bytesIn  = bytesInPool[numStreamsAvailable];
	bytesIn.setBytes(bytes, offset, len);
	return customIn;
}
/**
 * 
 * @author Steven Sinclair
 * @return com.cboe.externalIntegrationServices.tipsAdapter.message.CustomInputStreamPool
 */
public static CustomInputStreamPool getInstance()
{
	if (staticInstance == null)
	{
		synchronized(CustomInputStreamPool.class)
		{
			if (staticInstance == null)
			{
				staticInstance = new CustomInputStreamPool(DEFAULT_POOL_SIZE);
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
public synchronized void recycleStream(CustomInputStream customIn)
{
	for (int i=numStreamsAvailable; i < customInPool.length; ++i)
	{
		if (customInPool[i] == customIn)
		{
			if (i != numStreamsAvailable)
			{
				// Swap array value so that all available streams are in the first n array elements.
				//
				ReplaceableByteArrayInputStream bytesIn = bytesInPool[i];
				bytesIn.setBytes(emptyByteArray, 0, 0);
				customInPool[i] = customInPool[numStreamsAvailable];
				bytesInPool[i] = bytesInPool[numStreamsAvailable];
				customInPool[numStreamsAvailable] = customIn;
				bytesInPool[numStreamsAvailable] = bytesIn;
			}
			++numStreamsAvailable;
			break;
		}
	}
}
}
