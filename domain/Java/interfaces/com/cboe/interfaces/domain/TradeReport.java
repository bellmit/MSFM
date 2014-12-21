package com.cboe.interfaces.domain;

// ------------------------------------------------------------------------
//  Source file: TradeReport.Java
//
//  PACKAGE: com.cboe.interfaces.domain
//
// @author Magic Magee
// ------------------------------------------------------------------------
//  Copyright (c) 1999 The Chicago Board Option
// ------------------------------------------------------------------------

import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.TransactionFailedException;
import com.cboe.idl.cmiOrder.FilledReportStruct;
import com.cboe.idl.cmiUtil.DateStruct;
import com.cboe.idl.cmiUtil.DateTimeStruct;
import com.cboe.idl.marketData.InternalTickerStruct;
import com.cboe.idl.trade.AtomicTradeStruct;
import com.cboe.idl.trade.BustTradeStruct;
import com.cboe.idl.trade.TradeReportSettlementStruct;
import com.cboe.idl.trade.TradeReportStruct;
import com.cboe.idl.trade.TradeReportStructV2;
import com.cboe.idl.trade.TradeReportStructV3;
import com.cboe.infrastructureServices.uuidService.IdService;
import com.cboe.idl.trade.TradeReportSummaryStruct;

/**
 * This class represents the actual implementation of the
 * TradeReport interface.
 *
 * @version 0.5
 * @author David Wegener
 * @author Magic Magee
 */

public interface TradeReport {

    // Last sale prefix codes for tickers
    // The OPEN and NULL prefixes are not standard and are not sent to OPRA.
	public static final String OPEN_PREFIX = "OPNT";
	public static final String OPEN_LATE_PREFIX = "FTAO";
	public static final String REOPEN_PREFIX = "REOP";
	public static final String SPREAD_PREFIX = "SPRD";
	public static final String CANCEL_PREFIX = "CANC";
	public static final String CANCELOPEN_PREFIX = "CNCO";
    public static final String CANCELLAST_PREFIX = "CNCL";
    public static final String CANCELONLY_PREFIX = "CNOL";

    public static final String OUT_OF_SEQUENCE_PREFIX = "OSEQ";
    public static final String LATE_PREFIX = "LATE";

    // BT EFP PREFIXES per the Message Types from Symbology Document
    // Will be used to indicate to the ticker that the Trade was 'out of
    // sequence'
    public static final String BLOCK_TRADE_PREFIX = "BLKT";
    public static final String EXCHANGE_FOR_PHYSICAL_PREFIX = "EXPH";
    public static final String GWAP_TRADE_PREFIX = "GWAP";

    // define post fix constants
    public static final String LINKAGE_POSTFIX = "LNK"; // for linkage order involved trade

    // blank used so won't be null in database
	public static final String NULL_PREFIX = " ";
	
	   // Marked as Sweep for ISO/ISB contingencies
    public static final String SWEEP_PREFIX = "SWEP";
    public static final String AUCT_PREFIX = "AUCT";
    
    // Hash key prefixed used in TradeReport bust()
    public static final String ORDER_PREFIX = "O";
    public static final String BUY_QUOTE_PREFIX = "B";
    public static final String SELL_QUOTE_PREFIX = "S";
    
    public static final String VARIANCE_STRIP_TRADE_PREFIX = "BNMT";

   /**
    * Completes trade processing.  Order books will be updated and fill reports will be sent.
    *
    * @param updateOrderBook indicates if trade includes tradables from the order book
    * @param unbookedTradable tradable that is not in the order book.  Will be null
    *                         if all tradables are in book.
    */
	public void completeTrade(boolean updateOrderBook, Tradable unbookedTradable, boolean updateTrdable);
	
	public void fill(Map reportMap, boolean updateTradableBidSide, boolean updateTradableAskSide, Map<Tradable, TradableSnapShot> participantFills);	
	
	public void completeTrade(boolean updateOrderBook, Tradable unbookedTradable, boolean updateTrdable, Map<Tradable, TradableSnapShot> participantFills, boolean updateTradableForChildBid, boolean updateTradableForChildAsk);

    /**
     * Completes trade processing.  Order books will be updated and fill reports will be sent.
     * Create fill reports for external trades if any
     * @param updateOrderBook
     * @param unbookedTradable
     * @param generatedLegTradeReports TradeReportStructV2[]:create fill reports for external trades
     * @param generatedLegAtomicBuyer ArrayList[]: Buy participants of the atomic trade
     * @param generatedLegAtomicSeller ArrayList[]: Sell participants of the atomic trade
     * the items of arraies map each other
     */

    public void completeTrade(boolean updateOrderBook, Tradable unbookedTradable,
            TradeReportStructV2[] generatedLegTradeReports,
            Side[] generatedLegSides,
            ArrayList[] generatedLegAtomicBuyer,
            ArrayList[] generatedLegAtomicSeller, boolean updateTrdable, 
            Map<Tradable, TradableSnapShot> participantFills, boolean updateTradableBidChild, boolean updateTradableAskChild);
    
