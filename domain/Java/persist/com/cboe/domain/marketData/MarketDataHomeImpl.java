package com.cboe.domain.marketData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.cboe.domain.util.NoPrice;
import com.cboe.domain.util.TradingSessionNameHelper;
import com.cboe.exceptions.AlreadyExistsException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.TransactionFailedException;
import com.cboe.idl.cmiErrorCodes.NotFoundCodes;
import com.cboe.idl.cmiErrorCodes.TransactionFailedCodes;
import com.cboe.idl.cmiMarketData.ExchangeIndicatorStruct;
import com.cboe.idl.cmiProduct.ProductKeysStruct;
import com.cboe.idl.exchange.ExchangeClassIndicatorStruct;
import com.cboe.idl.session.TradingSessionStruct;
import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.foundationFramework.HomeFactory;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.systemsManagementService.NoSuchPropertyException;
import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.domain.TradingClass;
import com.cboe.interfaces.domain.TradingClassHome;
import com.cboe.interfaces.domain.TradingProduct;
import com.cboe.interfaces.domain.TradingProductHome;
import com.cboe.interfaces.domain.linkageClassGate.LinkageClassGateHome;
import com.cboe.interfaces.domain.marketData.MarketData;
import com.cboe.interfaces.domain.marketData.MarketDataHome;
import com.cboe.interfaces.domain.tradingProperty.MarketDataAwayExchanges;
import com.cboe.util.ExceptionBuilder;
import com.cboe.util.MathExtensions;
import com.cboe.domain.util.intMaps.IntHashMap;
/**
 * A persistentable implementation of MarketDataHome.
 *
 * @author John Wickberg
 */
public class MarketDataHomeImpl extends BOHome implements MarketDataHome
{
    protected String[]  exchIndSessions = new String[]  {};  // 1:1 mapping to exchIndCaches; session for cache
    protected IntHashMap[] exchIndCaches   = new IntHashMap[] {};  // 1:1 mapping to exchIndSessions; maps of <prodKey> -> <exchangeindstruct[]>

	/**
	 * Property that controls initial size of product key cache.
	 */
	public static final String PRODUCT_CACHE_SIZE = "productCacheSize";
    /**
     * Default cache size.
     */
    public static final String DEFAULT_CACHE_SIZE = "11519";
    /**
     * Property that controls if home should be active when service is in slave mode.  Value
     * for this property must match value of same property for MarketDataService.
     */
    public static final String ACTIVE_WHEN_SLAVE = "activeWhenSlave";
    /**
     * Default value for active when slave.
     */
    public static final String DEFAULT_ACTIVE_SLAVE_STATUS = "false";
    /**
     * Initial size of product cache.
     */
    private Integer productCacheSize;
    /**
     * Active when slave indicator.
     */
    private Boolean activeWhenSlave;
    
    private boolean autoCalcNBBO;
    
	/**
	 * Cache to access market data by product key.
	 */
	private Map<MarketDataCacheKey, MarketData> productCache;
	/**
	 * Cache to underlying products by class key.
	 */
	private Map<MarketDataCacheKey, ArrayList<MarketData>> underlyingClassCache;
    /**
     * Cached reference to the trading class home.
     */
    private TradingClassHome tradingClassHome;
    /**
     * Cached reference to the trading product home.
     */
    private TradingProductHome tradingProductHome;
        private static final ExchangeIndicatorStruct[] emptyExchangeIndicatorStructArray = new ExchangeIndicatorStruct[0];

