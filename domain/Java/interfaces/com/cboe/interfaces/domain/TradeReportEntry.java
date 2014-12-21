package com.cboe.interfaces.domain;

// ------------------------------------------------------------------------
//  Source file: TradeReportEntry.java
//
//  PACKAGE: com.cboe.interfaces.domain
//
//  @author Matt Sochacki
//  @author Eric Fredericks
// ------------------------------------------------------------------------
//  Copyright (c) 2000 The Chicago Board Options Exchange. All rights
//  reserved.
// ------------------------------------------------------------------------
import java.util.Map;

import com.cboe.exceptions.SystemException;
import com.cboe.idl.cmiUser.ExchangeAcronymStruct;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;
import com.cboe.idl.trade.AtomicTradeBillingStruct;
import com.cboe.idl.trade.AtomicTradeStruct;
import com.cboe.idl.trade.AtomicCmtaAllocationStruct;


public interface TradeReportEntry
{

	public static final String TABLE_NAME = "sbt_tradereportentry"; // FIX_ME - I made this name up - Magic

/**
 * Bust quantity specified by reducing the trade quantity.
 *
 * @author Eric Fredericks (updates for CTM reports)
 * @param quantityToBust int
 * @param newSequenceNumber An <code>int</code> representing the new matched sequence number;
 *          provided for CTM.
 */
public void bust(int quantityToBust, int newSequenceNumber, long bustTime);

/**
 * Persistant TradeReportEntry create comment.
 *  grabs the data from its param
 * @author Magic Magee
 * @author Eric Fredericks  (updates for CTM reports)
 * @param name A <code>String</code> representing a name
 * @param trade The <code>AtomicTrade</code> on which this entry is based
 * @param sequenceNumber An <code>int</code> representing the matched sequence number; provided
 *          for CTM.
 */
public TradeReportEntry create(String name, AtomicTrade trade, int sequenceNumber, long time, boolean isAsynchronousTrade, Map<Tradable, TradableSnapShot> participantFills) throws SystemException; // TOADS - I'm not sure on this one

/**
 * Persistant TradeReportEntry create comment.
 * grabs the data from its param
 * @param name A <code>TradeReportStruct</code> representing the BT/EFP
 * @author Sudhir MalhotraR
 */
public TradeReportEntry create( String name, AtomicTradeStruct tradeReportStruct, char tradeType, int sequenceNumber, TradingProduct product, String remoteSessionName) throws SystemException;



public TradeReportEntry create( String name, AtomicTradeStruct aStruct, char tradeType, int sequenceNumber, TradingProduct product, 
        String remoteSessionName, AtomicTradeBillingStruct billing, char tradedSide) throws SystemException;

/**
 * standard getter for atomic trade id
 * @return int - atmoic trade id
 */
public long getAtomicTradeId();

    boolean isActive();
    long getEntryTime();
    char getEntryType();
    long getLastUpdateTime();
    char getLastEntryType();

/**
 * standard getter for buyer
 * @return String - buyer
 * @author Magic Magee
 */
public ExchangeAcronymStruct getBuyer();

public int getBuyerUserKey();

/**
 * standard getter for buyFirm
 * @return String - buyfirm
 * @author Magic Magee
 */
public ExchangeFirmStruct getBuyFirm();

/**
 * standard getter for buy order key
 * @return long - buy order key
 */
public long getBuyOrderId();


/**
 * standard getter for buy side external order  key
 * @return long - buy side external order key
 * Requested by back office group specificly to separate the external cross product order
 * which is filled by leg makret in outside trade engine (such as W_STOCK)
 *
 */
public long getBuyExternalOrderId();

/**
 * standard getter for sell side external order  key
 * @return long - sell side external order key
 * Requested by back office group specificly to separate the external cross product order
 * which is filled by leg makret in outside trade engine (such as W_STOCK)
 *
 */
public long getSellExternalOrderId();

/**
 * standard getter for buy quote key
 * @return int  - buy quote key
 */
public int getBuyQuoteId();

