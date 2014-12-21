/*
 * Created by IntelliJ IDEA.
 * User: Park
 * Date: Nov 13, 2002
 * Time: 2:08:13 PM
 * Source file: OrderMarketMonitorHome.java
 *
 * PACKAGE: com.cboe.interfaces.domain
 *
 * @author Kevin Park
 * @anthor Mei Wu
 * ------------------------------------------------------------------------
 * Copyright (c) 2002 The Chicago Board Option Exchange
 * ------------------------------------------------------------------------
 */
package com.cboe.interfaces.domain;

import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;
import com.cboe.idl.cmiIntermarketMessages.ExchangeMarketStruct;
import com.cboe.idl.cmiUtil.CboeIdStruct;

public interface OrderMarketMonitorHome {
    public final static String HOME_NAME = "OrderMarketMonitorHome";

    /**
      * create OrderMarketMonitorData when an order is received
      * @param cboeOrderId CboeIdStruct
      * @param  ExchangeMarketStruct[] exchangeMarkets
      */
    public OrderMarketMonitor create(CboeIdStruct cboeOrderId, ExchangeMarketStruct[] exchangeMarkets, Price lastTradePrice, int lastTradeQuantity, char lastTickDirection )
            throws SystemException, TransactionFailedException, DataValidationException;

    /**
     * create OrderMarketMonitorData when an order is received
     * @param cboeOrderId CboeIdStruct
     * @param  ExchangeMarketStruct[] exchangeMarkets
     */
   public OrderMarketMonitor create(CboeIdStruct cboeOrderId, ExchangeMarketStruct[] exchangeMarkets, Price lastTradePrice, int lastTradeQuantity, char lastTickDirection, int productKey )
           throws SystemException, TransactionFailedException, DataValidationException;    
    
    /**
     *
     * @param cboeOrderId
     * @param exchangeMarkets
     * @param eventType
     * @return instance of OrderMarketMonitor
     */
    public OrderMarketMonitor createForPAOrder(CboeIdStruct cboeOrderId, ExchangeMarketStruct[] exchangeMarkets, char eventType)
            throws SystemException, TransactionFailedException, DataValidationException;
    /**
     * create OrderMarketMonitorData when an order is filled
     * @param cboeOrderId CboeIdStruct
     * @param tradeId
     * @param ExchangeMarketStruct[] exchangeMarkets
     */
    public OrderMarketMonitor create(CboeIdStruct cboeOrderId, CboeIdStruct tradeId, ExchangeMarketStruct[] exchangeMarkets, Price lastTradePrice, int lastTradeQuantity, char lastTickDirection )
            throws SystemException, TransactionFailedException, DataValidationException;

     /**
      * update WorstExchangeMarketStruct in OrderMarketMonitorData of receipt Entry,
      * before the orderMarketdata data is removed from the internal cache.
      * @param cboeOrderId CboeIdStruct
      * @param ExchangeMarketStruct[] worstMarkets
      */
     public OrderMarketMonitor update(CboeIdStruct cboeOrderId, ExchangeMarketStruct[] worstMarkets)
             throws SystemException, TransactionFailedException, DataValidationException;

    /**
     * find a unqiue OrderMarketMonitor entry by CboeOrderId for order information at receipt
     * @param cboeOrderId CboeIdStruct
     * @return OrderMarketMonitor
     */
    public OrderMarketMonitor find(CboeIdStruct cboeOrderId)
            throws  TransactionFailedException, NotFoundException, SystemException;

    /**
     * find a unqiue OrderMarketMonitor entry by CboeOrderId for order information at receipt
     * @param cboeOrderId CboeIdStruct
     * @param tradeId CboeIdStruct
     * @return OrderMarketMonitor
     */
    public OrderMarketMonitor find(CboeIdStruct cboeOrderId, CboeIdStruct tradeId)
            throws  TransactionFailedException, NotFoundException, SystemException;
    
    public void deleteOrderMarketMonitoringFromCache(Order order);
}