    private LinkageClassGateHome cachedLinkageClassGateHome;

/**
 *
 * MarketDataHomeImpl constructor comment.
 */
public MarketDataHomeImpl()
{
    setSmaType( "GlobalMarketData.MarketDataHomeImpl" );
}
/**
 * Adds market data to index.
 *
 * @param marketData object to be indexed.
 *
 * @author John Wickberg
 */
private void addToIndex(MarketDataImpl marketData)
{
	MarketDataCacheKey key = createCacheKey(marketData.getSessionName(), marketData.getProductKey());
	productCache.put(key, marketData);
    if (isUnderlyingSession(marketData.getSessionName())) {
        MarketDataCacheKey classKey = createCacheKey(marketData.getSessionName(), marketData.getClassKey());
        ArrayList<MarketData> classMarketData =  underlyingClassCache.get(classKey);
        if (classMarketData == null) {
            classMarketData = new ArrayList<MarketData>();
            underlyingClassCache.put(classKey, classMarketData);
        }
        classMarketData.add(marketData);
    }
}

    //exchIndSessions = new String[] {};  // 1:1 mapping to exchIndCaches; session for cache
    //exchIndCaches  = new String[] {};   // 1:1 mapping to exchIndSessions; maps of <prodKey> -> <exchangeindstruct[]>

public synchronized void updateExchangeIndicatorCache(String sessionName, ExchangeClassIndicatorStruct indicator)
{
    if (Log.isDebugOn())
    {
        Log.debug(this, "Updating exchange indicator cache for rpt class " + indicator.classKey);
    }

    // Find the cache map for the session, otherwise create it:
    //
    int sessIdx = -1;
    for (int i=0; i <  exchIndSessions.length; i++)
    {
        if (exchIndSessions[i].equals(sessionName))
        {
            sessIdx = i;
            break;
        }
    }
    if (sessIdx < 0)
    {
        String[]  newSess = new String [exchIndSessions.length+1];
        IntHashMap[] newMaps = new IntHashMap[exchIndSessions.length+1];
        System.arraycopy(exchIndSessions, 0, newSess, 1, exchIndSessions.length);
        System.arraycopy(exchIndCaches,   0, newMaps, 1, exchIndCaches.length  );
        sessIdx=0;
        newSess[sessIdx] = sessionName;
        newMaps[sessIdx] = new IntHashMap(1023);
        exchIndSessions = newSess;
        exchIndCaches = newMaps;
        if (Log.isDebugOn())
        {
            Log.debug(this, "Updating exchange indicator cache: create map for session '" + sessionName + "'");
        }
    }

    final int rptClassKey = indicator.classKey;

    // Replace or create the indicator in the cache:
    //
    ExchangeIndicatorStruct[] exchInds = (ExchangeIndicatorStruct[])exchIndCaches[sessIdx].get(rptClassKey);
    int idx = -1;
    if (exchInds == null)
    {
        exchInds = new ExchangeIndicatorStruct[1];
        idx = 0;
        exchIndCaches[sessIdx].put(rptClassKey, exchInds);
        if (Log.isDebugOn())
        {
            Log.debug(this, "Updating exchange indicator cache: cached new struct array for rpt class " + rptClassKey);
        }
    }
    else
    {
        for (int i=0; i < exchInds.length; i++)
        {
            if (exchInds[i].exchange.equals(indicator.exchange))
            {
                if (Log.isDebugOn())
                {
                    Log.debug(this, "Updating exchange indicator cache: update existing entry for rpt class " + rptClassKey + " and exchange " + indicator.exchange + " at index " + idx);
                }
                idx = i;
                break;
            }
        }
    }
    if (idx < 0)
    {
        ExchangeIndicatorStruct[] newInds = new ExchangeIndicatorStruct[exchInds.length+1];
        System.arraycopy(exchInds, 0, newInds, 1, exchInds.length);
        exchInds = newInds;
        exchIndCaches[sessIdx].put(rptClassKey, exchInds);
        idx = 0;
        if (Log.isDebugOn())
        {
            Log.debug(this, "Updating exchange indicator cache: added new entry for rpt class " + rptClassKey + " and exchange " + indicator.exchange
                + " (there are now " + exchInds.length + " exchange states for this rpt class)");
        }
    }

    ExchangeIndicatorStruct exchInd = new ExchangeIndicatorStruct();
    exchInd.exchange = indicator.exchange;
    exchInd.marketCondition = indicator.marketCondition;
    exchInds[idx] = exchInd;
}



/**
 *  Determine if the given session name is an underlying session
 */
protected boolean isUnderlyingSession(String sessionName)
{
    return TradingSessionNameHelper.isUnderlyingSession(sessionName);
}

/**
 * We need a method that takes the rptClassKey as a param to create.
 * @param String sessionName
 * @param productKey key of product
 * @param classKey key of product's class
 * @param rptClassKey of the product's class
 * @return created market data
 *
 * @author Byron Xiao
 *  
 */
public synchronized MarketData create(String sessionName, int productKey, int classKey, int rptClassKey) throws AlreadyExistsException, TransactionFailedException
{
    return create(sessionName, productKey, classKey, rptClassKey, new NoPrice());
}

public synchronized MarketData create(String sessionName, int productKey, int classKey, int rptClassKey, Price closePrice) throws AlreadyExistsException, TransactionFailedException
{
    if (getMarketData(sessionName, productKey) == null)
    {
        final String localExchange = TradingSessionNameHelper.getSession(sessionName).exchangeAcronym;
        MarketDataImpl marketData = new MarketDataImpl(sessionName, localExchange, productKey, classKey, rptClassKey, closePrice, autoCalcNBBO);
        addToContainer(marketData);
        addToIndex(marketData);
        return marketData;
    }
    else
    {
        throw ExceptionBuilder.alreadyExistsException("Market data already exists for product = " + productKey, TransactionFailedCodes.CREATE_FAILED);
    }
}

/**
 * Creates a new market data.
 *
 * @param String sessionName
 * @param productKey key of product
 * @param classKey key of product's class
 * @return created market data
 *
 * @author John Wickberg
 */
public synchronized MarketData create(String sessionName, int productKey, int classKey) throws AlreadyExistsException, TransactionFailedException
{
    return create(sessionName, productKey, classKey, new NoPrice());
}

/**
 * Creates a new market data.
 *
 * @param String sessionName
 * @param productKey key of product
 * @param classKey key of product's class
 * @param closePrice close price of the product
 * @return created market data
 *
 * @author John Wickberg
 */
public synchronized MarketData create(String sessionName, int productKey, int classKey, Price closePrice) throws AlreadyExistsException, TransactionFailedException
{
	if (getMarketData(sessionName, productKey) == null)
	{
		TradingSessionStruct tradingSession = TradingSessionNameHelper.getSession(sessionName);
		if (tradingSession !=null)
		{
		    final String localExchange = tradingSession.exchangeAcronym;
			MarketDataImpl marketData = new MarketDataImpl(sessionName, localExchange, productKey, classKey, closePrice, autoCalcNBBO);
			addToContainer(marketData);
			addToIndex(marketData);
			return marketData;
		}
		else
		{
			throw ExceptionBuilder.alreadyExistsException("Market data canot be created for product:"+productKey +" session "+sessionName +"Not found", TransactionFailedCodes.CREATE_FAILED);
		}
	}
	else
	{
		throw ExceptionBuilder.alreadyExistsException("Market data already exists for product = " + productKey, TransactionFailedCodes.CREATE_FAILED);
	}
}
/**
 * Searches for all market data entries for class.
 *
 * @param String sessionName
 * @param classKey search key
 * @return all found market data
 *
 * @author John Wickberg
 */
public MarketData[] findByClass(String sessionName, int classKey) throws NotFoundException
{
	MarketData[] result;
    if (isUnderlyingSession(sessionName)) {
        result = findByUnderlyingClass(sessionName, classKey);
    }
    else {
        TradingProduct[] products = getTradingProductsForClass(classKey);
    	result = new MarketData[products.length];
        for (int i = 0; i < result.length; i++) {
            try {
                result[i] = findByProduct(sessionName, products[i].getProductKey());
            }
            catch (NotFoundException e) {
                try {
                    result[i] = create(sessionName, products[i].getProductKey(), classKey);
                }
                catch (TransactionFailedException tfe) {
                    Log.exception(this, "Unable to recreate market data for product: " + products[i].getProductKey(), tfe);
                }
                catch (AlreadyExistsException tfe) {
                    // It would be odd to get this case:
                    // only if multiple threads are finding the same classkey for the first time.  Finding again should find it, though.
                    try
                    {
                        result[i] = findByProduct(sessionName, products[i].getProductKey());
                    }
                    catch (NotFoundException ex)
                    {
                        // ok, now we've really failed.
                        Log.exception(this, "Unable to recreate market data for product: " + products[i].getProductKey(), tfe);
                    }
                }
            }
    	}
    }
    return result;
}
/**
 * Returns MarketData for requested product.
 *
 * @param String sessionName,
 * @param int productKey
 * @author John Wickberg
 */
public MarketData findByProduct(String sessionName, int productKey) throws NotFoundException
{
	MarketData result = getMarketData(sessionName, productKey);
	if (result != null)
	{
		return result;
	}
	else
	{
		throw ExceptionBuilder.notFoundException("Market data not found for product key = " + productKey, 0 /* FIX_ME (Magee) errorCode for NotFound was not found */ );
	}
}

