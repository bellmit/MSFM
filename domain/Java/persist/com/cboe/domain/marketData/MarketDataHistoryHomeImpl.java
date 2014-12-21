package com.cboe.domain.marketData;

import java.util.Calendar;
import java.util.Enumeration;
import java.util.Vector;

import com.cboe.domain.marketData.mdhQueueEntry.MDHCurrentMarketEntry;
import com.cboe.domain.marketData.mdhQueueEntry.MDHEOPEntry;
import com.cboe.domain.marketData.mdhQueueEntry.MDHEntriesStruct;
import com.cboe.domain.marketData.mdhQueueEntry.MDHEntryStruct;
import com.cboe.domain.marketData.mdhQueueEntry.MDHLastSaleEntry;
import com.cboe.domain.marketData.mdhQueueEntry.MDHProductStateChangeEntry;
import com.cboe.domain.marketData.mdhQueueEntry.MDHQueueEntry;
import com.cboe.domain.util.DateWrapper;
import com.cboe.domain.util.IntProperty;
import com.cboe.domain.util.MarketDataHelper;
import com.cboe.domain.util.PriceSqlType;
import com.cboe.domain.util.StringProperty;
import com.cboe.exceptions.NotFoundException;
import com.cboe.idl.cmiConstants.CurrentMarketViewTypes;
import com.cboe.idl.cmiConstants.MarketDataHistoryEntryTypes;
import com.cboe.idl.cmiConstants.QueryDirections;
import com.cboe.idl.cmiMarketData.CurrentMarketStruct;
import com.cboe.idl.cmiMarketData.ExchangeIndicatorStruct;
import com.cboe.idl.cmiMarketData.ExpectedOpeningPriceStruct;
import com.cboe.idl.cmiMarketData.NBBOStruct;
import com.cboe.idl.cmiMarketData.TickerStruct;
import com.cboe.idl.cmiUtil.TimeStruct;
import com.cboe.idl.internalBusinessServices.MarketDataHistoryEntriesStruct;
import com.cboe.idl.marketData.InternalTickerDetailStruct;
import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.FatalFoundationFrameworkException;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.persistenceService.ObjectQuery;
import com.cboe.infrastructureServices.persistenceService.PersistenceException;
import com.cboe.infrastructureServices.systemsManagementService.ApplicationPropertyHelper;
import com.cboe.infrastructureServices.systemsManagementService.NoSuchPropertyException;
import com.cboe.interfaces.domain.HistoryServiceIdGenerator;
import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.domain.marketData.MarketDataHistoryEntry;
import com.cboe.interfaces.domain.marketData.MarketDataHistoryHome;
import com.cboe.interfaces.domain.product.ProductClassHome;
import com.cboe.server.dependencyFramework.Homes;
import com.cboe.server.queue.ServerQueue;
import com.cboe.server.queue.ServerQueueException;
import com.cboe.server.queue.ServerQueueHome;
import com.cboe.server.util.TradeServerIdPropertyHelper;
import com.cboe.util.ExceptionBuilder;
import com.objectwave.persist.constraints.ConstraintCompare;
import com.cboe.interfaces.domain.MarketUpdate;

/**
 * A home for persistent market data history.
 * 
 * Rewritten to always use queue and market data history service. - Dalji 3/8/06
 *
 * @author John Wickberg
 * @author Daljinder Singh 
 */
public class MarketDataHistoryHomeImpl extends BOHome implements MarketDataHistoryHome
{
	/**
	 * Property used to control maximum result set size for queries.
	 */
	public static final String MAX_RESULT_SIZE = "maxResultSize";

    /**
     * Default result size.
     */
    public static final String DEFAULT_RESULT_SIZE = "20";

	public static final String MAX_QUEUE_DEPTH = "maxQueueDepth";

	public static final String DEFAULT_QUEUE_DEPTH = "0";

