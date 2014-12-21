package com.cboe.interfaces.domain.marketData;

import com.cboe.exceptions.*;
import com.cboe.idl.cmiMarketData.ExchangeIndicatorStruct;
import com.cboe.idl.exchange.ExchangeClassIndicatorStruct;
import com.cboe.interfaces.domain.Price;

/**
 * A home for the market data.
 *
 * @author John Wickberg
 */
public interface MarketDataHome
{
    void updateExchangeIndicatorCache(String sessionName, ExchangeClassIndicatorStruct indicator);


	/**
	 * Name of the home.
	 */
	public static final String HOME_NAME = "MarketDataHome";

/**
 * Creates a market data container for a product.
 *
 * @param String sessionName
 * @param productKey key of product
 * @param classKey key of product's class
 * @param closePrice close price of the product.
 */
MarketData create(String sessionName, int productKey, int classKey, Price closePrice) throws AlreadyExistsException, TransactionFailedException;
/**
 * Creates a market data container for a product.
 *
 * @param String sessionName
 * @param productKey key of product
 * @param classKey key of product's class
 */
MarketData create(String sessionName, int productKey, int classKey) throws AlreadyExistsException, TransactionFailedException;

MarketData create(String sessionName, int productKey, int classKey, int reportingClassKey) throws AlreadyExistsException, TransactionFailedException;
/**
 * Finds all market data for the requested class.
 *
 * @param String sessionName
 * @param classKey key of requested class
 * @return market data for all products of the class
 */
MarketData[] findByClass(String sessionName, int classKey) throws NotFoundException;
/**
 * Finds the market data for a product.
 * @param String sessionName
 * @param productKey requested product
 */
MarketData findByProduct(String sessionName, int productKey) throws NotFoundException;
/**
*
* @param sessionName
* @param rptClassKey
* @return
* @throws NotFoundException
*/
MarketData[] findByReportingClass(String sessionName, int rptClassKey) throws NotFoundException;

/**
 * Returns the LinkageClassGateHome for purposes of finding an individual gate
 *
 * @author Mark Wolters
 * @return LinkageClassGateHome
 */
com.cboe.interfaces.domain.linkageClassGate.LinkageClassGateHome getLinkageClassGateHome();

}


