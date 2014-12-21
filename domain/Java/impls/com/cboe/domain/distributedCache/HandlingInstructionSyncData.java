package com.cboe.domain.distributedCache;

import java.io.Serializable;

import com.cboe.interfaces.domain.Price;

public class HandlingInstructionSyncData implements Serializable
{
    private Price oppositeSideBOTR;
    private Price executionPrice;
    private Price originalExecutionPrice;
    private int maximumExecutionVolume;
    private short maximumExecutionVolumeReason;
    private short tradingRestriction;
    private short remainderHandlingMode;
    private boolean ignoreContingency;
    private int orderQuantityAtReceiveTime;
    private int tradedVolume;
    private int maxTradableQtyForIndexHybrid;
    private short overRideReasonForIndexHybrid;
    private boolean expressOrder;
    private double bookableOrderMarketLimit;
    private short returnCode;
    private boolean inboundISOEnabled;
 
    public Price getOppositeSideBOTR()
    {
        return oppositeSideBOTR;
    }
    public Price getExecutionPrice()
    {
        return executionPrice;
    }
    public Price getOriginalExecutionPrice()
    {
        return originalExecutionPrice;
    }
    public int getMaximumExecutionVolume()
    {
        return maximumExecutionVolume;
    }
    public short getMaximumExecutionVolumeReason()
    {
        return maximumExecutionVolumeReason;
    }
    public short getTradingRestriction()
    {
        return tradingRestriction;
    }
    public short getRemainderHandlingMode()
    {
        return remainderHandlingMode;
    }
    public boolean isIgnoreContingency()
    {
        return ignoreContingency;
    }
    public int getOrderQuantityAtReceiveTime()
    {
        return orderQuantityAtReceiveTime;
    }
    public int getTradedVolume()
    {
        return tradedVolume;
    }
    public int getMaxTradableQtyForIndexHybrid()
    {
        return maxTradableQtyForIndexHybrid;
    }
    public short getOverRideReasonForIndexHybrid()
    {
        return overRideReasonForIndexHybrid;
    }
    public boolean isExpressOrder()
    {
        return expressOrder;
    }
    public double getBookableOrderMarketLimit()
    {
        return bookableOrderMarketLimit;
    }
    public short getReturnCode()
    {
        return returnCode;
    }
    public boolean isInboundISOEnabled()
    {
        return inboundISOEnabled;
    }
    public void setOppositeSideBOTR(Price p_oppositeSideBOTR)
    {
        oppositeSideBOTR = p_oppositeSideBOTR;
    }
    public void setExecutionPrice(Price p_executionPrice)
    {
        executionPrice = p_executionPrice;
    }
    public void setOriginalExecutionPrice(Price p_originalExecutionPrice)
    {
        originalExecutionPrice = p_originalExecutionPrice;
    }
    public void setMaximumExecutionVolume(int p_maximumExecutionVolume)
    {
        maximumExecutionVolume = p_maximumExecutionVolume;
    }
    public void setMaximumExecutionVolumeReason(short p_maximumExecutionVolumeReason)
    {
        maximumExecutionVolumeReason = p_maximumExecutionVolumeReason;
    }
    public void setTradingRestriction(short p_tradingRestriction)
    {
        tradingRestriction = p_tradingRestriction;
    }
    public void setRemainderHandlingMode(short p_remainderHandlingMode)
    {
        remainderHandlingMode = p_remainderHandlingMode;
    }
    public void setIgnoreContingency(boolean p_ignoreContingency)
    {
        ignoreContingency = p_ignoreContingency;
    }
    public void setOrderQuantityAtReceiveTime(int p_orderQuantityAtReceiveTime)
    {
        orderQuantityAtReceiveTime = p_orderQuantityAtReceiveTime;
    }
    public void setTradedVolume(int p_tradedVolume)
    {
        tradedVolume = p_tradedVolume;
    }
    public void setMaxTradableQtyForIndexHybrid(int p_maxTradableQtyForIndexHybrid)
    {
        maxTradableQtyForIndexHybrid = p_maxTradableQtyForIndexHybrid;
    }
    public void setOverRideReasonForIndexHybrid(short p_overRideReasonForIndexHybrid)
    {
        overRideReasonForIndexHybrid = p_overRideReasonForIndexHybrid;
    }
    public void setExpressOrder(boolean p_expressOrder)
    {
        expressOrder = p_expressOrder;
    }
    public void setBookableOrderMarketLimit(double p_bookableOrderMarketLimit)
    {
        bookableOrderMarketLimit = p_bookableOrderMarketLimit;
    }
    public void setReturnCode(short p_returnCode)
    {
        returnCode = p_returnCode;
    }
    public void setInboundISOEnabled(boolean p_inboundISOEnabled)
    {
        inboundISOEnabled = p_inboundISOEnabled;
    }

}