    private static final String PROP_NAME_lastPrefixSearchDurationInSeconds = "lastPrefixSearchDurationInSeconds";

/**
	 * Property used to control commit batch size.
	 */
	public static final String MARKET_DATA_HISTORY_BATCH_SIZE = "marketDataHistoryPersistenceBatchSize";

	/**
	 * Property used to control commit batch size.
	 */
	public static final String MARKET_DATA_HISTORY_BATCH_TIME = "marketDataHistoryBatchTime";

	/*
	 * Number of messages to dequeue from the queue
	 */
	public static final String MARKET_DATA_HISTORY_DEQUEUE_BATCH_SIZE = "marketDataHistoryDequeueBatchSize";

	/**
	 * Property used to control thread pool size.
	 */
	public static final String NUMBER_MARKET_DATA_HISTORY_PROCESSING_THREADS = "numberProcessingThreads";

	/**
	 * Default number of MarketDataHistoryProcessingThreads
	 */
	public static final String DEFAULT_NUMBER_MARKET_DATA_PROCESSING_THREADS = "10";

	private static final String ID_GENERATION_STRATEGY = "idgenerationstrategy";

	private static final String DB_ID_BLOCK_SIZE = "dbIdBlockSize";

	private static final String DEFAULT_DB_ID_BLOCK_SIZE = "-1";

	public String myClassName = "MarketDataHistoryHomeQueueImpl";

    private static byte serverInstanceNumber;
    
    static
    {
        initTradeServerId();
    }
    
    private static void initTradeServerId()
    {
        try
        {
            String serverInstanceNumberStr = ApplicationPropertyHelper.getProperty("serverInstanceNumber");
            String sessionList = ApplicationPropertyHelper.getProperty("sessionNames");
            String tradeServerIdStr = TradeServerIdPropertyHelper.getTradeServerId(serverInstanceNumberStr, sessionList);
            serverInstanceNumber = (byte) Integer.parseInt(tradeServerIdStr);
            Log.information("MarketDataHistoryHomeImpl: trade server id set to " + serverInstanceNumber);
        }
        catch (NoSuchPropertyException e)
        {
            Log.information("MarketDataHistoryHomeImpl:  Unable to determine trade server id value from defined properties. Using 1 as default value");
            serverInstanceNumber = 1;
        }
        catch (NumberFormatException nfe)
        {
            Log.alarm("MarketDataHistoryHomeImpl: Unable to determine trade server id value. Invalid value defined for serverInstanceNumber. Using 1 as default value");
            serverInstanceNumber = 1;
        }
    }
            
	/*
	 * Properties
	 */
	private IntProperty numberOfThreadsProperty = new IntProperty(this, NUMBER_MARKET_DATA_HISTORY_PROCESSING_THREADS);

	private IntProperty marketDataHistoryDequeueBatchSizeProperty = new IntProperty(this, MARKET_DATA_HISTORY_DEQUEUE_BATCH_SIZE);

	/**
	 * Number of events that should be read before a commit is done.
	 */
	private IntProperty marketDataHistoryBatchSizeProperty = new IntProperty(this, MARKET_DATA_HISTORY_BATCH_SIZE);

	private IntProperty marketDataHistoryBatchTimeProperty = new IntProperty(this, MARKET_DATA_HISTORY_BATCH_TIME);

	private IntProperty maxQueueDepthProperty = new IntProperty(this, MAX_QUEUE_DEPTH, DEFAULT_QUEUE_DEPTH);

	private IntProperty lastPrefixSearchDurationInSecondsProperty = new IntProperty(this,
			PROP_NAME_lastPrefixSearchDurationInSeconds);

	private IntProperty dbIdBlockSize = new IntProperty(this, DB_ID_BLOCK_SIZE, DEFAULT_DB_ID_BLOCK_SIZE);

	private StringProperty idGenerationStrategyProperty = new StringProperty(this, ID_GENERATION_STRATEGY);

	/**
	 * Result size being used.
	 */
	private Integer maxResultSize;