    public MarketData[] findByReportingClass(String sessionName, int rptClassKey) throws NotFoundException {
        MarketData[] result;
        TradingProduct[] products = getTradingProductHome().findByReportingClass(sessionName, rptClassKey);
        if (products == null || products.length == 0)
        {
            throw ExceptionBuilder.notFoundException("No market data found for session " + sessionName + " and rpt class key " + rptClassKey, NotFoundCodes.RESOURCE_DOESNT_EXIST);
        }
        result = new MarketData[products.length];
        for (int i = 0; i < result.length; i++) {
            try {
                result[i] = findByProduct(sessionName, products[i].getProductKey());
            }
            catch (NotFoundException e) {
                throw ExceptionBuilder.notFoundException("No market data found for session " + sessionName + " and rpt class key " + rptClassKey
                    + " (for product " + products[i].getProductKey() + ")", NotFoundCodes.RESOURCE_DOESNT_EXIST);
            }
        }
        return result;
    }

    /**
     * Return the cached LinkageClassGateHome
     * @return LinkageClassGateHome
     * @author Mark Wolters
     */
    public LinkageClassGateHome getLinkageClassGateHome() {
        if (cachedLinkageClassGateHome == null) {
            try {
                cachedLinkageClassGateHome = (LinkageClassGateHome) HomeFactory.getInstance().findHome(LinkageClassGateHome.HOME_NAME);
            }
            catch (Exception e) {
                Log.exception("Market Data Service is Unable to find LinkageClassGateHome", e);
            }
        }
        return cachedLinkageClassGateHome;
    }

