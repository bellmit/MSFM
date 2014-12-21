/*
 * Created by IntelliJ IDEA.
 * User: Park
 * Date: Nov 13, 2002
 * Time: 2:07:54 PM
 * Source file: OrderMarketMonitor.java
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

import com.cboe.idl.cmiIntermarketMessages.ExchangeMarketStruct;
import com.cboe.idl.cmiUtil.CboeIdStruct;
import com.cboe.idl.cmiUtil.DateTimeStruct;
import com.cboe.interfaces.domain.marketData.MarketData;
import com.cboe.interfaces.domain.Price;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
/**
 * OrderMarketMonitor Interface declared the access methods for the persisted
 * OrderMarketMonitor Object, the logic unique key is made up with OrderId, TradeId and EntryType.
 */
public interface OrderMarketMonitor
{
    // Declare some constants for the entryType
    static final char ORDER_RECEIPT = 'R';      // new held order received
    static final char ORDER_EXECUTION = 'T';    // trade execution reported
    static final char PA_ORDER_CREATION = 'C';   // PA order is created
    static final char PA_ORDER_EXECUTION = 'E';  // PA order is executed



    /**
     * create OrderMarketMonitorData when an order is received
     * @param cboeOrderId CboeIdStruct
     * @param  ExchangeMarketStruct[] exchangeMarkets
     */
     public OrderMarketMonitor create(CboeIdStruct cboeOrderId, ExchangeMarketStruct[] exchangeMarkets, Price lastTradePrice, int lastTradeVolume, char lastTickDirection ) throws SystemException, TransactionFailedException, DataValidationException;

     /**
      * create OrderMarketMonitorData when an order is filled
      * @param cboeOrderId CboeIdStruct
      * @param tradeId
      * @param ExchangeMarketStruct[] exchangeMarkets
      */
     public OrderMarketMonitor create(CboeIdStruct cboeOrderId, CboeIdStruct tradeId, ExchangeMarketStruct[] exchangeMarkets, Price lastTradePrice, int lastTradeVolume, char lastTickDirection ) throws SystemException, TransactionFailedException, DataValidationException;

     /**
      * update WorstExchangeMarketStruct in OrderMarketMonitorData of receipt Entry,
      * before the orderMarketdata data is removed from the internal cache.
      * @param cboeOrderId CboeIdStruct
      * @param ExchangeMarketStruct[] worstMarkets
      */
     public OrderMarketMonitor update(ExchangeMarketStruct[] worstMarkets) throws SystemException, TransactionFailedException, DataValidationException;

    /**
     * Returns the key of this OrderMarketMonitor entry
     * @return OrderMarketMonitor database Key
     */
    public long getOrderMarketMonitorKey();

    /**
     * Gets the tradeId
     * @return CboeIdStruct tradeId
     */
    public CboeIdStruct getTradeId();

    /**
     * Gets the OrderId
     * @return CboeIdStruct orderId
     */
    public CboeIdStruct getOrderId();


    /**
     * Gets the EntryType
     * @return char EntryType
     */
    public char getEntryType();

    /**
     * Gets the EntryTime
     * @return DateTimeStruct EntryTime
     */
    public DateTimeStruct getEntryTime();
    /**
     * Gets the exchange market data as the order is received for entry type 'R'
     * or as the order is filled for entry type 'T' for entry type 'T'
     * @param isReceipt boolean   (the value will be true if entry type is 'T'
     * @returnExchangeMarketStruct[] the exchange market data as the order is received  or as the order is filled
     * per the current database struct, the length of the return sequence should be 2 only,
     * which contains both NBBO and BBO marketData
     */
    public  ExchangeMarketStruct[] getReceiptOrExecutionExchangeMarketInfo() throws NotFoundException, TransactionFailedException, SystemException;


    /**
     * Gets the worst exchange market data of the OrderMarketMonitorEntry
     * when an order is received, the worst data will be same as the receipt Exchange market data
     * @return ExchangeMarketStruct[] the exchange market data as the order is filled
     * per the current database struct, the length of the return sequence should be 2 only,
     * which contains both NBBO and BBO marketData
     */
    public ExchangeMarketStruct[] getWorstExchangeMarketInfo()throws NotFoundException, TransactionFailedException, SystemException;

    /**
     * Gets the NationalLastSalePrice
     * @return Price
     */
    public Price getNationalLastSalePrice();

    /**
     * Gets the NationalLastSaleVolume
     * @return int
     */
    public int getNationalLastSaleVolume();

    /**
     * Gets the NationalLastSaleExchange
     * @return int
     */
    public String getNationalLastSaleExchange();

    /**
     * Sets the tradeId
     * @param CboeIdStruct tradeId
     */
    public void setTradeId(CboeIdStruct tradeId);


    /**
     * Sets the OrderId
     * @param CboeIdStruct orderId
     */
    public  void setOrderId(CboeIdStruct orderId);

    /**
     * Sets the EntryType
     * @param char EntryType
     */
    public void setEntryType(char entryType);

    /**
     * Sets the EntryTime
     * @param DateTimeStruct EntryTime
     */
    public void setEntryTime(DateTimeStruct EntryTime);

    /**
     * Sets the exchange market data as the order is received for entry type 'R'
     * or as the order is filled for entry type 'T' for entry type 'T'
     * @param ExchangeMarketStruct[] the exchange market data as the order is received  or as the order is filled
     * @param isReceipt boolean
     * per the current database struct, the length of the return sequence should be 2 only,
     * which contains both NBBO and BBO marketData
     */
    public void setReceiptOrExecutionExchangeMarketStruct( ExchangeMarketStruct[] exchangeMarkets, boolean isReceipt) throws DataValidationException, TransactionFailedException, SystemException;

    /**
     * Sets the worst exchange market data of the OrderMarketMonitorEntry
     * when an order is received, the worst data will be same as the receipt Exchange market data
     * @param ExchangeMarketStruct[] the exchange market data as the order is filled
     * per the current database struct, the length of the return sequence should be 2 only,
     * which contains both NBBO and BBO marketData
     */
    public void setWorstExchangeMarketStruct(ExchangeMarketStruct[] exchangeMarkets) throws DataValidationException, TransactionFailedException, SystemException;


    /**
     * Sets the national last sale price.
     *
     */
    public void setNationalLastSalePrice( Price aValue );


    /**
     * Sets the national last sale quantity.
     * @author Matt Sochacki
     */
    public void setNationalLastSaleVolume(int aValue);

    /**
     * Sets the national last sale exchange.
     * @author Matt Sochacki
     */
    public void setNationalLastSaleExchange(String aValue);

}
