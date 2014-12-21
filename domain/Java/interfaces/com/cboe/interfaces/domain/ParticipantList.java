package com.cboe.interfaces.domain;

import com.cboe.exceptions.*;
import java.util.*;

/**
 * A container for all participants on one side of a trade.
 */
public interface ParticipantList
{
	/**
	* Tradables are orders or quotes for the traded product
	*/
	static final int REGULAR_LIST = 1;

	/**
	* Tradables are for legs of a spread and are calculated from a spread order or quote.
	*/
	static final int CALCULATED_LIST = 2;

 	/**
	* Tradables are quotes for a spread that derived from the best books of the legs.
	*/
	static final int DERIVED_LIST = 3;

	/**
	 * This method links a new node into the linked list chain of ParticipantItem nodes
	 * author David Wegener
	 */
	public void addElement(ParticipantItem participant);

    /**
     * set trade price this participant list is created
     */
    public void setTradePrice(Price price);

    /**
     * return trade price this participant list is created
     */
    public Price getTradePrice();

    /**
     * return the trade side this participantList represents
     */
    public Side getTradeSide();

    /**
     * set the trade side this participantList represents
     */
    public void setTradeSide(Side side);

    /**
     * return total trade quantity
     */
    public int getTotalTradeQuantity();

    /**
     * set total trade quantity
     */
    public void setTotalTradeQuantity(int qty);

    /**
     * return Max quantity
     */
    public int getMaxQuantity();

    /**
     * set total trade quantity
     */
    public void setMaxQuantity(int qty);

   /**
    * This method returns a participant for each spread leg.
    * author Brandon Stewart
    */
    public ParticipantList[] getListsForLegs() throws SpreadProcessingException, DataValidationException;

    //the following method will be removed
    public ParticipantList[] getListsForLegs(StrategyLegTradePrice[] legPrices, Side tradeSide, Tradable tradable) throws SpreadProcessingException, DataValidationException;


	/**
	 * Provide an Enumeration interface to the list
	 * author David Wegener
	 */
	public Enumeration elements();

    /**
     * This method calls the fill(Price) method of each ParticipantItem in the participantList
     * author Magic Magee.
     */
    public void fill(int productKey, long tradeId, Price fillPrice, Map reportMap, boolean updateTradable, Map<Tradable, TradableSnapShot> asyncFills);
    
    //public void fill(int productKey, long tradeId, Price fillPrice, Map reportMap, boolean updateTradable);

	/**
	 * Tests to see if this list contains calculated tradables.
	 */
	public boolean isCalculatedList();


	/**
	 * Tests to see if this list contains derived tradables.
	 */
	public boolean isDerivedList();

	/**
	 * This method determines whether or not the list has members
	 * author Magic Magee
	 * @return boolean
	 */
	public boolean isEmpty();


	/**
	 * Tests to see if this list contains regular tradables.
	 */
	public boolean isRegularList();


	/**
	 * This method empties the list of its contents.
	 * author Magic Magee
	 */
	public void removeAll();

    /**
	 * Reset allocation properties in order to reuse the list
	 */
	public void resetAllocationProperties();
	
    /**
     * Boolean indicates if contingent best is used for the derived list.
     */
    public boolean usedContingentBest();

    /**
     * set the leg prices when the list is derived list
     */
    public void setLegTradePrices(StrategyLegTradePrice[] legPrices);

    /**
     * return the leg trade prices if the list is a derived list
     */
    public StrategyLegTradePrice[] getLegTradePrices();

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
    public void completePendingTrade(int product, long tradedId, Price aPrice, HashMap fillReport);

    /**
     * In the case of spread to spread trade, the calculated leg prices have to be exactly on
     * the tick. If not, either find a different leg price combination or if such price combination
     * can not be found, do a split trade.
     *
     * In order to do such calculation, all different participanting quantities have to be
     * checked to see if such split can be done.
     */
    public HashSet getDistinctParticipantQuantities();

    public int size();//TODO SAL remove me later
    
    public ParticipantItem getLastParticipant();
    
    public void setIncludeVolumeContingentTradables(boolean isVolumeContingent);
    
    public boolean getIncludeVolumeContingentTradables();

}
