/**
 * 
 */
package com.cboe.interfaces.domain.optionsLinkage;

import com.cboe.idl.sweepAutoLink.SweepElementStruct;
import com.cboe.interfaces.domain.Price;

/**
 * The interface for Sweeping.
 * 
 * @author misbahud
 *
 */
public interface SweepElement extends Comparable 
{
    public static final String AUCTION_RESPONSE = "CBOE_AR";
    public static final String ORDER = "CBOE_ORDER";
    public static final String QUOTE = "CBOE_QUOTE";
    public static final String INCOMING_ORDER = "CBOE_INCOMING_ORDER";
    public static final String INCOMING_QUOTE = "CBOE_INCOMING_QUOTE";

    public Object getReferenceTradable();
    
    public void setReferenceTradable(Object p_referenceTradable);
    
    public String getExchangeName();

    public void setExchangeName(String p_exchangeName);

    public int getPreferenceOrder();

    public void setPreferenceOrder(int p_preferenceOrder);

    public Price getSweepPrice();

    public void setSweepPrice(Price p_sweepPrice);

    public int getQuantity();

    public void setQuantity(int p_quantity);

    public boolean isCboeTradable();

    public void setCboeTradable(boolean p_cboeTradable);

    public boolean isAuctionResponse();

    public void setAuctionResponse(boolean p_auctionResponse);

    public boolean isAwayExchange();

    public void setAwayExchange(boolean p_awayExchange);
    
    public int getRouteQuantity();

    public void setRouteQuantity(int p_routeQuantity);
    
    public SweepElement copy();
    
    public SweepElementStruct toStruct();
        
    public boolean isPreDeterminedTradePrice();
    
    public void setPreDeterminedTradePrice(boolean p_preDeterminedTradePrice);
    
    public boolean isIncomingTradable();
    
    public void setIncomingTradable(boolean p_incomingTradable);
    
    public boolean isQuantityUsed();
    
    public void setQuantityUsed(boolean p_quantityUsed);
    
    public int getRemainingQuantity();
    
    public void setRemainingQuantity(int p_remainingQuantity);
    
    public int getOriginalQuantity();
    
    public boolean isLockedTradable();
    
    public void setLockedTradable(boolean p_lockedTradable);

}