	/**
	 * threads: index -- thread number. value:
	 * MarketDataHistoryProcessingThread.
	 */
	public MarketDataHistoryProcessingThread[] threads;

	public int getNumberOfThreads()
	{
		return numberOfThreadsProperty.get();
	}

	public int getMarketDataHistoryDequeueBatchSize()
	{
		return marketDataHistoryDequeueBatchSizeProperty.get();
	}

	public int getMarketDataHistoryBatchSize()
	{
		return marketDataHistoryBatchSizeProperty.get();
	}

	public int getMarketDataHistoryBatchTime()
	{
		return marketDataHistoryBatchTimeProperty.get();
	}

	public int getMaxQueueDepthSize()
	{
		return maxQueueDepthProperty.get();
	}

	public int getLastPrefixSearchDurationInSeconds()
	{
		return lastPrefixSearchDurationInSecondsProperty.get();
	}

	/**
	 * Called when the server is going to a 'slave' state. Default behavior is a
	 * no-op
	 */
	public void goSlave()
	{
		super.goSlave();

		// create queues but don't start threads yet. Start threads in goMaster
		initQueues();
	}

	private ServerQueue<MDHQueueEntry> getQueueObject(int i) throws ServerQueueException
	{
		int numberOfThreads = getNumberOfThreads();
		String queueName = "MarketDataHistoryQueue_" + i;
		
        ServerQueueHome serverQueueHome = Homes.getInstance().get(ServerQueueHome.class);
        ServerQueue<MDHQueueEntry> serverQueue; 
        serverQueue = serverQueueHome.find(MDHQueueEntry.class, queueName);

        int maxQueueDepth = getMaxQueueDepthSize();
        if( serverQueue == null ){  
            if (maxQueueDepth > 0)
            {
                serverQueue = serverQueueHome.createTransient(MDHQueueEntry.class, queueName, maxQueueDepth, true);
            }
            else
            {
                serverQueue = serverQueueHome.createTransient(MDHQueueEntry.class, queueName, false);
                
            }
        }
		return serverQueue;
	}

	private void startQueueReaders()
	{
		int numberOfThreads = getNumberOfThreads();

		for (int i = 0; i < numberOfThreads; i++)
		{
			try
			{
				threads[i].start();

				Log.information(this, "MarketDataHistoryProcessingThread " + threads[i].getName()
						+ " started to process market data events");
			}
			catch (Exception e)
			{
				Log.exception(this, "MarketDataProcessingThread : Error starting thread. FIX PROBLEM and RESTART SERVER", e);
			}
		}
	}

	private void initQueues()
	{
		if (threads != null)
		{
			return;
		}

		int numberOfThreads = getNumberOfThreads();

		threads = new MarketDataHistoryProcessingThread[numberOfThreads];

		ServerQueue sQueue;

		for (int i = 0; i < numberOfThreads; i++)
		{
			try
			{
				sQueue = getQueueObject(i);

				threads[i] = initMarketDataHistoryProcessingThread(i, sQueue);
			}
			catch (Exception e)
			{
				Log.exception(this, "MarketDataProcessingThread : Error creating queues. FIX PROBLEM AND RESTART.", e);
			}
		}
	}

	private MarketDataHistoryProcessingThread initMarketDataHistoryProcessingThread(int i, ServerQueue squeue)
	{
		MarketDataHistoryProcessingThread thread = new MarketDataHistoryProcessingThread(this, i, squeue,
				getMarketDataHistoryBatchTime(), getMarketDataHistoryDequeueBatchSize(), getMarketDataHistoryBatchSize(),
				getDbIdBlockSize());


		Log.information(this, "MarketDataHistoryProcessingThread " + thread.getName()
				+ " instantiated to process market data events");

		return thread;
	}

	public void goMaster(boolean failover)
	{
		super.goMaster(failover);

		startQueueReaders();
	}

