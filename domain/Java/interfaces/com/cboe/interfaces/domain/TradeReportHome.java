package com.cboe.interfaces.domain;

// ------------------------------------------------------------------------
//  Source file: TradeReportHome.java
//
//  PACKAGE: com.cboe.interfaces.domain
//
//  @author Magic Magee (building on code from David Wegener)
// ------------------------------------------------------------------------
//  Copyright (c) 1999 The Chicago Board Option
// ------------------------------------------------------------------------


import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;
import com.cboe.idl.cmiUser.ExchangeAcronymStruct;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;
import com.cboe.idl.cmiUtil.DateTimeStruct;
import com.cboe.idl.trade.AtomicCmtaAllocationStruct;
import com.cboe.idl.trade.AtomicTradeAcknowledgmentStruct;
import com.cboe.idl.trade.AtomicTradeBillingStruct;
import com.cboe.idl.trade.TradeReportStruct;
import com.cboe.idl.trade.TradeReportStructV2;
import com.cboe.infrastructureServices.persistenceService.PersistenceException;
import com.cboe.infrastructureServices.uuidService.IdService;

/**
 * This class represents the actual implementation of the
 * TradeReportHome interface.
 *
 * @author Werner Kubitsch
 * @author Craig Murphy
 * @author Magic Magee
 * @author Kevin Park	Added purge session maintenance functionality.
 * @author Eric Fredericks Added query method for querying for reports between two
 *                          timestamps.
 */

public interface TradeReportHome
{
	public static final String HOME_NAME = "TradeReport"; // name used to retrieve TradeReport from HomeFactory

	/**
	 * This method is used to create a TradeReport with respect of heldOrderTrade indicator.
	 * @return com.cboe.interfaces.domain.TradeReport
	 * @param product product being traded
     * @param heldOrderTrade indication if the trade is a held order trade
	 * @param quanity int
	 * @param atomicTrades Vector
	 * @param parent parent trade report
	 */
	TradeReport create(TradingProduct product, char tradeType, char settlementTradeType, boolean heldOrderTrade, int quantity, Price price, List atomicTrades, TradeReport parent, IdService myIdService, boolean isAschronousTrade, Map<Tradable, TradableSnapShot> participantFills) throws SystemException;
	/**
     * This method is used to create a TradeReport with respect of heldOrderTrade indicator.
     * @return com.cboe.interfaces.domain.TradeReport
     * @param product product being traded
     * @param heldOrderTrade indication if the trade is a held order trade
     * @param quanity int
     * @param atomicTrades Vector
     * @param parent parent trade report
     * @param auctionType auctionType
     */
    TradeReport create(TradingProduct product, char tradeType, char settlementTradeType, boolean heldOrderTrade, int quantity, Price price, List atomicTrades, TradeReport parent, IdService myIdService,short auctionType, boolean isAschronousTrade, Map<Tradable, TradableSnapShot> participantFills) throws SystemException;
	/**
	 * This method is used to create a TradeReport
	 * @return com.cboe.interfaces.domain.TradeReport
	 * @param product product being traded
	 * @param quanity int
	 * @param atomicTrades Vector
	 * @param parent parent trade report
	 */
	TradeReport create(TradingProduct product, char tradeType, char settlementTradeType, int quantity, Price price, List atomicTrades, TradeReport parent, IdService myIdService, boolean isAschronousTrade, Map<Tradable, TradableSnapShot> participantFills) throws SystemException;


    /**
     * @param product - tradingProduct for this trade
     * @param quantity int - quantity traded
     * @param price Price - trade price
     * @param manualStockTradeStruct - TradeReportStruct from SAGU
     * @param myIdService - IdService - Corba Service providing Unique Id to be used as Primary Key
     * @return TradeReport - after persisting the Trade, with the TradeId populated
     * @exception SystemException - Thrown by TradeReportImpl.create()
     * @author Mei Wu
     */
    TradeReport create(TradingProduct product, int quantity, Price price, TradeReportStructV2 manualStockTradeStruct, IdService myIdService) throws SystemException;

