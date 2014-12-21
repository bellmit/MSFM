package com.cboe.domain.marketDataSummary;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.cboe.exceptions.NotFoundException;
import com.cboe.infrastructureServices.foundationFramework.HomeFactory;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.FatalFoundationFrameworkException;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.domain.marketDataSummary.MarketDataSummary;
import com.cboe.interfaces.domain.marketDataSummary.MarketDataSummaryHome;


/**
 * This class is used to build cache of all products in Mkt_data_summary table.
 * @author Cognizant Technology Solutions
 * @created Nov 1, 2007
 *
 */
public class MarketDataSummaryCache implements Runnable{

    /*
     * The cache will be Map of Map of Maps, 
     * where first Map Key will be session Name and the Value will be a 
     * Map with Key as ClassKey and Value will be another 
     * Map with Key as ProductKey and value as MarketDataSummaryImpl. 
     */
     
    private Map<String, Map<Integer, Map<Integer, MarketDataSummary>>> marketDataSummaryCacheBySession = new HashMap<String, Map<Integer, Map<Integer, MarketDataSummary>>>();
    
    String sessionList[] = null;
    
    private static MarketDataSummaryCache marketDataSummaryCache;
    
    /**
     * This flag is used to decide whether cache needs to be rebuild before 
     * any write/read operation
     */
    private volatile boolean needToRebuild = true;
    
    private MarketDataSummaryCache() {}
    
    public static MarketDataSummaryCache getInstance()
    {
      if(marketDataSummaryCache == null)
      {
    	  marketDataSummaryCache = new MarketDataSummaryCache();
      }
      return marketDataSummaryCache;
    }

    /**
     *  Gets the marketDataSummaryHome 
     *
     *@return  marketDataSummaryHome object
     *@exception  NotFoundException
     */
    public MarketDataSummaryHome getMarketDataSummaryHome() 
    {
        try
        {
            MarketDataSummaryHome marketDataSummaryHome = ( MarketDataSummaryHome ) HomeFactory.getInstance().findHome( MarketDataSummaryHome.HOME_NAME );
            if( marketDataSummaryHome == null )
            {
                throw new FatalFoundationFrameworkException("Failed to find home named MarketDataSummaryHome, configuration problem");
            }
            return marketDataSummaryHome;
        }
        catch( CBOELoggableException ex )
        {
            throw new FatalFoundationFrameworkException("Failed to find home named MarketDataSummaryHome, configuration problem");
        }
    }
    
    /**
     * This method queries database table Mkt_data_summary & recap for passed in session & puts in the cache  
     * @param sessionName
     */
    public void populateCache(String sessionName)
    {
        if(sessionName != null)
        {
            MarketDataSummary[] marketDataSummaryBySession = getMarketDataSummaryHome().findBySession(sessionName);
            if(marketDataSummaryBySession != null)
            {
                Log.information("Total number of records in database : "+marketDataSummaryBySession.length);
                if (marketDataSummaryBySession.length > 0)
                {
                    Map <Integer, Map<Integer, MarketDataSummary>> marketDataSummaryCacheByClass = new HashMap<Integer, Map<Integer, MarketDataSummary>>();
                    for(int j = 0; j < marketDataSummaryBySession.length; j++)
                    {
                        int classKey = marketDataSummaryBySession[j].getClassKey();
                        int productKey = marketDataSummaryBySession[j].getProductKey();
                        if(marketDataSummaryCacheByClass.get(classKey)==null)
                        {
                            marketDataSummaryCacheByClass.put(classKey, new HashMap<Integer, MarketDataSummary>());
                        }
                        marketDataSummaryCacheByClass.get(classKey).put(productKey, marketDataSummaryBySession[j]);
                    }
                    marketDataSummaryCacheBySession.put(sessionName, marketDataSummaryCacheByClass);
                    Log.information("MarketDataSummary cache is successfully built.");
                }
                else
                {
                    Log.information("No MarketDataSummary found for session " + sessionName);
                }
            }
        }
    }
    
    /**
     * This method queries database table Mkt_data_summary & recap for all 
     * sessions & puts in the cache  
     * @param sessionName
     */
    public void populateCacheForAllSessions()
    {
        String [] sessionList = getSessionList();
        for (int i = 0; sessionList != null && i < sessionList.length; i++) {
            Log.information("Calling populateCache method for session " + sessionList[i]);
            populateCache(sessionList[i]);
        }        
        this.needToRebuild = false;
    }
    
