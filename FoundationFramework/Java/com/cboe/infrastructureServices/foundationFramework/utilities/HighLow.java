package com.cboe.infrastructureServices.foundationFramework.utilities;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * 
 * @author Dave Hoag
 */
public class HighLow
{
	public int high;
	public int low;
	protected int shiftSize;
	static long lowMask = 0x00000000FFFFFFFFl;
	static long highMask = 0xFFFFFFFF00000000l;
	/**
	 */
	public int getHigh()
	{
		return high;
	}
	/**
	 */
	public int getLow()
	{
		return low;
	}
	/**
	 * The likely case, convert  a long into two 32 bit ints
	 */
	public static HighLow create(long lValue)
	{
		HighLow result = new HighLow();
		result.shiftSize = 32;
		result.low = (int)(lValue & lowMask);
		result.high = (int)((lValue >> 32) & lowMask);
		return result;
	}
	/**
	 * @return long 
	 */
	public long getValue()
	{
		long result = high & lowMask;
		return ((result << shiftSize) & highMask) | ( low & lowMask);
	}
	/** 
	 * Unit Test.
	 */
	public static class UnitTest extends TestCase
	{
	    
	    public UnitTest(String name) {
			super(name);
		}
	    
		public void testBoth()
		{
			long one = 1l;
			HighLow impl = HighLow.create(one);
			assertEquals(one, impl.getValue());

			long negone = -1l;
			 impl = HighLow.create(negone);
			assertEquals(negone, impl.getValue());

			long zero = 0l;
			 impl = HighLow.create(zero);
			assertEquals(zero, impl.getValue());

			long highTest = 0x003FF00FF00l;
			 impl = HighLow.create(highTest);
			assertEquals(highTest, impl.getValue());
			assertEquals(0x003, impl.high);
			assertEquals(0x0FF00FF00, impl.low);
		}
		public static void main(String [] args)
		{
        	TestSuite suite = new TestSuite(UnitTest.class);
            TestRunner.run(suite);
		}
	}
}