	/**
 * MarketDataHistoryHomeImpl constructor comment.
 */
	public MarketDataHistoryHomeImpl()
	{
		setSmaType("GlobalMarketDataHistory.MarketDataHistoryHomeImpl");
}

/**
* Initialize the properties
*/
public void initialize()
{
		try
		{
			getNumberOfThreads();

			getMarketDataHistoryDequeueBatchSize();

			getMarketDataHistoryBatchSize();

			getMarketDataHistoryBatchTime();

			getMaxQueueDepthSize();

			getLastPrefixSearchDurationInSeconds();

			getDbIdBlockSize();

			setMarketDataHistoryIdGenerationStrategy(getIdGenerationStrategy());
		}
		catch (Exception e)
		{
			Log.alarm(this, "Failed to get properties");
			Log.exception(e);
			throw new FatalFoundationFrameworkException("Failed to get property");
		}
	}

	private int getDbIdBlockSize()
	{
		return this.dbIdBlockSize.get();
	}

	private void setMarketDataHistoryIdGenerationStrategy(String idGenerationStrategy2) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException
	{
		Class clasz = Class.forName(idGenerationStrategy2);

		HistoryServiceIdGenerator impl = (HistoryServiceIdGenerator) clasz.newInstance();

		MarketDataHistoryObjectPool.setIdGeneratorStrategy(impl);
}

/**
 * Creates history entry from a market update.
 *
	 * @param bestMarket
	 *            market best information
	 * @param underlyingPrice
	 *            price of underlying at time of last sale
 * @return created history entry
 */
	public void createCurrentMarketEntry(CurrentMarketStruct bestMarket, CurrentMarketStruct bestLimitMarket,
			CurrentMarketStruct bestPublicMarket, NBBOStruct nbboStruct, NBBOStruct botrStruct,
			ExchangeIndicatorStruct[] exchangeIndicatorStruct, Price underlyingPrice, short productState, long entryTime,
			String location)
	{
		MDHCurrentMarketEntry entry = new MDHCurrentMarketEntry(bestMarket, bestLimitMarket, bestPublicMarket, nbboStruct,
				botrStruct, exchangeIndicatorStruct, underlyingPrice, productState, entryTime, location);

		enqueue(entry);
	}
	
	public void createCurrentMarketEntry(MarketUpdate update, NBBOStruct nbboStruct, NBBOStruct botrStruct,
	        ExchangeIndicatorStruct[] exchangeIndicatorStruct, Price underlyingPrice, short productState, long entryTime,
	        String location)
	{
	    CurrentMarketStruct bestMarket = new CurrentMarketStruct(); 
	    bestMarket.productKeys = new com.cboe.idl.cmiProduct.ProductKeysStruct();
	    update.copyIntoStruct(bestMarket, CurrentMarketViewTypes.BEST_PRICE);
	    
	    CurrentMarketStruct bestLimitMarket = new CurrentMarketStruct(); 
	    bestLimitMarket.productKeys = new com.cboe.idl.cmiProduct.ProductKeysStruct();
	    update.copyIntoStruct(bestLimitMarket, CurrentMarketViewTypes.BEST_LIMIT_PRICE);
	    
	    CurrentMarketStruct bestPublicMarket = new CurrentMarketStruct(); 
	    bestPublicMarket.productKeys = new com.cboe.idl.cmiProduct.ProductKeysStruct();
	    update.copyIntoStruct(bestPublicMarket, CurrentMarketViewTypes.BEST_PUBLIC_ORDER_PRICE);

	    MDHCurrentMarketEntry entry = new MDHCurrentMarketEntry(bestMarket, bestLimitMarket, bestPublicMarket, nbboStruct,
	            botrStruct, exchangeIndicatorStruct, underlyingPrice, productState, entryTime, location);

	    enqueue(entry);
	}

