package com.cboe.interfaces.domain;

import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.idl.order.OrderHandlingInstructionStruct;
import com.cboe.interfaces.domain.Price;

public interface HandlingInstruction {

	
    // return the best of the rest for the opposite side of an order
    public Price getOppositeSideBOTR();

    public void setOppositeSideBOTR(Price aBOTR);
    
    // return the execution price at which order should be executed
    public Price getExecutionPrice();
    public void setExecutionPrice(Price price);

    // return the original execution price at which order should be executed
    public Price getOriginalExecutionPrice();
    public void setOriginalExecutionPrice(Price price);

    // return the volume up to which order can be traded
    public int getMaximumExecutionVolume();
    public void setMaximumExecutionVolume(int value); 

    // return the reason for the max volume. Maximum execution volume reason
    // is defined in com.cboe.idl.constants.MaximumExecutionVolumeReasons
    public short getMaximumExecutionVolumeReason();

    // return the trading restiction.  Trading restriction is defined in
    // com.cboe.idl.constants.TradingRestriction
    public short getTradingRestriction();
    public void setTradingRestriction(short value);

    // return the remainder handling mode. Remaninder handling mode is defined
    // in com.cboe.idl.constants.RemainderHandlingModes
    public short getRemainderHandlingMode();
    public void setRemainderHandlingMode(short aHandlingMode);

    // return a boolean to indicate if contingency should be ignored if it is a
    // contingent order
    public boolean ignoreContingency();
    public void setIgnoreContingency(boolean aBoolean);

    // return the order's quantity at the time when system receives it
    public int getOrderQuantityReceived();
    
    //  return maximum quantity that can be traded for Index Hybrid
    public int getMaxTradableQtyForIndexHybrid();
    public void setMaxTradableQtyForIndexHybrid(int value);
    public void setDefaultMaxTradableQtyForIndexHybrid();
    public boolean isDefaultMaxTradableQtyForIndexHybrid();
    
    //  return overRide reason for Index Hybrid
    public short getOverRideReasonForIndexHybrid();
    public void setOverRideReasonForIndexHybrid(short value);
    public void setDefaultOverRideReasonForIndexHybrid();
    public boolean isOrderReturnReasonSetForIndexHybrid();
    
    public void updateFrom(OrderHandlingInstructionStruct handlingInstruction, PriceStruct oppositeSideBestOfRest, int quantity);
    
    public boolean updateFrom(String instruction, int orderQuantityReceived);
    
    // added for OHS as we pass string, need to set instruction from string
    public boolean updateFrom(String handlingStruction);
    

    public String toPersistenceString();

    public int getTradedVolume();

    public int getAllowedQuantity();

    public void setTradedVolume(int value);
    
    public void setExpressOrder(boolean value);
    
    public boolean isExpressOrder();

    public void setReturnedCode(short value);
    public short getReturnedCode();
    
    public void setInboundISOEnabled(boolean value);
    public boolean isInboundISOEnabled();
    
    public void setBookableOrderMarketLimit(double value);
    public double getBookableOrderMarketLimit();
    
    public void setAttempted(boolean attempted);
    public boolean getAttempted();
}