    /**
 * Find market data for an underlying class.  Underlying classes are not in the TradingClass collection since
 * they are not traded, so need to be found using separate method.
 */
private MarketData[] findByUnderlyingClass(String sessionName, int classKey) throws NotFoundException {
    MarketDataCacheKey cacheKey = threadLocalCacheKey(sessionName, classKey);
    ArrayList<MarketData> classMarketData = underlyingClassCache.get(cacheKey);
    if (classMarketData != null) {
        MarketData[] result = new MarketData[classMarketData.size()];
        classMarketData.toArray(result);
        return result;
    }
    else {
        throw ExceptionBuilder.notFoundException("No market data found for session " + sessionName + " and class key " + classKey, NotFoundCodes.RESOURCE_DOESNT_EXIST);
    }
}
/**
 * Gets market data for product from product cache.
 *
 * @author John Wickberg
 */
private MarketData getMarketData(String sessionName, int productKey)
{
	MarketDataCacheKey key = threadLocalCacheKey(sessionName, productKey);
	MarketData result =  productCache.get(key);
	return result;
}
/**
 * Gets the initial size of the product cache.
 *
 * @author John Wickberg
 */
public int getProductCacheSize()
{
    if (productCacheSize == null) {
        String size = DEFAULT_CACHE_SIZE;
        try
        {
            size = getProperty( PRODUCT_CACHE_SIZE );
        }
        catch( NoSuchPropertyException nspe )
        {
            Log.alarm( this, "Failed to get property '" + PRODUCT_CACHE_SIZE );
            Log.exception( nspe );
            throw new IllegalArgumentException( "Failed to get property '" + PRODUCT_CACHE_SIZE );
        }
	    productCacheSize = new Integer( size );
        Log.information(this, PRODUCT_CACHE_SIZE + " property is " + productCacheSize);
    }
    return productCacheSize.intValue();
}
/**
 * Gets home for trading classes.
 */
private TradingClassHome getTradingClassHome() {
    if (tradingClassHome == null) {
        try {
            tradingClassHome = (TradingClassHome) HomeFactory.getInstance().findHome(TradingClassHome.HOME_NAME);
        }
        catch (Exception e) {
            Log.exception(this, "Unable to find trading class home", e);
        }
    }
    return tradingClassHome;
}

private TradingProductHome getTradingProductHome() {
    if (tradingProductHome == null) {
        try {
            tradingProductHome = (TradingProductHome) HomeFactory.getInstance().findHome(TradingProductHome.HOME_NAME);
        }
        catch (Exception e) {
            Log.exception(this, "Unable to find trading product home", e);
        }
    }
    return tradingProductHome;
}
/**
 * Gets the trading products for a class.
 */
private TradingProduct[] getTradingProductsForClass(int classKey) throws NotFoundException {
    TradingClass requestedClass = getTradingClassHome().findByKey(classKey);
    return requestedClass.getProducts(true);
}
/**
 * Prepares to begin processing as master.
 */
public void goMaster(boolean failover) {
}
/**
 *  Prepares to begin processing as slave.
 */
public void goSlave() {
    if (isActiveWhenSlave()) {
        // only want to create cache once - will continue to use existing
        // data across master/slave switches.
        if (productCache == null) {
            productCache = new ConcurrentHashMap<MarketDataCacheKey, MarketData>(getProductCacheSize());
            underlyingClassCache = new ConcurrentHashMap<MarketDataCacheKey, ArrayList<MarketData>>(MathExtensions.nextPrime(getProductCacheSize() / 20));
        }
    }
    else {
    	// assume that persistent data is being used and that we need to
        // create fresh cache on each switch
	    productCache = new ConcurrentHashMap<MarketDataCacheKey, MarketData>(getProductCacheSize());
        underlyingClassCache = new ConcurrentHashMap<MarketDataCacheKey, ArrayList<MarketData>>(MathExtensions.nextPrime(getProductCacheSize() / 20));
    }
    
    initProperties();    
}

private void initProperties()
{
    try
    {
        autoCalcNBBO = Boolean.valueOf(getProperty( "autoCalcNBBO" ));
    }
    catch( NoSuchPropertyException nspe )
    {
        Log.alarm( this, "Failed to get property autoCalcNBBO" );
        Log.exception( nspe );
        throw new IllegalArgumentException( "Failed to get property autoCalcNBBO");
    }
    Log.information(this, "autoCalcNBBO property is " + autoCalcNBBO);    
}

/**
 * Reads all market data from database.
 *
 * @author John Wickberg
 */
public void initialize()
{
	// create cache for market data key by product
	productCache = new ConcurrentHashMap<MarketDataCacheKey, MarketData>(getProductCacheSize());
	registerAdminRequestCommands();
}