    /**
     * Find MarketDataSummary object from provided class key and product key.
     * @param sessionName
     * @param classKey
     * @param productKey
     * @return MarketDataSummary object
     */
    public MarketDataSummaryImpl findFromCacheByProduct(String sessionName, int classKey, int productKey)
    {
        mayBePopulateCache();
        MarketDataSummaryImpl marketDataSummaryImpl = null;
        MarketDataSummary marketDataSummary = null;
        if(sessionName != null)
        {
            if(marketDataSummaryCacheBySession != null)
            {
                // Get maps of all classes for passed in session name.
                Map<Integer, Map<Integer, MarketDataSummary>> marketDataSummaryCacheByClass = 
                                                marketDataSummaryCacheBySession.get(sessionName);
                if(marketDataSummaryCacheByClass!=null)
                {
                    // Get map of passed in class key.
                    Map<Integer, MarketDataSummary> marketDataSummaryCacheByProduct =
                                                    marketDataSummaryCacheByClass.get(classKey);
                    if(marketDataSummaryCacheByProduct!=null)
                    {
                        marketDataSummary = marketDataSummaryCacheByProduct.get(productKey);
                        if( marketDataSummary instanceof MarketDataSummaryImpl)
                        {
                            marketDataSummaryImpl = (MarketDataSummaryImpl)marketDataSummary;
                        }
                    }
                }
            }
        }
        return marketDataSummaryImpl;
    }
    
    /**
     * Find hashmap of all products of a particular class and session.
     * @param sessionName
     * @param classKey
     * @return MarketDataSummaryImpl[]
     */
    public MarketDataSummaryImpl[]  findFromCacheByClass(String sessionName, int classKey)
    {
       // Get maps of all classes for passed in session name.
        Map<Integer, Map<Integer, MarketDataSummary>> marketDataSummaryCacheByClass = marketDataSummaryCacheBySession.get(sessionName);
        if(marketDataSummaryCacheByClass != null)
        {
            mayBePopulateCache();
            // Get map of passed in class key.
            Map<Integer, MarketDataSummary> marketDataSummaryCacheByProduct = marketDataSummaryCacheByClass.get(classKey);
            if(marketDataSummaryCacheByProduct != null)
            {
                //Convert map to array of MarketDataSummary objects.
                Collection<MarketDataSummary> marketDataSummaryObjectsByProduct = marketDataSummaryCacheByProduct.values();
                return marketDataSummaryObjectsByProduct.toArray(new MarketDataSummaryImpl[marketDataSummaryObjectsByProduct.size()]);
            }
            else
            {
                return new MarketDataSummaryImpl[0];
            }
        }
        else
        {
            return new MarketDataSummaryImpl[0];
        }
    }
    
    /**
     * Update cache with product and corresponding MarketDataSummary object.
     * @param sessionName
     * @param classKey
     * @param productKey
     * @param marketDataSummaryImpl
     * @return MarketDataSummary object
     */
    public void updateCache(String sessionName, int classKey, int productKey, MarketDataSummaryImpl marketDataSummaryImpl)
    {
    	if(marketDataSummaryImpl != null)
    	{
    	    mayBePopulateCache();
    		if(marketDataSummaryCacheBySession.get(sessionName) == null)
    		{
    			marketDataSummaryCacheBySession.put(sessionName, new HashMap<Integer, Map<Integer, MarketDataSummary>>());
    		}
    		if(marketDataSummaryCacheBySession.get(sessionName).get(classKey) == null)
    		{
    			marketDataSummaryCacheBySession.get(sessionName).put(classKey, new HashMap<Integer, MarketDataSummary>());
    		}
    		marketDataSummaryCacheBySession.get(sessionName).get(classKey).put(productKey, marketDataSummaryImpl);
    	}    	    	
    }
    
    public void run()
    {
        populateCacheForAllSessions();
    }
    
    public void setSessionList(final String[] sessionListVal)
    {
        this.sessionList = sessionListVal;
    }
    
    public String[] getSessionList()
    {
        return this.sessionList;
    }
    /**
     * Cleaning up all cached summary data for all sessions
     */
    public void clearSummaryCache()
    {
        this.marketDataSummaryCacheBySession.clear();
        this.needToRebuild = true;
        Log.information("Cleared Market Data Summary cache");
    }
    
    /**
     * Cleaning up all cached summary data for all sessions
     */
    public void mayBePopulateCache()
    {
       if(this.needToRebuild)
       {
           Log.information("Populating Market Data Summary Cache");
           populateCacheForAllSessions();
       }
    }
}