	/**
	* standard getter for quantity
	* @return int - quantity
	* @author Magic Magee
	*/
	public int getQuantity();

	/**
	* standard getter for seller
	* @return String - seller
	* @author Magic Magee
	*/
	public ExchangeAcronymStruct getSeller();

        public int getSellerUserKey();
	
	/**
	* standard getter for sellFirm
	* @return String - sellFirm
	* @author Magic Magee
	*/
	public ExchangeFirmStruct getSellFirm();

	/**
	* standard getter for sell order key
	* @return long - sell order key
	*/
	public long getSellOrderId();

	/**
	* standard getter for sell quote key
	* @return int - sell quote key
	*/
	public int getSellQuoteId();

	// foreign key NEWFIX method above


    /** Provides the matched sequence number associated with this TradeReportEntry.
     *  Note:  A CORBA <code>long</code> is a Java <code>int</code>.
     *
     * @author Eric Fredericks
     * @return An <code>int</code> representing the matched sequence number
     *         associated with this TradeReportEntry.
     */
    public int getMatchedSequenceNumber();

    /**
    * @author Eric Fredericks
    * @return A character representing the buyerOriginType
    */
    public char getBuyerOriginType();

    /**
    * @author Eric Fredericks
    * @return A string containing the buyerCmta
    */
    public ExchangeFirmStruct getBuyerCmta();

    /**
    * @author Eric Fredericks
    * @return A character representing the buyerPositionEffect
    */
    public char getBuyerPositionEffect();

    /**
    * @author Eric Fredericks
    * @return A string containing the buyerSubAccount
    */
    public String getBuyerSubAccount();

    /**
    * @author Eric Fredericks
    * @return A string containing the buyerOptionalData
    */
    public String getBuyerOptionalData();

    /**
    * @author Eric Fredericks
    * @return A character representing the sellerOriginType
    */
    public char getSellerOriginType();

    /**
    * @author Eric Fredericks
    * @return A string containing the sellerCmta
    */
    public ExchangeFirmStruct getSellerCmta();

    /**
    * @author Eric Fredericks
    * @return A character representing the sellerPositionEffect
    */
    public char getSellerPositionEffect();

    /**
    * @author Eric Fredericks
    * @return A string containing the sellerSubAccount
    */
    public String getSellerSubAccount();

    /**
    * @author Eric Fredericks
    * @return A string containing the sellerOptionalData
    */
    public String getSellerOptionalData();
    /**
     * @return Session name
     */
    public String getSessionName();
    public String getBuyerSessionName();
    public String getSellerSessionName();
	/**
	* Get the reinstatable flag for the buy side
	* @return boolean - buy reinstatable
	*/
	public boolean isBuyReinstatable();

	/**
	* Get the reinstatable flag for the sell side
	* @return boolean - sell reinstatable
	*/
	public boolean isSellReinstatable();

	/**
	* standard setter for buyer
	* @author Magic Magee
	* @param aValue String - buyer name
	*/

        /**
        * Get the auctionTrade flag for the buy side
        * @return boolean - buyerAuctionTrade
        */
        public boolean isBuyerAuctionTrade();

        /**
        * Get the auctionTrade flag for the sell side
        * @return boolean - sellerAuctionTrade
        */
        public boolean isSellerAuctionTrade();

	public void setBuyer(ExchangeAcronymStruct aValue);

        public void setBuyerUserKey(int userKey);

	/**
	* standard setter for buyFirm
	* @author Magic Magee
	* @param aValue String - buyer firm name
	*/
	public void setBuyFirm(ExchangeFirmStruct aValue);

	/**
	* standard setter for buy order key
	* @param long - buy order key, s/b 0 if there is no buy order
	*/
	public void setBuyOrderId( long buyOrder );

	/**
	* standard setter for buy quote key
	* @param int - buy quote key, s/b 0 if there is no buy quote
	*/
	public void setBuyQuoteId( int buyQuote );

	/**
	* Sets the reinstatable flag for the buy side
	* @param boolean - reinstatable flag for buy side
	*/
	public void setBuyReinstatable( boolean reinstatable );