    /**
     * @param product - tradingProduct for this trade
     * @param quantity int - quantity traded
     * @param price Price - trade price
     * @param manualStockTradeStruct - TradeReportStruct from SAGUI
     * @param myIdService - IdService - Corba Service providing Unique Id to be used as Primary Key
     * @param handlingInstruction - short - Indicates how to handle publishing of reports to outside world.
     * @return TradeReport - after persisting the Trade, with the TradeId populated
     * @exception SystemException - Thrown by TradeReportImpl.create()
     * @author Mei Wu
     */
    TradeReport create(TradingProduct product, int quantity, Price price, TradeReportStructV2 manualStockTradeStruct, IdService myIdService,
                       short handlingInstruction, String remoteSessionName) throws SystemException;
    
    /**
     * @param product - tradingProduct for this trade
     * @param quantity int - quantity traded
     * @param price Price - trade price
     * @param manualStockTradeStruct - TradeReportStruct from SAGUI
     * @param myIdService - IdService - Corba Service providing Unique Id to be used as Primary Key
     * @param handlingInstruction - short - Indicates how to handle publishing of reports to outside world.
     * @param billings - AtomicTradeBillingStruct to hold billing information
     * @return TradeReport - after persisting the Trade, with the TradeId populated
     * @exception SystemException - Thrown by TradeReportImpl.create()
     */
    TradeReport create(TradingProduct product, int quantity, Price price, TradeReportStructV2 manualStockTradeStruct, IdService myIdService,
                       short handlingInstruction, String remoteSessionName,
                       AtomicTradeBillingStruct[] billings) throws SystemException;
    
    /**
     * @param product - tradingProduct for this trade
     * @param quantity int - quantity traded
     * @param price Price - trade price
     * @param manualStockTradeStruct - TradeReportStruct from SAGUI
     * @param myIdService - IdService - Corba Service providing Unique Id to be used as Primary Key
     * @param handlingInstruction - short - Indicates how to handle publishing of reports to outside world.
     * @param billings - AtomicTradeBillingStruct to hold billing information
     * @param tradedSide - indicates trader side whether Buy or Sell
     * @return TradeReport - after persisting the Trade, with the TradeId populated
     * @exception SystemException - Thrown by TradeReportImpl.create()
     */
    TradeReport create(TradingProduct product, int quantity, Price price, TradeReportStructV2 manualStockTradeStruct, IdService myIdService,
                       short handlingInstruction, AtomicTradeBillingStruct[] billings, char tradedSide) throws SystemException;
    
    /**
     * @param product - tradingProduct for this trade
     * @param quantity int - quantity traded
     * @param price Price - trade price
     * @param manualStockTradeStruct - TradeReportStruct from SAGUI
     * @param tradeType - char, tradeType constant ( REGULAR_TRADE, CASH_TRADE, NEXT_DAY_TRADE, SOLD )
     * @param myIdService - IdService - Corba Service providing Unique Id to be used as Primary Key
     * @param isParTrade - boolean - Indicates if it is a fill from PAR 
     * @param tradedSide - char - Indicates which side got traded
     * The last two parameters are introduced to set the clearingFlag to Ignore for the non-traded side 
     * @return TradeReport - after persisting the Trade, with the TradeId populated
     * @exception SystemException - Thrown by TradeReportImpl.create()
     * @author Mei Wu
     */
    public TradeReport create(TradingProduct product, int quantity, Price price, TradeReportStructV2 manualTradeStruct, IdService myIdService, boolean isParTrade, char tradedSide,AtomicCmtaAllocationStruct[] atomicCmtaAllocations) throws SystemException;
    