	public void completeTrade(boolean updateOrderBook, Tradable unbookedTradable,
            Map<Tradable, FilledReportStruct[]> remoteLegFills, boolean updateTrdable, 
            Map<Tradable, TradableSnapShot> participantFills, boolean updateTradableForChildBid, boolean updateTradableForChildAsk) throws DataValidationException;    
    
    
	
    public void manualTradeBust() ;
     
        
     
    
    /**
	* method that allows busting the atomic trades within a trade
    * @author Eric Fredericks (updates for CTM reports - added idService)
    * @return  Hashtable  String order key or modified String quote key
    *                     (String quote key + "B" for buy or "S" for sell)
    *                     and bust report struct pair
	* @exception DataValidationException
	*/
    public void bust(BustTradeStruct[] tradesToBust, IdService myIdService, boolean parTradeMMSideBust, char parTradeMMSide)
        throws DataValidationException, SystemException, TransactionFailedException;

   
	/**
	 * Finds a trade report entry given an atomicTradeId.
	 *
	 * @param int AtomicTradeId
	 * @returns TradeReportEntry
	 * @exception DataValidationException - when the atomicTradeId does not match a TradeReportEntry
	 */
	
    public TradeReportEntry findTradeReportEntry( long atomicTradeId ) throws DataValidationException;
	/**
	* standard getter for TradeReportEntries[]
	* @return TradeReportEntries[] entries - the Report detail entries
	*/
	public TradeReportEntry[] getEntries();
    /**
     * standard getter for TradeReportEntries[]
     * @param boolean activeOnly - if true only active entries are returned.
     * @return TradeReportEntries[] entries - the Report detail entries
     */
    public TradeReportEntry[] getEntries(boolean activeOnly);	
	/**
	* standard getter for price
	* @return Price price - the price of this trade
	*/
	public Price getPrice();
	/**
	* standard getter for product key
	* @return int product - the key of the product
	*/
	public int getProduct();
	/**
	* standard getter for quantity
	* @return int quantity - the amount of the trade
	*/
	public int getQuantity();
	/**
	* standard getter for time
	* @return long time - the time signature of the trade
	*/
	public long getTime();
	/**
	* standard getter for trade source
	* @return String tradeSource - the source identifier of the trade
	*/
	public String getTradeSource();
	/**
	* determines if the trade is bustable
	* @return boolean
	*/
	public boolean isBustable();
	/**
	 * converter to TradeReportStruct
	 * @return TradeReportStruct
	 */
	public TradeReportStruct toStruct(boolean activeOnly);
    /**
     * converter to TradeReportStructV2
     * @return TradeReportStructV2
     * @author Crystal Chen
     */
    public TradeReportStructV2 toStructV2(boolean activeOnly);
    
    public TradeReportStructV3 toStructV3(boolean activeOnly);
    
	/**
	* converter to TickerStruct (was LastSaleStruct)
	* @return TickerStruct
	*/
	public InternalTickerStruct toTickerStruct();

    public void setTradeType(char aValue);
    public char getTradeType();
    public long getTradeId();

    public boolean isParentReport();
	public TradingProduct getTradingProduct();
	public boolean isReportForLocalTradeServer();

    /**
     * change related to quote trigger processing
     */
    public ParticipantList getBidList();
    public ParticipantList getAskList();
    public void setBidList(ParticipantList bidList);
    public void setAskList(ParticipantList askList);

    public boolean isStrategyReport();

    public void update(AtomicTradeStruct [] tradesToUpdate, IdService myIdService)  throws DataValidationException, SystemException, TransactionFailedException;

    // change for stock done order entry, which is also called as large trade
    public DateStruct getSettlementDate();
    public boolean isAsOfFlag();
    public DateTimeStruct getTransactionTime();
    public void setSettlementDate(DateStruct settlementDate) throws SystemException;
    public void setAsOfFlag(boolean flag);
    /*
     * This transaction timestamp is to record the time
     * when the manual trade really occurs in trading floor or outside system
     * and should be input from SAGUI, server just pass&save it
     * so it might be meaningless for other types of tradeReports
     */
    public void setTransactionTime(DateTimeStruct transactionTime);
    public TradeReportSettlementStruct getTradeReportSettlementStruct();
    public void setTradeReportSettlement(TradeReportSettlementStruct tradeReportSettlementStruct) throws SystemException;
    
    public String getExtensions();
    public void setExtensions(String extensions);
    
    //temporarily hold the lastest added child trade reports, if any, otherwise null
    public void setLastestAddedChildTradeReports(TradeReport[] childReports);
    public void addChildTradeReport(TradeReport childReport);
    public TradeReport[] getLastestAddedChildTradeReports();
    public boolean isActive();
    public TradeReportSummaryStruct toSummaryStruct();
    public TradeReport getParentReport();
    public int getClassKey();
    public Vector getChildReports();
    public TradeReportHome getTradeReportHome();
}
