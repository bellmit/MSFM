package com.cboe.interfaces.domain.marketDataSummary;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;
import com.cboe.idl.marketDataSummary.ClassSummaryStruct;
/**
 *@author  David Hoag
 *@created  September 19, 2001
 */
public interface MarketDataSummaryHome
{
    /**
     *  Name of the home.
     */
    public final static String HOME_NAME = "MarketDataSummaryHome";
    /**
     *  Creates a new market data.
     *
     *@param  productKey key of product
     *@param  classKey key of product's class
     *@param  sessionName
     *@return  created market data
     *@exception  TransactionFailedException
     */
    public MarketDataSummary create( String sessionName, int productKey, int classKey ) throws TransactionFailedException;
    /**
     *  Returns MarketDataSummary for requested product.
     *
     *@param  sessionName
     *@param  classKey
     *@param  productKey
     *@return
     *@exception  NotFoundException
     */
    public MarketDataSummary findByProduct( String sessionName, int classKey, int productKey ) throws NotFoundException;
    /**
     *@param  sessionName
     *@param  classKeys
     *@return
     *@exception  SystemException
     */
    public MarketDataSummary[] findByClasses( final String sessionName, final int[] classKeys ) throws SystemException;
    
    /**
     * 
     * @author Cognizant Technology Solutions
     * 
     * @param sessionName
     * @param classKeys
     * @return
     * @throws SystemException
     */
    public ClassSummaryStruct[] findClassSummary( final String sessionName, final int [] classKeys) 
            throws SystemException;
    
    /**
     * 
     * @param sessionName
     * @return MarketDataSummary[]
     */
    public MarketDataSummary[] findBySession(String sessionName);
    
    /**
     * Cleaning up all cached summary data for all sessions
     */
    public void clearSummaryCache();
}
