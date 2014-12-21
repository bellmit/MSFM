package com.cboe.interfaces.domain;

// ------------------------------------------------------------------------
//  Source file: ParticipantItem.java
//
//  PACKAGE: com.cboe.domain.util
//
//  @author Matt Sochacki
// ------------------------------------------------------------------------
//  Copyright (c) 1999 The Chicago Board Option
// ------------------------------------------------------------------------

import com.cboe.interfaces.domain.*;
import com.cboe.idl.cmiOrder.*;
import com.cboe.idl.cmiQuote.*;
import com.cboe.idl.cmiUser.ExchangeAcronymStruct;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;
import com.cboe.exceptions.*;
import com.cboe.util.*;

import java.util.*;

/**
 * This class is used to hold a single participant in a trade.  It will
 * contain the Tradable (order or quote) for the participant, the quantity, the price, and the contra parties.
 * @version 1.2
 * @author David Wegener, Magic Magee
 */
public interface ParticipantItem {

	/**
	 * this method adds an atomicTrade to the participant item
 	 * @author David Wegener, Magic Magee
	 * @param anAtomicTrade AtomicTrade - the AtomicTrade item to add
	 */
	public void addAtomicTrade(AtomicTrade anAtomicTrade, ParticipantItem contraParty);

	/**
	 * This method adds a contra party to the participant item.
	 * @param nextContra the contra party participant item to add.
	 * @author David Wegener
	 */
	public void addContra(ParticipantItem nextContra);

	/**
	 * This method asks the Tradable to fill itself
	 * @author Magic Magee
         *
         * @param int productKey: the key of a product for which a FilledReportStruct is going to
         *        be built.
         *        Note: this product may be different from the product of the participantTradable.
	 * @param aPrice Price price to fill with
         * @param HashMap reportMap: this method is responsible to populate the reportMap
         *        with FilledReportStruct keyed by the participantTradable.
	 */
	public void fill(int productKey, long tradeId, Price aPrice, Map reportMap,
			boolean updateTradable, TradableSnapShot captuerdFillData);

	/**
	 * This method returns an array of ContraPartyStructs
	 * that the participant is trading with.
	 * @author David Wegener, Magic Magee
	 * @return ContraPartyStruct[] containing contra parties for
	 * this participant.
	 */
	public ContraPartyStruct[] getContraParties();

	/**
	 * This method returns the initial trade price of the particapant tradable.
	 * @author Magic Magee
	 * @return Price
	 */
	public Price getInitialTradePrice();

	/**
	 * This method will return the maximum quantity
	 * allowed to be allocated to this participant.
	 * @author Magic Magee
	 * @return int value of max quantity to assign
	 */
	public int getMaxParticipantQuantity();

	/**
	 * This method returns the firm associated with this participant
	 * @author David Wegener
	 * @return String firm of participant
	 */
	public ExchangeFirmStruct getParticipantFirm();

	/**
	 * This method returns the name associated with this participant
	 * @author David Wegener
	 * @return String name of participant
	 */
	public ExchangeAcronymStruct getParticipantAcronym();

	/**
	 * Gets the participantQuantity property value.
	 * @return The participantQuantity property value.
	 * @author David Wegener
	 */
	public int getParticipantQuantity();

	/**
	 * Gets the participantTradable property value.
	 * @return The participantTradable property value.
	 * @author David Wegener
	 */
	public Tradable getParticipantTradable();

	/**
	 * This method returns the Price of the Tradable.
	 * @return Price
	 * @author David Wegener
	 */
	public Price getPrice();

	/**
	 * This method returns the productKey of the Tradable.
	 * @return Integer
	 * @author David Wegener
	 */
	public Integer getProductKey();

	/**
	 * This method returns the quantity of the participant Tradable.
	 * @return int
	 * @author David Wegener
	 */
	public int getQuantity();
    
    public int getQuantity (boolean includeReserved);

	/**
	 * This method returns the side (buy or sell) of the particpant tradable.
	 * @return Side
	 * @author David Wegener
	 */
	public Side getSide();

    /**
     * This method returns the next node in this linked list (could be null)
     * @author David Wegener
     * @return ParticipantItem nextParticipant - next link in chain
     */
    public ParticipantItem  getNextParticipant();