	private void enqueue(MDHQueueEntry entry)
	{
		MarketDataHistoryProcessingThread thread = threads[entry.getProductKey() % getNumberOfThreads()];

		thread.enqueue(entry);
	}

/**
 * creates an expected opening price history entry
 *
	 * @param expectedOpenPrice
	 *            com.cboe.idl.cmiMarketData.ExpectedOpeningPriceStruct - the
	 *            expected opening price data
	 * @param underlyingPrice
	 *            com.cboe.util.Price - the price of the corresponding
	 *            underlying
 *
 * @author Magic Magee
 */
	public void createExpectedOpenPriceEntry(ExpectedOpeningPriceStruct expectedOpenPrice, Price underlyingPrice,
			short productState, long entryTime)
	{
		MDHEOPEntry entry = new MDHEOPEntry(expectedOpenPrice, underlyingPrice, productState, entryTime);

		enqueue(entry);
	}

	/**
	 * Creates an entry from a last sale. This metohd will use market data such
	 * as nbbo, best public price and size from market data instead of that is
	 * provided in lastSale message This method should be used when TickerDetail
	 * does not have all the market information such as when received as result
	 * of trade done on SBT server as opposed to TPF
	 */

	public void createLastSaleEntry(TimeStruct tradeTime, InternalTickerDetailStruct lastSale, Price underlyingPrice,
			short productState, long entryTime)
	{
		MDHLastSaleEntry entry = new MDHLastSaleEntry(tradeTime, lastSale, underlyingPrice, productState, entryTime);

		enqueue(entry);
}
	
	

/**
 * Creates an history entry from a product State change.
 *
	 * @param int
	 *            productKey product changing state
	 * @param Price
	 *            an underlying price
	 * @param productState
	 *            new state of product
	 */
	public void createProductStateChangeEntry(String sessionName, int productKey, Price underlyingPrice, short productState)
	{
		MDHProductStateChangeEntry entry = new MDHProductStateChangeEntry(sessionName, productKey, underlyingPrice, productState);

		enqueue(entry);
	}

	/*
	 * This method gets called from HistoryServer when it receives a CORBA call
	 * from trade server. This method enqueues the data in MDH queue. This
	 * method is called from MarketDataHistoryServicePOAStubImpl.
	 */
	public void create(String blockId, MarketDataHistoryEntriesStruct[] entries)
	{
		MDHEntriesStruct struct = new MDHEntriesStruct(blockId, entries);

		this.enqueue(struct);
	}

	/*
	 * This method gets called from HistoryServer when it receives a CORBA call
	 * from trade server. This method enqueues the data in MDH queue. This
	 * method is called ONLY if we are unblocking the received block of MDH
	 * entries prior to enqueuing in MDH queues. The unblocking of received MDH
	 * entry block is determined by the method called on the
	 * MarketDataHistoryService IDL by the trade server. This method is called
	 * from MarketDataHistoryServicePOAStubImpl.
	 */
	public void create(MarketDataHistoryEntry entry)
	{
		MDHEntryStruct struct = new MDHEntryStruct(entry);

		this.enqueue(struct);
	}