	protected void registerAdminRequestCommands() {
		try {
			this.registerCommand(
						this, // Callback Object
						"setExchangeInd", // External name
						"adminSetExchangeIndicator", // Method name
						"Set the exchange indicator to the new indicator passed in: By session", // Method
																									// description
			new String[] { String.class.getName(), // session
																// name
							String.class.getName(), // Exchange;
							String.class.getName(), // Market Condition
							String.class.getName(), // productKey
						}, new String[] { "session name", "Exchange",
								"Market Condition", "ProductKey" });
		} catch (Exception exe) {
			Log.alarm("Exception while setting exchange indicator: " + exe);
		}
	}
	
	public void adminSetExchangeIndicator(String sessionName, String exchange,
			String p_marketCondition, String p_productKey) {
		
		char exchangeCode = (char) MarketDataAwayExchanges.findLinkageExchange(exchange).exchangeChar;
		ProductKeysStruct productKey = new ProductKeysStruct(Integer.parseInt(p_productKey), 0, (short)0, 0);
		if (productKey != null){
			MarketData marketData = getMarketData((TradingSessionNameHelper.getConfiguredSessionNames())[0], productKey.productKey);
			if (marketData != null)
				marketData.updateExchangeIndicator(exchangeCode, productKey.productKey, (char)(Short.parseShort(p_marketCondition)));
		}
	}


/**
 * Gets value of "active when slave" property.
 * 
 * @return true if property is set
 */
private boolean isActiveWhenSlave()
{
    if (activeWhenSlave == null) {
        //In case we end up supporting defaults
        String active = DEFAULT_ACTIVE_SLAVE_STATUS;
        try
        {
            active = getProperty( ACTIVE_WHEN_SLAVE );
        }
        catch( NoSuchPropertyException nspe )
        {
            Log.alarm( this, "Failed to get property '" + ACTIVE_WHEN_SLAVE );
            Log.exception( nspe );
            throw new IllegalArgumentException( "Failed to get property '" + ACTIVE_WHEN_SLAVE  );
        }
        activeWhenSlave = new Boolean( active );
        Log.information(this, ACTIVE_WHEN_SLAVE + " property is " + activeWhenSlave);
    }
    return activeWhenSlave.booleanValue();
}

