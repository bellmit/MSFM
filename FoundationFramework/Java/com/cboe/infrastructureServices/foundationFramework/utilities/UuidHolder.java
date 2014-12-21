package com.cboe.infrastructureServices.foundationFramework.utilities;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * A helper class that will facilitate working with UUIDs of long values.
 * This class is not thread safe. Any changes or updates to an instance of this class should
 * be evaluated for thread safety.
 * @author Dave Hoag
 * @version 1.0
 */
public class UuidHolder
{
	long longValue; //The long version of the primary and secondary bytes.
	int primaryBytes; //high order bytes
	int secondaryBytes; //low order bytes
	/**
	 * The default constructor will be empty. 
	 * Use of the 'set' methods is expected.
	 */
	public UuidHolder()
	{
	}
	/**
	 * If you already know the long value, use this constructor.
	 */
	public UuidHolder(long uuid)
	{
		setLongValue(uuid);
	}
	/**
	 * If the primary and secondary bytes are known, use this constructor.
	 */
	public UuidHolder(int primary, int secondary)
	{
		primaryBytes = primary;
		secondaryBytes = secondary;
		initLongValue(primaryBytes, secondaryBytes);
	}
	/**
	 * Update the 'secondary' bytes to the new value.
	 * @param lowBytes An integer representing the low order bytes.
	 */
	public void setSecondaryBytes(int lowBytes)
	{
		secondaryBytes = lowBytes;
		initLongValue(primaryBytes, secondaryBytes);
	}
	/**
	 * Update the 'primary' bytes to the new value.
	 * @param highBytes An integer representing the high order bytes.
	 */
	public void setPrimaryBytes(int highBytes)
	{
		primaryBytes = highBytes;
		initLongValue(primaryBytes, secondaryBytes);
	}
	/**
	 * Create a new long value from the provided parameters.
	 * 
	 * @param highBytes An integer representing the high order bytes.
	 * @param lowBytes An integer representing the low order bytes.
	 */
	protected void initLongValue(int highBytes, int lowBytes)
	{
		long lowSeed = lowBytes;
		long result = highBytes;
		result =  ((result << 32) & 0xFFFFFFFF00000000l) | ( lowSeed & 0x00000000FFFFFFFFl);
		longValue = result;
	}
	/**
	 * Change the long value for this holder to be the provided value.
	 * This will have the side effect of updating the Primary and Secondary bytes.
	 * 
	 * @param uuid The new long value.
	 */
	public void setLongValue(long uuid)
	{
		longValue = uuid;
		primaryBytes = (int)((uuid >> 32) & 0x00000000FFFFFFFFl);
		secondaryBytes = (int)(uuid & 0x00000000FFFFFFFFl);
	}
	/**
	 * Get the long version of the primary and secondary bytes.
	 * @return long Value held by this holder.
	 */
	public long getLongValue()
	{
		return longValue;
	}
	/**
	 * @return int The high order bytes of the long value.
	 */
	public int getPrimaryBytes()
	{
		return primaryBytes;
	}
	/**
	 * @return int The low order bytes of the long value.
	 */
	public int getSecondaryBytes()
	{
		return secondaryBytes;
	}
	/**
	 * Exercise some of the methods with a unit test.
	 */
	public static class UnitTest extends TestCase
	{
		long [] longsToTest = { 0x0F0F0F0F01010101l, 0xFF00FF00FF00FF00l };
		int [] expected = { 0x0F0F0F0F, 0x01010101, 0xFF00FF00, 0xFF00FF00 };
		/** */
	    
	    public UnitTest(String name) {
			super(name);
		}
	    
		public void testConversions()
		{
			for(int i = 0; i < longsToTest.length; ++i)
			{
				long lValue = longsToTest[i];
				int high = (i * 2)  ;
				int low  = (i * 2 ) + 1;
				high = expected[high];
				low = expected [low];
				UuidHolder holder = new UuidHolder(lValue);
				assertEquals("High bytes", high, holder.getPrimaryBytes());
				assertEquals("Low bytes", low, holder.getSecondaryBytes());

				holder = new UuidHolder(high, low);
				assertEquals("Long value", lValue, holder.getLongValue());

				holder.setSecondaryBytes(low);
				assertEquals("Long value after low set", lValue, holder.getLongValue());

				holder.setPrimaryBytes(high);
				assertEquals("Long value after high set", lValue, holder.getLongValue());

				holder.setLongValue(0x01l);
				assertEquals("New high", 0, holder.getPrimaryBytes());
				assertEquals("New low", 1, holder.getSecondaryBytes());
			} 
		}
		public static void main(String [] args)
		{
        	TestSuite suite = new TestSuite(UnitTest.class);
            TestRunner.run(suite);
		}
	}
}
