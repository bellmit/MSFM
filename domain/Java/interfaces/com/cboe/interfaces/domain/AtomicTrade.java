package com.cboe.interfaces.domain;

// ------------------------------------------------------------------------
//  Source file: AtomicTrade.java
//
//  PACKAGE: com.cboe.domain.util
//
//  @author David Wegener, Magic Magee
// ------------------------------------------------------------------------
//  Copyright (c) 1999 The Chicago Board Option
// ------------------------------------------------------------------------

import java.util.Map;

import com.cboe.idl.cmiUser.ExchangeAcronymStruct;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;

/**
 * This class represents the lowest level of a trade
 * between two participants.  It knows about the participants,
 * and the quantity traded between them.
 *
 * @version 1.3
 * @author David Wegener
 */
public interface AtomicTrade
{
	/**
	 * This method returns the buyers name.
	 * @return String name of buyer
	 */
	public ExchangeAcronymStruct getBuyer();

	/**
	 * This method returns the buyers firm.
	 * @return String name of firm
	 */
	public ExchangeFirmStruct getBuyFirm();

	/**
	 * This method returns the buyer's order key
	 * @return long OrderId, order key will be zero if the buyer was a quote
	 */
	public long getBuyOrderId();

	/**
	 * This method returns the buyer's quote key
	 * @return int QuoteId, quote key will be zero if the buyer was an order
	 */
	public int getBuyQuoteId();

	/**
	 * This method returns the total trade quantity.
	 * @return int trade quantity
	 */
	public int getQuantity();

	/**
	 * This method returns the sellers name.
	 * @return String name of seller
	 */
	public ExchangeAcronymStruct getSeller();

	/**
	 * This method returns the sellers firm.
	 * @return String name of firm
	 */
	public ExchangeFirmStruct getSellFirm();

	/**
	 * This method returns the seller's order key
	 * @return long OrderId, order key will be zero if the seller was a quote
	 */
	public long getSellOrderId();

	/**
	 * This method returns the seller's quote key
	 * @return int QuoteId, quote key will be zero if the seller was a quote
	 */
	public int getSellQuoteId();

	/**
	 * Determines if the buyer's order is reinstatable
	 * @return boolean
	 */
	public boolean isBuyerReinstatable();

	/**
	 * Determines if the seller's order is reinstatable
	 * @return boolean
	 */
	public boolean isSellerReinstatable();
    
    public TradingProduct getTradingProduct();
    
    public void setQuoteTriggerTrade(boolean isQuoteTriggerTrade);
    
    public void setIsQuoteTriggerBuySide(boolean isQuoteTriggerBuySide);
    
    public void setQuoteTriggerType(short qtType);

 /**
  * Method to populate TradeReportEntry data
  *
  * @author Eric Fredericks
  * @param reportEntry An object implementing the TradeReportEntry interface
  */
  public void populateTradeReportEntryData(TradeReportEntry reportEntry, boolean isAsynchronousTrade, Map<Tradable, TradableSnapShot> participantFills);
  
  public ParticipantItem getBuyParticipant();
  
  public ParticipantItem getSellParticipant();

  /**
   * Methods to get user-id of the buyer and seller
   */
  public String getBuyerUserId();
  public String getSellerUserId();
  
 }
