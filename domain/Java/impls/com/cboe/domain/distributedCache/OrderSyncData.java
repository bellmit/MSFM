package com.cboe.domain.distributedCache;

import java.io.Serializable;

import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.domain.util.PriceFactory;
import com.cboe.interfaces.domain.Price;

public class OrderSyncData extends OrderBaseCarrier implements Serializable
{
    private static final long serialVersionUID = 7526472295622776147L;
    
    private PriceStruct widenedPrice;
    private PriceStruct pendingTradePrice;
    private PriceStruct marketOrderConvertedPrice;
    private long pendingTradeId;
    private boolean isInvolvedInQuoteTrigger;
    private boolean cancelRequestPending;
    private String cancelRequestUserAssignedCancelId;
    private int cancelRequestPendingQuantity;
    private long cancelReplaceNewOrderId;
    private PriceStruct lastBookedPrice;
    
    // Do we need to create
    // a TE-specific handling instruction data carrier or
    // is the HandlingInstruction used in OHS carrier sufficient? Need to find out.
    // private OrderHandlingInstructionSyncData handlingInstruction
    public Price getWidenedPrice()
    {
        Price price = null;
        if (this.widenedPrice != null) {
            price = PriceFactory.create(widenedPrice);
        }
        return price;
    }
    public Price getPendingTradePrice()
    {
        Price price = null;
        if (this.pendingTradePrice != null) {
            price = PriceFactory.create(pendingTradePrice);
        }
        return price;
    }
    public Price getMarketOrderConvertedPrice()
    {
        Price price = null;
        if (this.marketOrderConvertedPrice != null) {
            price = PriceFactory.create(marketOrderConvertedPrice);
        }
        return price;
    }
    public long getPendingTradeId()
    {
        return pendingTradeId;
    }
    public boolean isInvolvedInQuoteTrigger()
    {
        return isInvolvedInQuoteTrigger;
    }
    public boolean isCancelRequestPending()
    {
        return cancelRequestPending;
    }
    public String getCancelRequestUserAssignedCancelId()
    {
        return cancelRequestUserAssignedCancelId;
    }
    public int getCancelRequestPendingQuantity()
    {
        return cancelRequestPendingQuantity;
    }
    public long getCancelReplaceNewOrderId()
    {
        return cancelReplaceNewOrderId;
    }
    public Price getLastBookedPrice()
    {
        Price price = null;
        if (lastBookedPrice != null) {
            price = PriceFactory.create(lastBookedPrice);
        }
        return price;
    }    
    public void setWidenedPrice(Price p_widenedPrice)
    {
        widenedPrice = null;;
        if (p_widenedPrice != null) {
            this.widenedPrice = p_widenedPrice.toStruct();
        }
    }
    public void setPendingTradePrice(Price p_pendingTradePrice)
    {
        pendingTradePrice = null;
        if (p_pendingTradePrice != null) {
            this.pendingTradePrice = p_pendingTradePrice.toStruct();
        }
    }
    public void setMarketOrderConvertedPrice(Price p_marketOrderConvertedPrice)
    {
        marketOrderConvertedPrice = null;
        if (p_marketOrderConvertedPrice != null) {
            this.marketOrderConvertedPrice = p_marketOrderConvertedPrice.toStruct();
        }
    }    
    public void setPendingTradeId(long p_pendingTradeId)
    {
        pendingTradeId = p_pendingTradeId;
    }
    public void setInvolvedInQuoteTrigger(boolean p_isInvolvedInQuoteTrigger)
    {
        isInvolvedInQuoteTrigger = p_isInvolvedInQuoteTrigger;
    }
    public void setCancelRequestPending(boolean p_cancelRequestPending)
    {
        cancelRequestPending = p_cancelRequestPending;
    }
    public void setCancelRequestUserAssignedCancelId(String p_cancelRequestUserAssignedCancelId)
    {
        cancelRequestUserAssignedCancelId = p_cancelRequestUserAssignedCancelId;
    }
    public void setCancelRequestPendingQuantity(int p_cancelRequestPendingQuantity)
    {
        cancelRequestPendingQuantity = p_cancelRequestPendingQuantity;
    }
    public void setCancelReplaceNewOrderId(long p_cancelReplaceNewOrderId)
    {
        cancelReplaceNewOrderId = p_cancelReplaceNewOrderId;
    }
    public void setLastBookedPrice(Price p_lastBookedPrice)
    {
        lastBookedPrice = null;
        if (p_lastBookedPrice != null) {
            lastBookedPrice = p_lastBookedPrice.toStruct();
        }
    }
    public int getRemainingQuantity()
    {
        return getOrderHandlingStruct().originalQuantity +
        getOrderHandlingStruct().addedQuantity -
        getOrderHandlingStruct().tradedQuantity -
        getOrderHandlingStruct().cancelledQuantity -
        getPendingTradeQuantity()-
        getShipQuantity();
    }
    
    public String toString()
    {
    	StringBuffer sb = new StringBuffer();
    	sb.append("Prod:"+getOrderHandlingStruct().productKey);
    	sb.append("Br/Seq="+getOrderHandlingStruct().orderId.branch+":");
    	sb.append(getOrderHandlingStruct().orderId.branchSequenceNumber+";");
    	sb.append("Orig Qty="+getOrderHandlingStruct().originalQuantity+";");
    	sb.append("Traded="+getOrderHandlingStruct().tradedQuantity+";");
    	sb.append("Cancelled="+getOrderHandlingStruct().cancelledQuantity+";");
    	sb.append("Remain="+getRemainingQuantity()+";");
    	sb.append("Origin="+getOrderHandlingStruct().orderOriginType+";");
    	sb.append("Cont="+getOrderHandlingStruct().contingency.type+";");
    	sb.append("Booked="+getBookedQuantity());
    	sb.append("Shiped="+getShipQuantity());
    	return sb.toString();
    }
}
