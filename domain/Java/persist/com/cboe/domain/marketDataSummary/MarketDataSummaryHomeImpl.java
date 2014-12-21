package com.cboe.domain.marketDataSummary;
import com.cboe.domain.util.PriceFactory;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiErrorCodes.*;
import com.cboe.idl.cmiMarketData.*;
import com.cboe.idl.marketDataSummary.ClassSummaryStruct;
import com.cboe.idl.marketDataSummary.ProductSummaryStruct;
import com.cboe.idl.trade.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.FatalFoundationFrameworkException;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;
import com.cboe.infrastructureServices.persistenceService.*;
import com.cboe.infrastructureServices.systemsManagementService.*;
import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.domain.TradingClass;
import com.cboe.interfaces.domain.TradingClassHome;
import com.cboe.interfaces.domain.TradingProduct;

import com.cboe.interfaces.domain.marketDataSummary.*;
import com.cboe.util.*;
import java.util.*;
/**
 *  A persistentable implementation of MarketDataSummaryHome.
 *
 *@author  David Hoag
 *@created  September 19, 2001
 */
public class MarketDataSummaryHomeImpl extends BOHome implements MarketDataSummaryHome
{
    private Configuration config;
	
    private static final String LIST_OF_SESSIONS = "listOfSessions";
    
    /**
     *  MarketDataHomeImpl constructor comment.
     */
    public MarketDataSummaryHomeImpl()
    {
        setSmaType( "GlobalMarketDataSummary.MarketDataSummaryHomeImpl" );
        config = new Configuration(this);
    }
    /**
     *  Creates a new market data.
     *
     *@param  productKey key of product
     *@param  classKey key of product's class
     *@param  sessionName
     *@return  created market data
     *@exception  TransactionFailedException
     */
    public synchronized MarketDataSummary create( String sessionName, int productKey, int classKey ) throws TransactionFailedException
    {
        MarketDataSummaryCache marketDataSummaryCache = MarketDataSummaryCache.getInstance();
    	try
        {
            // Check in the cache, if not present then insert in database and update in Cache
            if( getMarketData( sessionName, classKey, productKey ) == null )
            {
            	MarketDataSummaryImpl marketData = new MarketDataSummaryImpl();
                addToContainer( marketData );
                marketData.create( sessionName, productKey, classKey );
                // Update cache with MarketDataSummary object.
                marketDataSummaryCache.updateCache(sessionName, classKey, productKey, marketData);
                return marketData;
            }
        }
        catch( DataValidationException de )
        {
            Log.debug( this, de.details.message );
            Log.exception( this, "Trouble finding MarketDataSummary", de );
        }
        catch( Exception pe )
        {
            Log.exception( this, "Trouble finding MarketDataSummary", pe );
            throw ExceptionBuilder.transactionFailedException( "Exception looking for existing market data summary = " + productKey, TransactionFailedCodes.CREATE_FAILED );
        }
        throw ExceptionBuilder.transactionFailedException( "Market data summary already exists for product = " + productKey, TransactionFailedCodes.CREATE_FAILED );
    }
    /**
     *  Returns MarketDataSummary for requested product.
     *
     *@param  sessionName
     *@param  productKey
     *@return
     *@exception  NotFoundException
     */
    public MarketDataSummary findByProduct( String sessionName, int classKey, int productKey ) throws NotFoundException
    {
    	try
        {
            // Find the product from the Cache
            MarketDataSummary result = getMarketData( sessionName, classKey, productKey );
            if( result != null )
            {
                return result;
            }
        }
        catch( DataValidationException de )
        {
            Log.debug( this, de.details.message );
            Log.exception( this, "Trouble finding MarketDataSummary", de );
        }
        catch( Exception pe )
        {
            Log.exception( this, "Trouble finding MarketDataSummary", pe );
        }
        throw ExceptionBuilder.notFoundException( "Market data not found for product key = " + productKey, 0);
    }
    /**
     *  Prepares to begin processing as master.
     *
     *@param  failover
     */
    public void goMaster( boolean failover ) {
        String [] sessionList = null;
        try
        {
            sessionList = config.getPropertyList(LIST_OF_SESSIONS, ",");
        }
        catch (Exception ex)
        {
            Log.exception(this, ex);
            throw new FatalFoundationFrameworkException(ex, ex.getMessage());
        }
        Log.information(this, "Building MarketDataSummaryCache");
        MarketDataSummaryCache.getInstance().setSessionList(sessionList);
        Thread cacheBuilderThread = new Thread(MarketDataSummaryCache.getInstance());
        cacheBuilderThread.start();
    }
    /**
     *  Prepares to begin processing as slave.
     */
    public void goSlave() { }
    /**
     *
     */
    public MarketDataSummary[] findByClasses( final String sessionName, final int [] classKeys) throws SystemException
    {
    	// Find from Cache
        MarketDataSummaryCache marketDataSummaryCache = MarketDataSummaryCache.getInstance();
        MarketDataSummaryImpl marketDataSummaryImplForClass[] = null;
        ArrayList <MarketDataSummary>marketDataSummaryList = new ArrayList<MarketDataSummary>(1001);
        for(int i = 0; i < classKeys.length; i++)
        {
        	marketDataSummaryImplForClass = marketDataSummaryCache.findFromCacheByClass(sessionName, classKeys[i]);
        	for(int j = 0; j < marketDataSummaryImplForClass.length; j++)
            {
        	    marketDataSummaryList.add(marketDataSummaryImplForClass[j]);
            }
        }
        MarketDataSummaryImpl[] marketDataSummaryImpl = new MarketDataSummaryImpl[marketDataSummaryList.size()];
        return marketDataSummaryList.toArray(marketDataSummaryImpl);    	
    }
    
