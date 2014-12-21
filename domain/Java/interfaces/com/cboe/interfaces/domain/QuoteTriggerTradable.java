package com.cboe.interfaces.domain;

import java.util.ArrayList;
import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.domain.Tradable;
import com.cboe.interfaces.domain.QuoteTriggerTradeReport;
import com.cboe.idl.cmiUtil.CboeIdStruct;

/**
 * this interface is designed as a wrap tradable which contains multiple underlying
 * Quote-like tradables
 */

public interface QuoteTriggerTradable extends Tradable {

    //Trigger Type, currently not being used just set
    public static final short UNSPECIFIED = 0;//i.e normal QuoteTrigger
    public static final short HAL_TRIGGERED = 1;

    //Trigger Expiry reasons
    public static final short INCOMING_HAL = 5;
    public static final short INCOMING_MANUAL_QUOTE = 6;

    /**
     * return a collection of all price details this trigger holds on
     */
    public ArrayList getPriceDetails();

    /**
     * return a collection of all tradables
     */
    public ArrayList getTradables();

    /**
     * return the price this trigger is created for
     */
    public Price getTriggerPrice();

    public void setNonQPrice(Price nonQPriceIn);

    public Price getNonQPrice();
    /**
     * set the trigger price
     */
    public void setTriggerPrice(Price aPrice);
    /**
     * add a tradable to this trigger
     */
    public void addTradable(Tradable aTradable);

	/**
     * remove a tradable to this trigger
     */
    public void removeTradable(Tradable aTradable);

    /**
     * expire the trigger
     */
    public void expire();
    /**
     * return a boolean to indicate if the quote trigger is still active
     */
    public boolean isActive();

    /**
     * Return the pending TradeReport of this quote trigger
     */
    public QuoteTriggerTradeReport getPendingTradeReport();

    /**
     * set the pending TradeReport of this quote trigger
     */
    public void setPendingTradeReport(QuoteTriggerTradeReport aTradeReport);

    public void setAuctionId(CboeIdStruct cboeId);

    public CboeIdStruct getAuctionId();

    public short getTriggerType();

    public void setExpiryReason(short expiryReason);

    public short getExpiryReason();

    public void updateRFPQuantity(int rfpQuantityIn);

    public int getQuantityToPublish(int priceItemQty);

    public int getQLikeQuantity();
}
