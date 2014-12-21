package com.cboe.domain.distributedCache;

import java.io.Serializable;

import com.cboe.domain.util.CboeId;
import com.cboe.domain.util.PriceFactory;
import com.cboe.idl.cmiOrder.OrderIdStruct;
import com.cboe.idl.cmiUtil.CboeIdStruct;
import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.idl.order.OrderHandlingStruct;
import com.cboe.interfaces.OrderCarrier;
import com.cboe.interfaces.domain.Price;

public class OrderBaseCarrier implements OrderCarrier, Serializable 
{
    private static final long serialVersionUID = 7526472390612776147L;
    
	//Persistence Fields
	private Long        key;
	private OrderHandlingStruct orderHandlingStruct;
    private boolean 	alreadyAuctioned = false;
    private boolean 	bookedByAuction = false;
    private int 		bookedQuantity;
    private int 		bookedStatus;
    private long 		bookedTime;
    private int 		bustedQuantity;
    private boolean 	crossingIndicator;
    private PriceStruct	initialTradePrice;
    private boolean 	markedForDropCopy = false;
    private int 		pendingTradeQuantity = 0;
    private int 		shipQuantity = 0;
    private int 		userKey;   // Remove this. This is derived from userId of OrderHandlingStruct
    
    public OrderBaseCarrier()
    {
    	
    }
    public OrderIdStruct getOrderIdStruct()
    {
        return orderHandlingStruct.orderId;
    }
    
    public long getOrderId()
    {
        CboeIdStruct cboeId = new CboeIdStruct(orderHandlingStruct.orderId.highCboeId, orderHandlingStruct.orderId.lowCboeId);
        return CboeId.longValue(cboeId);
    }
    
    public OrderHandlingStruct getOrderHandlingStruct()
    {
        return orderHandlingStruct;
    }
    
    public void setOrderHandlingStruct(OrderHandlingStruct struct) 
    {
        orderHandlingStruct = struct;
    }
    
	public boolean isCrossingIndicator() {
		return crossingIndicator;
	}
	public void setCrossingIndicator(boolean crossingIndicator) {
		this.crossingIndicator = crossingIndicator;
	}
	public int getUserKey() {
		return userKey;
	}
	public void setUserKey(int userKey) {
		this.userKey = userKey;
	}
	public long getBookedTime() {
		return bookedTime;
	}
	public void setBookedTime(long bookedTime) {
		this.bookedTime = bookedTime;
	}
	public int getBookedStatus() {
		return bookedStatus;
	}
	public void setBookedStatus(int bookedStatus) {
		this.bookedStatus = bookedStatus;
	}
	public int getBustedQuantity() {
		return bustedQuantity;
	}
	public void setBustedQuantity(int bustedQuantity) {
		this.bustedQuantity = bustedQuantity;
	}
	public int getBookedQuantity() {
		return bookedQuantity;
	}
	public void setBookedQuantity(int bookedQuantity) {
		this.bookedQuantity = bookedQuantity;
	}
	public Price getInitialTradePrice() {
		return PriceFactory.create(initialTradePrice);
	}
	public void setInitialTradePrice(Price p_initialTradePrice) {
	    if (p_initialTradePrice != null) {
	        this.initialTradePrice = p_initialTradePrice.toStruct();
	    }
	    else {
	        this.initialTradePrice = PriceFactory.create(Price.NO_PRICE_STRING).toStruct();	        
	    }
	}
	public int getShipQuantity() {
		return shipQuantity;
	}
	public void setShipQuantity(int shipQuantity) {
		this.shipQuantity = shipQuantity;
	}

	public int getPendingTradeQuantity() {
		return pendingTradeQuantity;
	}
	public void setPendingTradeQuantity(int pendingTradeQuantity) {
		this.pendingTradeQuantity = pendingTradeQuantity;
	}
	public boolean isMarkedForDropCopy() {
		return markedForDropCopy;
	}
	public void setMarkedForDropCopy(boolean markedForDropCopy) {
		this.markedForDropCopy = markedForDropCopy;
	}
	public boolean isBookedByAuction() {
		return bookedByAuction;
	}
	public void setBookedByAuction(boolean bookedByAuction) {
		this.bookedByAuction = bookedByAuction;
	}
	public boolean isAlreadyAuctioned() {
		return alreadyAuctioned;
	}
	public void setAlreadyAuctioned(boolean alreadyAuctioned) {
		this.alreadyAuctioned = alreadyAuctioned;
	}
	public Long getKey() {
		return key;
	}

	public void setKey(Long key) {
		this.key = key;
	}
}