	/**
	 * Searches history for entries for product with a time boundary of the
	 * start time. The entries are gathered from that boundary in the direction
	 * indicated. The entries are returned in a time sequence with the entry
 * closest to the start time returned first time sequence.
 *
 */
public MarketDataHistoryEntry[] findByTime(int productKey, long startTime, short direction) throws NotFoundException
{
	// query for product
	MarketDataHistoryEntryImpl example = new MarketDataHistoryEntryImpl();
	addToContainer(example); // need to get database settings
	ObjectQuery query = new ObjectQuery(example);
	example.setProductKey(productKey);
    example.setDayOfWeek((byte) getDayOfWeek(startTime));
    example.setTradeServerId(serverInstanceNumber);
    
	ConstraintCompare timeConstraint = new ConstraintCompare();
	timeConstraint.setPersistence(example);
	timeConstraint.setField("entryTime");
		timeConstraint.setCompValue(String.valueOf(startTime));
	if (direction == QueryDirections.QUERY_FORWARD) // ??
	{
		timeConstraint.setComparison(">=");
	}
	else
	{
		timeConstraint.setComparison("<=");
	}
	query.addConstraint(timeConstraint);
		query.addOrderByField("entryTime", (direction == QueryDirections.QUERY_FORWARD) ? false : true);
    int maxSize = maxResultSize();
		query.setObjectLimit(maxSize + 1);
	try
	{
		Vector queryResult = query.find();
        int queryResultSize = queryResult.size();

			if (queryResult.size() < (maxSize + 1))
        {
				// This for loop is only used to loop through the marketData
				// history partitions i.e. Monday = 2 to Friday = 6 starting
				// from
				// the starttime going either Forwar or Backward based on query
				// direction.
            int partitionNumber = getDayOfWeek(startTime);
				for (int partionLoop = 0; partionLoop < 4; partionLoop++)
            {
                queryResultSize = queryResult.size();
					// get the next
                partitionNumber = calculatePartitionNumber(direction, partitionNumber);

					if (partitionNumber >= 1 && queryResultSize < (maxSize + 1))
                {
                    example.setDayOfWeek((byte) partitionNumber);
                    Vector tmpQueryResult = query.find();
						if (tmpQueryResult.size() >= ((maxSize + 1) - queryResultSize))
						{
							for (int i = 0; i < ((maxSize + 1) - queryResultSize); i++)
                            queryResult.add(tmpQueryResult.get(i));
                    }
                    else
                    {
                        queryResult.addAll(tmpQueryResult);
                    }
                }
					if (queryResult.size() == (maxSize + 1))
						break;
            }
        }

			if (queryResult.size() == (maxSize + 1))
            {
                MarketDataHistoryEntry entryOne = (MarketDataHistoryEntry) queryResult.lastElement();
				MarketDataHistoryEntry entryTwo = (MarketDataHistoryEntry) queryResult.elementAt(maxSize - 1);
            	if (entryOne.getEntryTime() == entryTwo.getEntryTime())
            	{
            	    long lastEntryTime = entryOne.getEntryTime();
            	    while (lastEntryTime == ((MarketDataHistoryEntry) queryResult.lastElement()).getEntryTime())
            	    {
						queryResult.removeElementAt(queryResult.size() - 1);
            	    }
            	}
            	else
            	    queryResult.removeElementAt(maxSize);
            }

		if (direction == QueryDirections.QUERY_FORWARD) // ??
		{
			Vector reverse = new Vector();
				for (int index = 0; index < queryResult.size(); index++)
					reverse.insertElementAt(queryResult.elementAt(index), 0);
			queryResult = reverse;
		}

        MarketDataHistoryEntry[] result = new MarketDataHistoryEntry[queryResult.size()];
		queryResult.copyInto(result);
		return result;
	}
	catch (PersistenceException e)
	{
		Log.exception(e);
			throw ExceptionBuilder.notFoundException("Market history query failed for product = " + productKey, 0);
			/*
			 * FIX_ME (Magee) errorCode for NotFound was not found
			 */
		}

	}

