package com.cboe.domain.marketData;

import junit.framework.*;
import com.cboe.idl.cmiMarketData.*;
import com.cboe.idl.trade.*;
import com.cboe.domain.util.*;
import com.cboe.interfaces.domain.Price;
import com.cboe.util.*;

/**
 * Unit test for LastSaleSummaryImpl.
 *
 * @author John Wickberg
 */
public class UnitTestRecap extends TestCase
{
	/**
	 * Last sale instance used for testing.
	 */
	private static RecapImpl testRecap;
/**
 * UnitTestLastSale constructor comment.
 */
public UnitTestRecap(String testName)
{
	super(testName);
}
/**
 * Creates test object on first test.
 *
 * @author John Wickberg
 */
public void setUp()
{
	if (getName().equals("testFirstUpdate"))
	{
		testRecap = new RecapImpl();
	}
}
/**
 * Creates suite of tests.
 */
public static Test suite()
{
	TestSuite suite = new TestSuite();
	suite.addTest(new UnitTestRecap("testFirstUpdate"));
	suite.addTest(new UnitTestRecap("testNewLow"));
	suite.addTest(new UnitTestRecap("testNewHigh"));
	return suite;
}
/**
 * Tests first time last sale summary is updated.
 *
 * @author John Wickberg
 */
public void testFirstUpdate()
{
	TickerStruct lastSale = new TickerStruct();
	Price lastSalePrice = PriceFactory.create(5.0);
	lastSale.lastSalePrice = lastSalePrice.toStruct();
	lastSale.lastSaleVolume = 10;
	testRecap.update(lastSale);
	assertEquals("Last Sale Price", lastSalePrice, testRecap.getLastSalePrice());
	assertEquals("Last Sale Quantity", lastSale.lastSaleVolume, testRecap.getLastSaleVolume());
	assertEquals("Total Volume", lastSale.lastSaleVolume, testRecap.getTotalVolume());
	assertEquals("High Price", lastSalePrice, testRecap.getHighPrice());
	assertEquals("Low Price", lastSalePrice, testRecap.getLowPrice());
	assertEquals("Open Price", lastSalePrice, testRecap.getOpenPrice());
	assertEquals("Close Price", lastSalePrice, testRecap.getClosePrice());
	assertEquals("Tick amount", testRecap.getTickAmount(), PriceFactory.create(0));
	assertEquals("Net Change Amount", testRecap.getNetChange(), PriceFactory.create(0));
}
/**
 * Tests an update that sets a new low.
 *
 * @author John Wickberg
 */
public void testNewHigh()
{
	TickerStruct lastSale = new TickerStruct();
	Price lastSalePrice= PriceFactory.create(5.5);
	Price lowPrice = testRecap.getLowPrice();
	lastSale.lastSalePrice = lastSalePrice.toStruct();
	lastSale.lastSaleVolume = 10;
	testRecap.update(lastSale);
	assertEquals("Last Sale Price", lastSalePrice, testRecap.getLastSalePrice());
	assertEquals("Last Sale Quantity", lastSale.lastSaleVolume, testRecap.getLastSaleVolume());
	assertEquals("High Price", lastSalePrice, testRecap.getHighPrice());
	assertEquals("Low Price", lowPrice, testRecap.getLowPrice());
	assertEquals("Total Volume", 30, testRecap.getTotalVolume());
	assertEquals("Tick amount", testRecap.getTickAmount(), PriceFactory.create(1.0));
	assertEquals("Net Change Amount", testRecap.getNetChange(), PriceFactory.create(0.5));
}
/**
 * Tests an update that sets a new low.
 *
 * @author John Wickberg
 */
public void testNewLow()
{
	TickerStruct lastSale = new TickerStruct();
	Price lastSalePrice= PriceFactory.create(4.5);
	Price highPrice = testRecap.getHighPrice();
	lastSale.lastSalePrice = lastSalePrice.toStruct();
	lastSale.lastSaleVolume = 10;
	testRecap.update(lastSale);
	assertEquals("Last Sale Price", lastSalePrice, testRecap.getLastSalePrice());
	assertEquals("Last Sale Quantity", lastSale.lastSaleVolume, testRecap.getLastSaleVolume());
	assertEquals("High Price", highPrice, testRecap.getHighPrice());
	assertEquals("Low Price", lastSalePrice, testRecap.getLowPrice());
	assertEquals("Total Volume", 20, testRecap.getTotalVolume());
	assertEquals("Tick amount", testRecap.getTickAmount(), PriceFactory.create(-0.5));
	assertEquals("Net Change Amount", testRecap.getNetChange(), PriceFactory.create(-0.5));
}
}