    /**
     * @see com.cboe.interfaces.domain.marketDataSummary.MarketDataSummaryHome#findClassSummary(String, int[]).
     */
   public ClassSummaryStruct[] findClassSummary( final String sessionName, final int [] classKeys) 
           throws SystemException
   {
        ClassSummaryStruct[] result = null;
        List<ClassSummaryStruct> resultList = new ArrayList<ClassSummaryStruct>();
        if (sessionName != null && classKeys != null) {
            MarketDataSummaryCache marketDataSummaryCache = MarketDataSummaryCache.getInstance();
            MarketDataSummaryImpl marketDataSummaryImplForClass[] = null;
            for (int i = 0; i < classKeys.length; i++) {
                marketDataSummaryImplForClass = 
                    marketDataSummaryCache.findFromCacheByClass(sessionName, classKeys[i]);
                resultList.add(getClassSummaryStruct(marketDataSummaryImplForClass));
            }
        }
        result = resultList.toArray(new ClassSummaryStruct[resultList.size()]);
        return result;
    }
   
    /**
     * Return a list of ClassSummaryStructs
     * @author Cognizant Technology Solutions
     * 
     * @param marketDataSummaryImplForClass
     */
    private ClassSummaryStruct getClassSummaryStruct(final MarketDataSummaryImpl[] marketDataSummaryImplForClass) {
        ClassSummaryStruct classSummaryStructObj = new ClassSummaryStruct(); ;
        if(marketDataSummaryImplForClass != null && marketDataSummaryImplForClass.length > 0)
        {
            final int marketDataSummaryLength = marketDataSummaryImplForClass.length;
            List<ProductSummaryStruct> summaries = new ArrayList<ProductSummaryStruct>(marketDataSummaryLength);
            Price underlyingStockPrice = null;
            ProductSummaryStruct prdSummary = null;
            // This flag is used for setting underlyingStockPrice one time only
            boolean isUnderlyingPriceSetup = false;
            for(int i = 0; i < marketDataSummaryLength; i++)
            {
                if(marketDataSummaryImplForClass[i] != null)
                {
                    if(!isUnderlyingPriceSetup)
                    {
                        underlyingStockPrice = marketDataSummaryImplForClass[i].getUnderlyingPrice();
                        if(underlyingStockPrice == null)
                        {
                            underlyingStockPrice = PriceFactory.create( 0.0 );
                        }
                        classSummaryStructObj.underlyingStockPrice = underlyingStockPrice.toStruct();
                        isUnderlyingPriceSetup = true;
                    }
                    prdSummary = new ProductSummaryStruct();
                    prdSummary.openInterest = marketDataSummaryImplForClass[i].getOpenInterest();
                    prdSummary.recap = marketDataSummaryImplForClass[i].toRecapStruct();
                    summaries.add(prdSummary);
                }
            }
            classSummaryStructObj.summaries = summaries.toArray( new ProductSummaryStruct[summaries.size()] ); 
        }
        else
        {
            classSummaryStructObj.summaries = new ProductSummaryStruct[0];
            //Set it to zero
            classSummaryStructObj.underlyingStockPrice = PriceFactory.create( 0.0 ).toStruct();
        }
        return classSummaryStructObj;
    }
    
    /**
     *@param  sessionName
     *@param  productKey
     *@return  The marketData value
     *@exception  PersistenceException
     */
    private MarketDataSummary getMarketData( String sessionName, int classKey, int productKey ) throws PersistenceException, DataValidationException
    {
        if( productKey == 0 )
        {
            throw ExceptionBuilder.dataValidationException( "MarketDataSummary must be requested for a specific product key!", 0);
        }
        
        return MarketDataSummaryCache.getInstance().findFromCacheByProduct(sessionName, classKey, productKey);    	
    }
    
    /**
     * fire query to database and get all the rows for passed in session.
     * @param sessionName
     * @return
     */
    public MarketDataSummary[] findBySession(String sessionName)
    {
    	MarketDataSummary[] marketDataSummary = null;
    	MarketDataSummaryImpl example = new MarketDataSummaryImpl();
    	ObjectQuery query = new ObjectQuery(example);
    	addToContainer(example);
    	
    	try
        {
            example.setSessionName(sessionName);
            Vector queryResult = query.find();
            marketDataSummary = new MarketDataSummary[queryResult.size()];
            queryResult.copyInto(marketDataSummary);            
        }
        catch (PersistenceException e)
        {
        	Log.information(this, "Persistence exception; marketDataSummary array length = 0");
        	marketDataSummary = new MarketDataSummary[0];
        }
        return marketDataSummary;
    }
    
    /**
     * Cleaning up all cached summary data for all sessions
     */
    public void clearSummaryCache()
    {
        MarketDataSummaryCache.getInstance().clearSummaryCache();
    }
    
}
