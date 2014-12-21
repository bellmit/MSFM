package com.cboe.domain.marketData;

import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.domain.marketData.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.systemsManagementService.*;
import com.cboe.infrastructureServices.persistenceService.*;
import com.cboe.infrastructureServices.foundationFramework.transactionManagement.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;
import com.cboe.idl.cmiMarketData.*;
import com.cboe.idl.marketData.InternalTickerStruct;
import com.cboe.idl.marketData.InternalTickerDetailStruct;
import com.cboe.idl.cmiProduct.*;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiConstants.*;
import com.cboe.idl.trade.*;
import com.cboe.domain.util.*;
import com.cboe.util.*;
import junit.framework.*;
/**
 * A unit test for MarketDataHistoryHomeImpl and MarketDataHomeImpl.
 *
 * @author John Wickberg
 */
public class UnitTestHomes extends TestCase
{
    private static final String TEST_SESSION = "TestSession";
	private static boolean setupComplete;
	private static MarketDataHomeImpl marketDataHome;
	private static MarketDataHistoryHomeImpl marketDataHistoryHome;
	private static BOSession session;
	private static boolean testCompleted;
	private static long beforeFirst;
	private static long afterLast;
/**
 * UnitTestHomes constructor comment.
 * @param name java.lang.String
 */
public UnitTestHomes(String name) {
	super(name);
}
/**
 * Verifies that all struct conversions can be performed on test result.
 *
 * @author John Wickberg
 */
public void assertConversions(MarketData testResult)
{
	//assertTrue("Contingent Market Best Struct", testResult.toCurrentMarketStructs() != null);
    
    assertTrue("Contingent Market Best Struct", 
            testResult.getBestMarket() != null ||
            testResult.getBestLimitMarket() !=null ||
            testResult.getBestPublicMarket() !=null );
    
	assertTrue("NBBO Struct", testResult.toNBBOStruct() != null);
	assertTrue("Recap", testResult.toRecapStruct() != null);
}
/**
 * Initializes foundation framework.
 *
 * @author John Wickberg
 */
private static void initialize()
{
	try
	{
		FoundationFramework ff = FoundationFramework.getInstance();
		ConfigurationService configService = new ConfigurationServiceFileImpl();
		String[] args = {"MarketDataUnitTest.properties"};
		configService.initialize(args, 0);
		ff.initialize("MarketDataServer", configService);
	}
	catch (Exception e)
	{
		e.printStackTrace();
		System.exit(1);
	}
	catch (Throwable t)
	{
		t.printStackTrace();
		System.exit(1);
	}
}
/**
 * Runs the unit test.
 */
public static void main(String args[])
{
	initialize();
	String[] testArgs = {UnitTestHomes.class.getName()};
	junit.textui.TestRunner.main(testArgs);
}
/**
 * Initializes homes used in testing.
 *
 * @author John Wickberg
 */
public void setUp()
{
	if (!setupComplete)
	{
		setupComplete = true;
		try
		{
			marketDataHome = (MarketDataHomeImpl) HomeFactory.getInstance().findHome(MarketDataHome.HOME_NAME);
			marketDataHistoryHome = (MarketDataHistoryHomeImpl) HomeFactory.getInstance().findHome(MarketDataHistoryHome.HOME_NAME);
		}
		catch (Exception e)
		{
			System.out.println("Unable to find homes: " + e);
			System.exit(1);
		}
	}
	session = BOSessionManager.getDefaultManager().getSession();
	if (session == null)
	{
		session = BOSession.createAndJoin("Unit test");
	}
	session.startTransaction();
	testCompleted = false;
}
/**
 * Creates suite of tests.
 *
 * @author John Wickberg
 */
public static Test suite()
{
	TestSuite suite = new TestSuite();
	suite.addTest(new UnitTestHomes("testCreateMarketData"));
	suite.addTest(new UnitTestHomes("testFindMarketData"));
	suite.addTest(new UnitTestHomes("testCreateMarketHistory"));
	suite.addTest(new UnitTestHomes("testFindMarketHistory"));
	return suite;
}
/**
 * Commit or rollback changes at end of test.
 *
 * @author John Wickberg
 */
public void tearDown()
{
	if (testCompleted)
	{
		try
		{
			session.commit();
		}
		catch (UpdateException e)
		{
			session.rollback();
			Log.exception(e);
		}
	}
	else
	{
		session.rollback();
	}
}
/**
 * Create market data and try all to structs.
 */
public void testCreateMarketData() throws TransactionFailedException, AlreadyExistsException
{
	MarketData result = marketDataHome.create(TEST_SESSION, 1, 1);
	assertConversions(result);
	// since everything seemed to work, create a second.
	marketDataHome.create(TEST_SESSION, 2, 1);
	testCompleted = true;
}
/**
 * Creates market data history entries.
 */
public void testCreateMarketHistory()
{
	beforeFirst = System.currentTimeMillis();
	int maxSize = marketDataHistoryHome.maxResultSize();
	//InternalTickerStruct internalTicker = new InternalTickerStruct();
    InternalTickerDetailStruct internalTicker = new InternalTickerDetailStruct();
	internalTicker.lastSaleTicker.ticker = new TickerStruct();
    internalTicker.lastSaleTicker.ticker.productKeys = new ProductKeysStruct();
	CurrentMarketStruct currentMarket = new CurrentMarketStruct();
	currentMarket.productKeys = new ProductKeysStruct();
	currentMarket.productKeys.productKey = 1;
	currentMarket.bidSizeSequence = new MarketVolumeStruct[1];
	currentMarket.bidSizeSequence[0] = new MarketVolumeStruct();
	currentMarket.bidSizeSequence[0].volumeType = VolumeTypes.LIMIT;
	currentMarket.bidSizeSequence[0].quantity = 10;
	currentMarket.askSizeSequence = new MarketVolumeStruct[1];
	currentMarket.askSizeSequence[0] = new MarketVolumeStruct();
	currentMarket.askSizeSequence[0].volumeType = VolumeTypes.LIMIT;
	currentMarket.askSizeSequence[0].quantity = 10;
    NBBOStruct nbboStruct = new  NBBOStruct();
    NBBOStruct botrStruct = new  NBBOStruct();
    ExchangeIndicatorStruct[] exchangeIndicatorStruct = new ExchangeIndicatorStruct[0];
    MarketData marketData  = null;
    long entryTime =  FoundationFramework.getInstance().getTimeService().getCurrentDateTime();


	for (int i = 0; i < maxSize; i++)
	{
		if (i % 3 == 0)
		{

            internalTicker.lastSaleTicker.tradeTime = DateWrapper.convertToTime(System.currentTimeMillis());
            internalTicker.lastSaleTicker.ticker.productKeys.productKey = 1;
            internalTicker.lastSaleTicker.ticker.lastSalePrice = PriceFactory.create(2.0 + (double) i / 8.0).toStruct();
            internalTicker.lastSaleTicker.ticker.lastSaleVolume = 10;
			marketDataHistoryHome.createLastSaleEntry(internalTicker.lastSaleTicker.tradeTime,internalTicker,new PriceSqlType((double) i), ProductStates.OPEN, entryTime);
		}
		else
		{
			currentMarket.sentTime = new DateWrapper().toTimeStruct();
			currentMarket.bidPrice = PriceFactory.create(2.0 + (double) i / 8.0).toStruct();
			currentMarket.askPrice = PriceFactory.create(2.0 + (double) (i  + 2) / 8.0).toStruct();
			marketDataHistoryHome.createCurrentMarketEntry(currentMarket, currentMarket,currentMarket, nbboStruct,botrStruct,exchangeIndicatorStruct,new PriceSqlType((double) i), ProductStates.OPEN ,entryTime, "");
		}
		try
		{
			Thread.sleep(100);
		}
		catch (InterruptedException e)
		{
		}
	}
	afterLast = System.currentTimeMillis();
	testCompleted = true;
}
/**
 * Tests find of market data by product key and class key.
 *
 * @author John Wickberg
 */
public void testFindMarketData() throws NotFoundException
{
	MarketDataImpl result = (MarketDataImpl) marketDataHome.findByProduct(TEST_SESSION, 1);
	assertTrue("Product Key", result.getProductKey() == 1);
	assertConversions(result);
	MarketData[] classResult = marketDataHome.findByClass(TEST_SESSION, 1);
	assertTrue("Class Query", classResult.length == 2);
	assertConversions(classResult[0]);
	assertConversions(classResult[1]);
	testCompleted = true;
}
/**
 * Tests queries for market data history.
 */
public void testFindMarketHistory() throws NotFoundException
{
	MarketDataHistoryEntry[] result;
	result = marketDataHistoryHome.findByTime(1, beforeFirst, QueryDirections.QUERY_BACKWARD);
	assertTrue("Empty result", result.length == 0);
	result = marketDataHistoryHome.findByTime(1, System.currentTimeMillis(), QueryDirections.QUERY_BACKWARD);
	assertTrue("Full result", result.length == marketDataHistoryHome.maxResultSize());
	for (int i = 0; i < result.length; i++)
	{
		assertTrue("Convert result#" + i, result[i].toStruct() != null);
	}
	testCompleted = true;
}
}
