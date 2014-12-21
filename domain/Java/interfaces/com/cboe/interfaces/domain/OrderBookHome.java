package com.cboe.interfaces.domain;

import java.util.List;

import com.cboe.exceptions.*;
import com.cboe.idl.cmiProduct.ProductKeysStruct;
/**
 *
 * @version 0.31
 * @author Kevin Park
 */
public interface OrderBookHome {
	public static final String HOME_NAME = "OrderBook"; // name used to retrieve OrderBook from HomeFactory
/**
 * @author Kevin Park
 * @return com.cboe.interfaces.businessServices.OrderBook
 * @param productKey int
 */
OrderBook create(String name, ProductKeysStruct keys) throws OrderBookAlreadyExistsException, DataValidationException;
/**
 * @author Kevin Park
 * @return com.cboe.interfaces.businessServices.OrderBook
 * @param productKey int
 */
OrderBook find(int productKey);
/**
 * Remove this order book from collection
 *
 * @param productKey int
 */
void purge(int productKey);

/* Method for querying the current opening requirement code setting */
public short getOpeningRequirementCode(String sessionName, int classKey)
throws DataValidationException,NotFoundException;

    public boolean getPreOpenEOPFlag();
    
    public boolean getCancelAimOrdersFlag();

    public boolean applyCustomerProrataToMarketOrderOpeningTrade();

/* setting the opening requirement code */
public void setOpeningRequirementCode(String sessionName, int classKey, short anOpeningRequirementCode)
throws DataValidationException;

    /**
     * In the intraday product addition, the trading product creation and order book creation will be in
     * the same transaction. That means the orderBook can not query the database for the trading product
     * during the processing of order book creation.
     *
     * The followinig method will be providing a way to resolve this
     */
    public OrderBook create(String name, TradingProduct aTradingProduct) throws OrderBookAlreadyExistsException, DataValidationException;
    
    public Short getReservedQuantityAllocationStrategyType();
  
    public boolean isBookAccessWithinClassLevelLockCheckNeeded();
  
    public void setBookAccessWithinClassLevelLockCheckNeeded(boolean p_checkIfBookAccessWithinClassLevelLock);
    
    public void reBuildForRollBack(String name, TradingProduct aTradingProduct, boolean isRecoverOrders) throws DataValidationException;
  
    public void populateTradablesInOrderBook(TradingProduct aTradingProduct) throws DataValidationException;
    
    public long getOrderCount();
    
    public List<Order> getAllBookedOrders();
    
    public void refreshOrderCount();
}