	/**
	 * This method will attempt to increase the participant quantity
	 * by the amount specified.  If the value exceeds the max for this
	 * participant, the quantity is set to the max.  The amount of
	 * the increase is returned.
	 * @author David Wegener, Magic Magee
	 * @param quantity quantity to increase the participant quantity by. If
	 * this quantity exceeds the maximum allowed for this participant
	 * the quantity is set to the maximum.
	 * @return int is the actual increase in the assigned quantity
	 */
	public int increaseParticipantQuantity(int quantity);

	/**
	 * This method finds out if the tradable is a buy Side tradable
	 * @author Magic Magee
	 * @return boolean
	 */
	public boolean isBuySide();

	/**
	 * This method finds out if the tradable is a sell side tradable
	 * @author Magic Magee
	 * @return boolean
	 */
	public boolean isSellSide();

    /**
     * This method links a new participant into the chain
     * @author David Wegener, Magic Magee
     * @param ParticipantItem - the new node in the chain
     * @return ParticipantItem - the former end
     */
    public ParticipantItem linkParticipant(ParticipantItem aParticipant);

	/**
	 * This method sets the initial trade price for the tradable.
	 * @author Magic Magee
	 * @param tradePrice Price
	 */
	public void setInitialTradePrice(Price tradePrice);

	/**
	 * @author David Wegener
	 * @param quantity quantity to assign to this participant. If
	 * this quantity exceeds the maximum allowed for this participant
	 * the quantity is set to the maximum.
	 */
	public void setParticipantQuantity(int quantity);

   /**
   * This method takes an object that implements TradeReportEntry and
   * forwards that object to the tradable object which populates it with
   * appropriate data pertaining to the trade.
   *
   * @author Eric Fredericks
   * @param reportEntry The trade report entry to be filled with data
   */
  public void populateTradeReportEntryData(TradeReportEntry reportEntry);

    /**
     * Inform the participant list that it is involved in a quote trigger
     */
    public void involvedInQuoteTrigger();

    /**
     * Inform the participant list that it is released in a quote trigger
     */
    public void releasedFromQuoteTrigger();

    /**
     * Fill a pending trade. From a tradable's point of view, a pending trade is a trade, in which
     *
     * 1. the trade price is known
     * 2. the quantity allocated to the tradable is also known
     * 3. but the contra parties to the tradable is not known.
     *
     */
    public void fillPendingTrade(int product, long tradeId, Price aPrice);

    /**
     * complete a pending trade
     */
    public void completePendingTrade(int product, long tradeId, Price aPrice, HashMap reportMap);

    /**
     * consolidate pending trade quantity
     */
    public void consolidateTradeQuantity(int aQuantity);

    /**
     * if this participant should be cleared like a Q-order based on its origin
     *
     */
    public boolean clearsLikeQuote();

    /**
     * sets the trade context like TradeContext.AUCTION trade.
     *
     */
    public void setTradeContext(short tradeContext);

    /**
     * get the trade context like auction trade.
     *
     */
    public short getTradeContext();

    //AllowedQuantity is same as tradable.QuantityAllowed generally
    //In the case of SAL Trading, allowedQuantity will be decided based on the cap size
    public void setAllowedQuantity(int allowedQuantityIn);
    public int getAllowedQuantity();
    public int getAllowedQuantity(boolean includeReserved);

    //if this SAL PI is already allocated, skip it.
    public void markItemIsAllocated(boolean flag);
    public boolean isItemAllocated();

    public void markItemAsDPM(boolean flag);
    public boolean isItemDPM();

    public void markItemAsEDPM(boolean flag);
    public boolean isItemEDPM();

    public void markItemAsPreferred(boolean flag);//ie. preferred DPM/eDPM/MM
    public boolean isItemPreferred();
    
    //New billing type flag
    public char getBillingType();
    public void setBillingType(char billingType);
    
    //special flags
    public static final int NONE = 0;
    public static final int LINKED_AWAY = 1;
    public static final int AUCTION_SPIN_OFF = 2;
    
    public boolean isLinkedAway();
    //Some unusual trading cases that an auctioned tradable may be traded outside the auction context
    //   but we may need this for other business purposes, such as billing
    public boolean isSpinOffFromAnAuction();
    
    public boolean isGuaranteedEntitlementAllocated();
    
    public void setGuaranteedEntitlementAllocated(boolean guaranteedEntitlementAllocated);
}
