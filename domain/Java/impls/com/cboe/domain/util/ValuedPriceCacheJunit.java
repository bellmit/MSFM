package com.cboe.domain.util;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.cboe.idl.cmiConstants.PriceTypes;
import com.cboe.idl.cmiUtil.PriceStruct;

/**
 * Cmd line to run this junit test case:
 * 
 * java junit.swingui.TestRunner com.cboe.domain.util.ValuedPriceCacheJunit
 * or
 * java com.cboe.domain.util.ValuedPriceCacheJunit
 * 
 * For GUI version you might need to run the following command:
 * xhost +
 * in case the the junit framework cannot open the display terminal
 * 
 * @author singh
 *
 */

public class ValuedPriceCacheJunit extends TestCase
{
	private int maxDollarAmount = 0;
	
	public ValuedPriceCacheJunit(String testCase)
	{
		super(testCase);
	}

	protected void setUp() throws Exception
	{
		super.setUp();

		maxDollarAmount = ValuedPriceCache.getMaxDollarAmount();
	}

	private int getMaxDollarAmount()
	{
		return maxDollarAmount;
	}

	public void testValuedPriceCacheWithPriceStructs()
	{
		PriceStruct priceStruct = new PriceStruct();
		int maxValue = getMaxDollarAmount() + 1;

		for (int i = 0; i < maxValue; i++)
		{
			for (int j = 0; j < 100; j++)
			{
				priceStruct.whole = i;
				priceStruct.fraction = j * 10000000;

				ValuedPrice price = new ValuedPrice(priceStruct);

				assertEquals(price, ValuedPriceCache.lookup(priceStruct));

				priceStruct.whole = -priceStruct.whole;
				priceStruct.fraction = -priceStruct.fraction;

				price = new ValuedPrice(priceStruct);

				assertEquals(price, ValuedPriceCache.lookup(priceStruct));
			}
		}
	}

	public void testPriceStructs()
	{
		PriceStruct priceStruct = new PriceStruct();
		int maxValue = getMaxDollarAmount() + 1;
		PriceStruct rval;

		for (int i = 0; i < maxValue; i++)
		{
			priceStruct.type = PriceTypes.VALUED;
			
			for (int j = 0; j < 100; j++)
			{
				priceStruct.whole = i;
				priceStruct.fraction = j * 10000000;
				
				rval = ValuedPriceCache.lookupPriceStruct(priceStruct);
				
				assertTrue((rval.type == priceStruct.type) && (rval.fraction == priceStruct.fraction) &&
						(rval.whole == priceStruct.whole));

				priceStruct.whole = -priceStruct.whole;
				priceStruct.fraction = -priceStruct.fraction;

				rval = ValuedPriceCache.lookupPriceStruct(priceStruct);

				assertTrue((rval.type == priceStruct.type) && (rval.fraction == priceStruct.fraction) &&
						(rval.whole == priceStruct.whole));
			}
		}
	}
	
	public void testPriceFactoryWithPriceStructs()
	{
		PriceStruct priceStruct = new PriceStruct();
		int maxValue = getMaxDollarAmount() + 1;

		priceStruct.type = PriceTypes.VALUED;
		for (int i = 0; i < maxValue; i++)
		{
			for (int j = 0; j < 100; j++)
			{
				priceStruct.whole = i;
				priceStruct.fraction = j * 10000000;

				ValuedPrice price = new ValuedPrice(priceStruct);

				assertEquals(price, PriceFactory.create(priceStruct));

				priceStruct.whole = -priceStruct.whole;
				priceStruct.fraction = -priceStruct.fraction;

				price = new ValuedPrice(priceStruct);

				assertEquals(price, PriceFactory.create(priceStruct));
			}
		}
	}

	public void testPriceFactoryPositiveFractionTest()
	{
		PriceStruct priceStruct = new PriceStruct();

		priceStruct.type = PriceTypes.VALUED;
		priceStruct.whole = 1;
		priceStruct.fraction = 5000;

		ValuedPrice price = new ValuedPrice(priceStruct);

		assertEquals(price, PriceFactory.create(priceStruct));
	}

