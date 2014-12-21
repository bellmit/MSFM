package com.cboe.interfaces.domain.marketDataSummary;

import com.cboe.idl.cmiMarketData.RecapStruct;
import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.domain.marketData.Recap;
/**
 *@author  David Hoag
 *@created  September 19, 2001
 */
public interface MarketDataSummary
{
    /**
     *  Creates a new instance.
     *
     *@param  sessionName
     *@param  productKey key of product
     *@param  classKey key of product's class
     */
    public void create( String sessionName, int productKey, int classKey );
    /**
     *  Getter for sessionName.
     *
     *@return  The sessionName value
     */
    public String getSessionName();
    /**
     *  Getter for class key.
     *
     *@return  The classKey value
     */
    public int getClassKey();
    /**
     *@return  The openInterest value
     */
    public int getOpenInterest();
    /**
     *  Getter for product key.
     *
     *@return  The productKey value
     */
    public int getProductKey();
    /**
     *  Getter for recap.
     *
     *@return  The recap value
     */
    public Recap getRecap();
    /**
     *  Creates underlying recap struct. Product keys is not completely filled
     *  in, don't want to make call to product service from the domain layer.
     *
     *@return
     */
    public RecapStruct toRecapStruct();
    /**
     *  Updates underlying recap.
     *
     *@param  newOpenInterest
     *@param  recapUpdate
     *@param  underlying
     */
    public void updateInterestRecapPrice( int newOpenInterest, RecapStruct recapUpdate, PriceStruct underlying );
    
    /**
     *  Updates underlying recap.
     *
     *@param  newOpenInterest
     *@param  recapUpdate
     *@param  underlying
     *@param  closingSuffix
     *@param  prevClosingSuffix
     */
    public void updateInterestRecapPrice( int newOpenInterest, RecapStruct recapUpdate, PriceStruct underlying, String closingSuffix, String prevClosingSuffix );
    /**
     *  Gets the underlyingPrice attribute of the MarketDataSummary object
     *
     *@return  The underlyingPrice value
     */
    public Price getUnderlyingPrice();
}