	/**
	 * Searches history for all last sale entries for the current day given a
	 * session and product. The result will be returned in ascending time order.
	 * Will assume that business day and calendar day are equivalent.
	 * 
	 * @param sessionName
	 *            name of session
	 * @param productKey
	 *            key of requested product
 * @return entries found for product and time
 */
	public MarketDataHistoryEntry[] findCurrentDayLastSales(String sessionName, int productKey)
	{
	// query for product
	MarketDataHistoryEntryImpl example = new MarketDataHistoryEntryImpl();
	addToContainer(example); // need to get database settings
	ObjectQuery query = new ObjectQuery(example);
	example.setProductKey(productKey);
    example.setSessionName(sessionName);
    example.setEntryType(MarketDataHistoryEntryTypes.PRICE_REPORT_ENTRY);

    long currentTime = System.currentTimeMillis();
    
    // get start time that is the earliest time of the current day
    long startTime = DateWrapper.convertToMillis(DateWrapper.convertToDate(currentTime));
    long endTime = currentTime;
    
    example.setDayOfWeek((byte) getDayOfWeek(startTime));
    example.setTradeServerId(serverInstanceNumber);
	ConstraintCompare timeConstraint = new ConstraintCompare();
	timeConstraint.setPersistence(example);
	timeConstraint.setField("entryTime");
		timeConstraint.setCompValue(String.valueOf(startTime));
	timeConstraint.setComparison(">=");

	query.addConstraint(timeConstraint);
    
    ConstraintCompare timeEndConstraint = new ConstraintCompare();
    timeEndConstraint.setPersistence(example);
    timeEndConstraint.setField("entryTime");
    timeEndConstraint.setCompValue(new String().valueOf(endTime));
    timeEndConstraint.setComparison("<");
    
    query.addConstraint(timeEndConstraint);
    
	query.addOrderByField("entryTime");
	try
	{
		Vector queryResult = query.find();
		MarketDataHistoryEntry[] result = new MarketDataHistoryEntry[queryResult.size()];
		queryResult.copyInto(result);
		return result;
	}
	catch (PersistenceException e)
	{
		Log.exception(this, "Market history query failed for product = " + productKey, e);
        return new MarketDataHistoryEntry[0];
	}
}

/**
 *  This method will update salePrefix if needed after doing query to history
	 * A database query can be made to find of is salePrefix should be LATE ot
	 * OSEQ Here is the logic: SELECT * FROM MKT_DATA_HIST WHERE &start_time <=
	 * ENTRY_TIME AND &end_time >= ENTRY_TIME AND PROD_KEY = &prod_key AND (
	 * entry_type = 2 OR ( entry_type = 1 AND BID_PRICE <=&lastSalePrice AND
	 * &lastSalePrice <= ASK_PRICE)) ORDER BY entry_type and DAYOFWEEK =
	 * to_char(sysdate,'D')
	 * 
	 * If no row found lastSalePrefix = “LATE” //No quote, no trade
	 * 
	 * If first row entry_type = 1 //yes quote lastSalePrefix = “”
	 * 
	 * If first row entry_type = 2 lastSalePrefix = “OSEQ” //no quote, yes trade
 *
 * @param ticker
 */