	/**
	* standard setter for quantity
	* @author Magic Magee
	* @param aValue int - trade quantity
	*/
	public void setQuantity(int aValue);

	/**
	* standard setter for seller
	* @author Magic Magee
	* @param aValue String - seller name
	*/
	public void setSeller(ExchangeAcronymStruct aValue);

        public void setSellerUserKey(int userKey);

	/**
	* standard setter for sellFirm
	* @author Magic Magee
	* @param aValue String - seller firm name
	*/
	public void setSellFirm(ExchangeFirmStruct aValue);

	/**
	* standard setter for sell order key
	* @param long - sell order key, s/b 0 if there is no sell order
	*/
	public void setSellOrderId( long sellOrder );

	/**
	* standard setter for sell quote key
	* @param int - sell quote key, s/b 0 if there is no sell quote
	*/
	public void setSellQuoteId( int sellQuote );

	/**
	* Sets the reinstatable flag for the sell side
	* @param boolean - reinstatable flag for sell side
	*/
	public void setSellReinstatable( boolean reinstatable );

    /**
    * Setter for matched sequence number
    * @author Eric Fredericks
    * @param number The matched sequence number value to be set.
    */
//    public void setMatchedSequenceNumber(int number);

    /**
    * Used by Tradable object to fill in data for this report entry.
    *
    * @author Eric Fredericks
    * @param originType A character representing the buyerOriginType
    */
    public void setBuyerOriginType(char originType);

    /**
    * Used by Tradable object to fill in data for this report entry.
    *
    * @author Eric Fredericks
    * @param cmta A String containing the buyerCmta
    */
    public void setBuyerCmta(ExchangeFirmStruct cmta);

    /**
    * Used by Tradable object to fill in data for this report entry.
    *
    * @author Eric Fredericks
    * @param positionEffect A character representing the buyerPositionEffect
    */
    public void setBuyerPositionEffect(char positionEffect);

    /**
    * Used by Tradable object to fill in data for this report entry.
    *
    * @author Eric Fredericks
    * @param buyerSubAccount A string containing the buyerSubAccount
    */
    public void setBuyerSubAccount(String subAccount);

    /**
    * Used by Tradable object to fill in data for this report entry.
    *
    * @author Eric Fredericks
    * @param buyerOptionalData A string containing the buyerOptionalData
    */
    public void setBuyerOptionalData(String optionalData);

    /**
    * Used by Tradable object to fill in data for this report entry.
    *
    * @author Eric Fredericks
    * @param  boolean - auctionTrade flag for buy side
    */ 
    public void setBuyerAuctionTrade(boolean auctionTrade); 
 
    /** 
    * Used by Tradable object to fill in data for this report entry. 
    * 
    * @author Eric Fredericks 
    * @param originType A character representing the sellerOriginType
    */
    public void setSellerOriginType(char originType);

    /**
    * Used by Tradable object to fill in data for this report entry.
    *
    * @author Eric Fredericks
    * @param boolean - auctionTrade flag for sell side
    */ 
    public void setSellerAuctionTrade(boolean auctionTrade); 
 
    /** 
    * Used by Tradable object to fill in data for this report entry. 
    * 
    * @author Eric Fredericks 
    * @param cmta A String containing the sellerCmta
    */
    public void setSellerCmta(ExchangeFirmStruct cmta);
    /**
    * Used by Tradable object to fill in data for this report entry.
    *
    * @author Eric Fredericks
    * @param positionEffect A character representing the sellerPositionEffect
    */
    public void setSellerPositionEffect(char positionEffect);

    /**
    * Used by Tradable object to fill in data for this report entry.
    *
    * @author Eric Fredericks
    * @param sellerSubAccount A string containing the sellerSubAccount
    */
    public void setSellerSubAccount(String subAccount);

    /**
    * Used by Tradable object to fill in data for this report entry.
    *
    * @author Eric Fredericks
    * @param sellerOptionalData A string containing the sellerOptionalData
    */
    public void setSellerOptionalData(String optionalData);