	public void testPriceFactoryNegativeFractionTest()
	{
		PriceStruct priceStruct = new PriceStruct();

		priceStruct.type = PriceTypes.VALUED;
		priceStruct.whole = -1;
		priceStruct.fraction = -5000;

		ValuedPrice price = new ValuedPrice(priceStruct);

		assertEquals(price, PriceFactory.create(priceStruct));
	}
	
	public void testPriceFactoryWithInvalidPricesInPriceStruct()
	{
		PriceStruct priceStruct = new PriceStruct();

		priceStruct.type = PriceTypes.VALUED;
		priceStruct.whole = -1;
		priceStruct.fraction = 5000;

		ValuedPrice price = new ValuedPrice(priceStruct);

		assertEquals(price, PriceFactory.create(priceStruct));
	}

	public void testPriceFactoryWithDoubleValues()
	{
		double maxValue = this.getMaxDollarAmount() * 100;

		for (double value = 0.0; value <= maxValue; value += 0.01)
		{
			ValuedPrice price = new ValuedPrice(value);

			assertEquals(price, PriceFactory.create(value));
			
			price = new ValuedPrice (-value);
			
			assertEquals(price, PriceFactory.create(-value));
		}
	}

	public void testPriceFactoryWithPositiveDoubleFractionalValue()
	{
		double value = 1.01625;

		ValuedPrice price = new ValuedPrice(value);

		assertEquals(price, PriceFactory.create(value));
	}

	public void testPriceFactoryWithNegativeDoubleFractionalValue()
	{
		double value = -1.01625;

		ValuedPrice price = new ValuedPrice(value);

		assertEquals(price, PriceFactory.create(value));
	}
	
	public void testPriceFactoryWithStringValues ()
	{
		int maxValue = getMaxDollarAmount() + 1;

		for (int i = 0; i < maxValue; i++)
		{
			for (int j = 0; j < 100; j++)
			{
				StringBuffer buf = new StringBuffer (40);
				
				buf.append(i).append(".").append(j);
				
				ValuedPrice price = new ValuedPrice(buf.toString());

				assertEquals(price, PriceFactory.create(buf.toString()));
				
				buf = new StringBuffer (20);
				
				buf.append(-i).append(".").append(j);
				
				price = new ValuedPrice(buf.toString());

				assertEquals(price, PriceFactory.create(buf.toString()));
			}
		}
	}

	public void testPriceFactoryWithNegativeStringFractionalValue()
	{
		String value = "-1.01625";

		ValuedPrice price = new ValuedPrice(value);

		assertEquals(price, PriceFactory.create(value));
	}
	
	public void testPriceFactoryWithPositiveStringFractionalValue()
	{
		String value = "1.01625";

		ValuedPrice price = new ValuedPrice(value);

		assertEquals(price, PriceFactory.create(value));
	}
	
	public void testPriceFactoryWithString_Positive_8ths()
	{
		String value = "1b";
		
		ValuedPrice price = new ValuedPrice(value);

		assertEquals(price, PriceFactory.create(value));
	}	

	public void testPriceFactoryWithString_Negative_8ths()
	{
		String value = "-1d";
		
		ValuedPrice price = new ValuedPrice(value);

		assertEquals(price, PriceFactory.create(value));
	}	
	
	public void testPriceFactoryWithString_Positive_16ths()
	{
		String value = "1 12+";
		
		ValuedPrice price = new ValuedPrice(value);

		assertEquals(price, PriceFactory.create(value));
	}	

	public void testPriceFactoryWithString_Negative_16ths()
	{
		String value = "-1 15+";
		
		ValuedPrice price = new ValuedPrice(value);

		assertEquals(price, PriceFactory.create(value));
	}	

	
	public static void main(String[] args)
	{
		TestSuite suite = new TestSuite();

		suite.addTestSuite(ValuedPriceCacheJunit.class);

		junit.textui.TestRunner.run(suite);
	}
}