	public void updateLastSalePrefixForOSEQ(TickerStruct ticker)
	{

    String lastSalePrefix = new String();

    long endTime = System.currentTimeMillis();
		// Find out starting time for duration
		long startTime = endTime - (getLastPrefixSearchDurationInSeconds() * 1000);

    // query for product
    MarketDataHistoryEntryImpl example = new MarketDataHistoryEntryImpl();
    addToContainer(example); // need to get database settings
    ObjectQuery query = new ObjectQuery(example);
    example.setProductKey(ticker.productKeys.productKey);
    example.setSessionName(ticker.sessionName);

    String lastSalePriceDBString = (new PriceSqlType(ticker.lastSalePrice)).toDatabaseString();

		String extraWhereClause = " entry_time >= " + String.valueOf(startTime) + " AND entry_time <= " + String.valueOf(endTime)
				+ " AND (         entry_type = " + MarketDataHistoryEntryTypes.PRICE_REPORT_ENTRY + " OR  ( entry_type = "
				+ MarketDataHistoryEntryTypes.QUOTE_ENTRY + " AND BID_PRICE <= " + lastSalePriceDBString + " AND "
				+ lastSalePriceDBString + " <= ASK_PRICE))" + " AND DAYOFWEEK = to_char(sysdate,'D') ";

		query.setSameConstraint(example, extraWhereClause);

    query.addOrderByField("entryType");
    try
    {
        Vector queryResult = query.find();
        MarketDataHistoryEntry[] result = new MarketDataHistoryEntry[queryResult.size()];
        queryResult.copyInto(result);
			if (result.length == 0)
			{ // No quote, no trade
            lastSalePrefix = "LATE";
			}
			else if (result[0].getEntryType() == 1)
			{ // yes quote
            lastSalePrefix = "";
			}
			else if (result[0].getEntryType() == 2)
			{ // no quote, yes trade
            lastSalePrefix = "OSEQ";
        }
    }
    catch (PersistenceException e)
    {
			Log.exception(this, "Market history query while evaluating lastSalePrefix failed for product = "
					+ ticker.productKeys.productKey, e);
    }

    ticker.salePrefix = lastSalePrefix;
}

/**
 * Gets the maximum size for result sets.
 *
 * @author John Wickberg
 */
public int maxResultSize()
{
		if (maxResultSize == null)
		{
        String size = DEFAULT_RESULT_SIZE;
        try
        {
				size = getProperty(MAX_RESULT_SIZE);
        }
			catch (NoSuchPropertyException nspe)
         {
				Log.alarm(this, "Failed to get property '" + MAX_RESULT_SIZE);
				Log.exception(nspe);
				throw new IllegalArgumentException("Failed to get property '" + MAX_RESULT_SIZE);
			}
			maxResultSize = new Integer(size);
        Log.information(this, MAX_RESULT_SIZE + " property is " + maxResultSize);
    }
	return maxResultSize.intValue();
}

/**
 * Purges old market data history entries.
 *
 * @see ProductClassHome#purge
 */
public void purge(long retentionCutoff)
{

	MarketDataHistoryEntryImpl example = new MarketDataHistoryEntryImpl();
	addToContainer(example);	// need to get database settings
	ObjectQuery query = new ObjectQuery(example);
	try
	{
		query.setFieldConstraint(example, "entryTime", "" + retentionCutoff, "<=");
		Vector queryResult = query.find();
		MarketDataHistoryEntryImpl marketDataHistoryEntry;
		Enumeration resultsEnum = queryResult.elements();
		while (resultsEnum.hasMoreElements())
		{
			marketDataHistoryEntry = (MarketDataHistoryEntryImpl) resultsEnum.nextElement();
			try
			{
				marketDataHistoryEntry.markForDelete();
			}
			catch (PersistenceException e)
			{
					Log.alarm(this, "Unable to delete market data history product = "
							+ marketDataHistoryEntry.getObjectIdentifierAsInt());
			}
		}
	}
	catch (PersistenceException e)
	{
		Log.exception(this, "Query for purging of market data history failed", e);
	}

}

    /**
     * This returns the day of the week based on the time in millis
     */
	int getDayOfWeek(long timeInMillis)
	{
        DateWrapper dateWrapper = new DateWrapper(timeInMillis);
        Calendar calendar = dateWrapper.getCalendar();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        // for Calendar, 1 - Sunday 2 - Monday...
        return (dayOfWeek);
    }
	
    /**
    *
    *
    */
    int calculatePartitionNumber(short direction, int prevPartitionNumber)
    {
        int partitionNumberForQuery = -1;

		if (direction == QueryDirections.QUERY_BACKWARD)
        {
			if (prevPartitionNumber <= 2)
                partitionNumberForQuery = 6;
            else
                partitionNumberForQuery = prevPartitionNumber - 1;
        }
        else
        {
			if (prevPartitionNumber >= 6)
                partitionNumberForQuery = 2;
            else
                partitionNumberForQuery = prevPartitionNumber + 1;
        }
        return partitionNumberForQuery;
    }

	public String getIdGenerationStrategy()
	{
		return idGenerationStrategyProperty.get();
	}

	public void createShortSaleTriggeredModeEntry(String sessionName, int productKey, boolean shortSaleTriggeredMode)
    {
        throw new UnsupportedOperationException("createShortSaleTriggeredModeEntry() is not implemented.");
    }
}