    /**
     * Construct Product Cache key from sessionName and product or class key
     *
     * @param String sessionName,
     * @param int key
     */
    private MarketDataCacheKey threadLocalCacheKey(String sessionName, int key)
    {
        final MarketDataCacheKey cKey = cacheKeyTL.get();
        cKey.sessionName = sessionName;
        cKey.key = key;
        return cKey;
    }
    /**
     * Create Product Cache key from sessionName and product or class key
     *
     * @param String sessionName,
     * @param int key
     */
    private MarketDataCacheKey createCacheKey(String sessionName, int key)
    {
        final MarketDataCacheKey cKey = new MarketDataCacheKey();
        cKey.sessionName = sessionName;
        cKey.key = key;
        return cKey;
    }
    
    private ThreadLocal<MarketDataCacheKey> cacheKeyTL = new ThreadLocal<MarketDataCacheKey>()
    {
        @Override
        protected MarketDataCacheKey initialValue()
        {
            return new MarketDataCacheKey();
        }
    };
    
    /**
     * Use this custom cache key rather than constructing a compound string every single
     * time.
     * 
     * @author sinclair
     */
    private static final class MarketDataCacheKey
    {
        private String sessionName;
        private int key;
        
        @Override 
        public String toString()
        {
            return sessionName +":"+key;
        }
        
        @Override
        public int hashCode()
        {
            // Ignore session name in hash code calc since it's VERY likely unique 
            // for a given product or class key.
            return key; 
        }
        
        @Override
        public boolean equals(Object p_rhs)
        {
            if (p_rhs == this)
            {
                return true;
            }
            if (p_rhs == null || p_rhs.getClass() != this.getClass())
            {
                return false;
            }
            final MarketDataCacheKey rhs = (MarketDataCacheKey) p_rhs;
            return rhs.key == key && rhs.sessionName.equals(sessionName);
        }
    }
}
