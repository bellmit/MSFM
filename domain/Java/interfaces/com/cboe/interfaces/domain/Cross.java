package com.cboe.interfaces.domain;

import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotSupportedException;

public interface Cross
{
    public static final short LINKED_AWAY_CROSS_NOT_SUPPORTED = 37;
    public static final short INVALID_REQUEST_ON_THE_CROSS = 38;
    
	public Integer getProductKey();
	public int getQuantity();
	public Order getBuyOrder();
	public Order getSellOrder();
	public Price getPrice();

    public void setOrders( Order buyOrder, Order sellOrder );

	public void cancel(short cancelReason) throws DataValidationException;
    
    /**
     * Publish the new order event
     */
    public void publishNewOrder();
    /**
     * Checking to see if one side of the order is existing order
     * Subclass can set to different value depends on the type of cross.
     */
    public boolean isCrossWithExistingOrder();
    
    public boolean isCrossWithAutoLinkedPAOrder();
    /**
     * The method is meaningful only when the above method returns true
     * @return true if the linked away order is a buy
     *         otherwise the linked away order is a sell
     */
    public boolean isLinkedAwaySideABuy() throws NotSupportedException;
    
    /**
     * Checking to see if existing order is held order
     * Subclass can set to different value depends on the type of cross.
     */
    public boolean isProcessingOutOfSequence();
    
    /**
     * Returns orders in the cross that should be deleted if cross processing failed    
     */
    public Order[] getOrdersToDeleteForFailedCross();
}
