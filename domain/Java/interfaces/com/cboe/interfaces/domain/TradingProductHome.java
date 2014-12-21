package com.cboe.interfaces.domain;

import java.util.List;

import com.cboe.exceptions.NotFoundException;
import com.cboe.idl.cmiProduct.ProductStruct;
import com.cboe.idl.cmiStrategy.StrategyStruct;

/**
 * A home for TradingProduct instances.
 *
 * @author John Wickberg
 */

public interface TradingProductHome {

	/**
	 * Name used to get home from HomeFactory.  Must also be name used for
	 * home in configuration.
	 */
	public static final String HOME_NAME = "TradingProductHome";

	/**
	 * Create a new product.  It is not considered an error to try to create
	 * a product that already exists, the existing product will be returned.
	 *
	 * @param parent trading class that owns this product
	 * @param productDefinition struct containing product definition
	 * @param intradayAdd indicates if add is being done during day
	 * @return created or existing trading product
	 */
	TradingProduct create(TradingClass parent, ProductStruct productDefinition, boolean intradayAdd);

	/**
	 * Create a new strategy product.  It is not considered an error to try to create
	 * a product that already exists, the existing product will be returned.
	 *
	 * @param parent trading class that owns this product
	 * @param strategyDefinition struct containing strategy definition
	 * @param intradayAdd indicates if add is being done during day
	 * @return created or existing trading product
	 */
	TradingProduct create(TradingClass parent, StrategyStruct strategyDefinition, boolean intradayAdd);

	/**
	 * Finds a product given its product key.
	 *
	 * @param productKey key of desired product
	 * @return trading product with the given key
	 * @exception NotFoundException if product is not found
	 */
	TradingProduct findByKey(int productKey) throws NotFoundException;
	
	boolean productExists(int productKey) ;

    /**
	 * Finds a product given its session name and product key.
     * Session name is used if product is not found on business
     * cluster to do a query on the TradingSessionService.
	 *
     * @param sessionName name of session for product
	 * @param productKey key of desired product
	 * @return trading product with the given key
	 * @exception NotFoundException if product is not found
	 */
	TradingProduct findBySessionByKey(String sessionName, int productKey) throws NotFoundException;

	/**
	 * Finds a product given its product key.
	 *
	 * @param productKey key of desired product
	 * @return trading product with the given key
	 * @exception NotFoundException if product is not found
	 */
	TradingProduct findByKey(Integer productKey) throws NotFoundException;

    /**
     * Inform the home that all trading products building has been completed. So it can
     * do some neccessary task if it needs
     */
    void completedBuildTradingProducts();

    /**
     * Find Trading products given the reporting class key
     * @param sessionName
     * @param rptClass
     * @return
     */
    public TradingProduct[] findByReportingClass(String sessionName, int rptClass);
    /**
     * Find Trading product keys for a given session (Trade Server)
     * @return int[]
     */
    public int[] getProductKeys() ;

    /**
     * A count of all products in the cache
     */
    public int getProductCount();

    public int getOpenProductCount(boolean isMaster);
    
    /**
     * Find all trading products for a trading class
     * @param tradingClassKey
     * @return List of all trading products for the given trading class
     */
    public List<TradingProduct> findByTradingClass(int rptClass);
    public void updateFromSync();
}