    /**
     * @param product - tradingProduct for this trade
     * @param quantity int - quantity traded
     * @param price Price - trade price
     * @param manualStockTradeStruct - TradeReportStruct from SAGUI
     * @param tradeType - char, tradeType constant ( REGULAR_TRADE, CASH_TRADE, NEXT_DAY_TRADE, SOLD )
     * @param myIdService - IdService - Corba Service providing Unique Id to be used as Primary Key
     * @param isParTrade - boolean - Indicates if it is a fill from PAR 
     * @param tradedSide - char - Indicates which side got traded
     * The last two parameters are introduced to set the clearingFlag to Ignore for the non-traded side 
     * @return TradeReport - after persisting the Trade, with the TradeId populated
     * @exception SystemException - Thrown by TradeReportImpl.create()
     */
    public TradeReport create(TradingProduct product, int quantity, Price price, TradeReportStructV2 manualTradeStruct, IdService myIdService, boolean isParTrade, char tradedSide,AtomicCmtaAllocationStruct[] atomicCmtaAllocations, char [] clearingTypes, boolean sweepTrade, boolean turnOffPersistant) throws SystemException;
    
    /**
	 * This method is used to find a TradeReport
     * @param int TradeId
	 * @return com.cboe.interfaces.domain.TradeReport
	 */
	TradeReport findByKey( long tradeId ) throws NotFoundException, TransactionFailedException;
    
    /**
     * This method is used to find a TradeReport
     * @param Vector<Long> tradeIdList List of trade ids as <code>Long</code> objects
     * @return com.cboe.interfaces.domain.TradeReport
     * 
     * @author David Dowat
     * @throws PersistenceException 
     */
    TradeReport[] findByKeys( Vector<String> tradeIdList ) throws NotFoundException, TransactionFailedException;
    
    /**
     * Called by TradeService triggered by purge session event
     */
    void purgeExpiredTradeReports() throws TransactionFailedException;

    /**
     * Called by CTM Report Formatting utility.
     *
     * @param startTime (<code>long</code>)
     * @param endTime (<code>long</code>)
     *
     * @return TradeReport[]
     * @exception TransactionFailedException
     *
     * @author Eric Fredericks
     * @date 12/06/2000
     */
    TradeReport[] findTradeReportsBetween(DateTimeStruct beginDateTime,
        DateTimeStruct endDateTime, boolean includeParentReports)
        throws TransactionFailedException;

    /**
     * Called by CTM Report Formatting utility.
     *
     * @param startTime (<code>long</code>)
     * @param endTime (<code>long</code>)
     *
     * @return TradeReportStruct[]
     * @exception TramsactionFailedException
     *
     * @author Keith A. Korecky
     */
    TradeReportStruct[] findUnsentTradeReportsBetween( DateTimeStruct beginDateTime, DateTimeStruct endDateTime, boolean includeParentReports )
        throws TransactionFailedException;

    /**
     * to query trade reports for v2 struct to get settlement information of trade
     * Called by CTM Report Formatting utility.
     * @param beginDateTime
     * @param endDateTime
     * @param includeParentReports
     * @return TradeReport[]
     * @throws TransactionFailedException
     * @author Mei Wu
     */
    TradeReportStructV2[] findUnsentTradeReportsV2Between(DateTimeStruct beginDateTime, DateTimeStruct endDateTime, boolean includeParentReports)
            throws TransactionFailedException;


    /**
     * Called by CTM Report Formatting utility.
     *
     * @param startTime (<code>long</code>)
     * @param endTime (<code>long</code>)
     *
     * @return TradeReport[]
     * @exception TransactionFailedException
     *
     * @author Sudhir Malhotra
     * @date 03/13/2002
     */
    TradeReport[] findTradeReports(ExchangeAcronymStruct broker,
                    ExchangeFirmStruct firm, int productKey,
                    String sessionName, DateTimeStruct beginTime,
                    DateTimeStruct endTime, char buySellInd,
                    boolean activeOnly)
                    throws NotFoundException, AuthorizationException, DataValidationException,
                SystemException, CommunicationException;