    /**
     * @param session name
     */
    public void setSessionName(String aValue);

    public void setInactive();
    public void setLastUpdateTime(long aValue);
    public void setLastEntryType(char aValue);

    public String getSellFirmBranch();
    public int getSellFirmBranchSequenceNumber();
    public String getSellAccount();
    public String getSellCorrespondentId();
    public ExchangeAcronymStruct getSellOriginator();

    public String getBuyFirmBranch();
    public int getBuyFirmBranchSequenceNumber();
    public String getBuyAccount();
    public String getBuyCorrespondentId();
    public ExchangeAcronymStruct getBuyOriginator();


    public void setSellFirmBranch(String aValue);
    public void setSellFirmBranchSequenceNumber(int aValue);
    public void setSellAccount(String aValue);
    public void setSellCorrespondentId(String aValue);
    public void setSellOriginator(ExchangeAcronymStruct aValue);

    public void setBuyFirmBranch(String aValue);
    public void setBuyFirmBranchSequenceNumber(int aValue);
    public void setBuyAccount(String aValue);
    public void setBuyCorrespondentId(String aValue);
    public void setBuyOriginator(ExchangeAcronymStruct aValue);

 	/**
	 * Returns a string representation of this atomic trade.
	 * @return String - the string description of this trade entry
	 */
	public String toString();

	public AtomicTradeStruct toStruct();

	// foreign key NEWFIX method above

    /**
     * update the Trade Report Entry - create appopriate Trade history data,
     * and in-activate the current Trade Report Entry with current update time stamp
     * @param newId
     * @param updateTime
     */
    public void update(int newId, long updateTime);
    
    //new billing fields
    public AtomicTradeBillingStruct getBillingStruct();
    //new CMTA fields
    public AtomicCmtaAllocationStruct getCmtaStruct();
    
    //Instead of using the struct, we are merging the fields into the trade report entry
    //and keep the struct for IDL interface parameter usage only
    //public void setBillingStruct(AtomicTradeBillingStruct billingStruct);
    public void setBuyerBillingType(char p_buyerBillingType);
    public void setExtensions(String p_extensions);
    public void setBuyerClearingType(char p_clearingType);
    public void setSellerClearingType(char p_clearingType);
    public void setRoundLotQuantity(int p_roundLotQuantity);
    public void setSellerBillingType(char p_sellerBillingType);
    public void setBuyAwayExchanges(String p_buyAwayExchanges);
    public void setSellAwayExchanges(String p_sellAwayExchanges);
    public char getBuyerBillingType();
    public String getExtensions();
    public char getBuyerClearingType();
    public char getSellerClearingType();
    public int getRoundLotQuantity();
    public char getSellerBillingType();
    public String getBuyAwayExchanges();
    public String getSellAwayExchanges();
    
    public TradeReport getParentTradeReport();
    public void setParentTradeReport(TradeReport parent);    
    
    
    //Added for CMTA Allocation    
    public void setBuyOrderDate(String orderDate);
    public void setSellOrderDate(String orderDate);
    public void setBuyOrsid(String orsId);
    public void setSellOrsid(String orsId);
    public void setSellSupressionReason(short aValue);
    public void setBuyerSupressionReason(short aValue );
    
    public String getBuyOrderDate();
    public String getSellOrderDate();
    public String getBuyOrsid();
    public String getSellOrsid();
    public short getSellSupressionReason();
    public short getBuyerSupressionReason();
    public void setBuyAwayExchangeAcronym(String awayExchangeAcronym);
    public String getBuyAwayExchangeAcronym();
    public void setSellAwayExchangeAcronym(String awayExchangeAcronym);
    public String getSellAwayExchangeAcronym();
    public void setOutboundVendor(String aValue);
    public String getOutboundVendor();
    public void setSellSideIndicator(char sellSideIndicator);
    public char getSellSideIndicator();
    
    public TradingProduct getTradingProduct();
    
}