    /**
     * Called by CTM Report Formatting utilitiy.
     *
     * @return TradeHistoryHome
     * @throws NotFoundException
     *
     * @author Eric Fredericks
     * @date 12/6/2000
     */

     TradeHistoryHome getTradeHistoryHome() throws NotFoundException;

     public TradeReportAck[] createTradeReportAcks(AtomicTradeAcknowledgmentStruct[] acks) throws TransactionFailedException;

     public TradeReportEntryGroup[] findUnsentTradeReportEntryGroups() throws TransactionFailedException;

    /**
     * create a QuoteTriggerTradeReport
     */
    public QuoteTriggerTradeReport createQuoteTriggerTradeReport(
        TradingProduct product,
        Price triggerPrice,
        Side triggerSide,
        IdService myIdService)
    throws SystemException;

    /**
     *
     * @return The default name of the broker to be reported in MDH
     */
    public String getDefaultBrokerForLastSale();

    /**
     *
     * @return The default name of the contra to be reported in MDH
     */
    public String getDefaultContraForLastSale();

    /**
     *
     * @return The maximum number of brokers that can be saved to broker or contrabroker fields in MDH
     */
    public int getMaxBrokersForLastSale();

    /**
     *
     * @return The default broker exchange acronym to be reported in MDH
     */
    public String getDefaultBrokerExchangeForLastSale();

    /**
     *
     * @return  The default contra exchange acronym to be reported in MDH
     */
    public String getDefaultContraExchangeForLastSale();
    
    /**
     *  UD  
     *  create a QuoteTriggerLegTradeReport
     */
    public QuoteTriggerTradeReport createQuoteTriggerLegTradeReport(
        TradingProduct product,
        int quantity,
        Price price,
        List parties,
        TradeReport parent,
        IdService myIdService)
    throws SystemException;
    
    public TradeReport initLegTradedSpreadTradeReport(TradingProduct product)
    throws SystemException;
    
    public void finalizeLegTradedSpreadTradeReport(TradeReport spreadReport, char tradeType, char settlementTradeType, boolean heldOrderTrade, int quantity, Price price, List atomicTrades, TradeReport parent, IdService myIdService, boolean isAschronousTrade, Map<Tradable, TradableSnapShot> participantFills)
    throws SystemException;
    
    public TradeReport create(TradingProduct product, int quantity, Price price, TradeReportStructV2 theTrade,
            IdService myIdService, AtomicCmtaAllocationStruct[] atomicCmtaAllocations, String outboundVendor, boolean sweepNReturnTrade) throws SystemException;

    /**
     * Called by CTM Report Formatting utility.
     *
     * @param startTime (<code>long</code>)
     * @param endTime (<code>long</code>)
     *
     * @return TradeReport[]
     * @exception TransactionFailedException
     *
     * @author Hemant THakkar
     * @date 09/17/2009
     */
    public TradeReport[] findTradeReportsByClass(DateTimeStruct beginDateTime,
        DateTimeStruct endDateTime, int[] classKeys)
        throws TransactionFailedException;
    
    public TradeReport[] findTradeReportsByExchange(DateTimeStruct beginDateTime,
            DateTimeStruct endDateTime, String[] primaryExchList)  
    	throws TransactionFailedException;
    
    public TradeReport[] findTradeReports(DateTimeStruct beginDateTime,
        DateTimeStruct endDateTime, String[] userids, boolean activeOnly)
        throws TransactionFailedException;

    public TradeReport[] findTradeReportsByProduct(DateTimeStruct beginDateTime,
            DateTimeStruct endDateTime, int[] productKeys)
            throws TransactionFailedException;
    
    public TradeReport[] findChildrenTradeReports(TradeReport parentReport) throws TransactionFailedException;
    
    public TradeReport[] findTradeReportsByOrderId(long theOrderId) throws TransactionFailedException;
}
