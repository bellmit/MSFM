package com.cboe.interfaces.domain;

import com.cboe.interfaces.domain.Tradable;
import com.cboe.interfaces.domain.Side;
/**
 * this class is designed to provide a common type for all different order book price detail
 * implementations.
 */

public interface OrderBookPriceDetail {

    /**
     * allocate volume to the price detail
     */
    public void allocateVolume(int aQuantity, boolean isGuaranteedEntitlementAllocated, boolean includeReservedQuantity);
    
    /** 
     * this resets the internal state of the object for the next trading transaction.
     */
    public void reset();

    public boolean isGuaranteedEntitlementAllocated();
    
    public void setGuaranteedEntitlementAllocated(boolean value);
    
    /**
     * return all available quantity, not including reserved quantity
     */
    public int getAvailableQuantity();

    /**
     * return all available quantity including/excluding reserved quantity
     */
    public int getAvailableQuantity( boolean includeReserved );

    /**
     * return the booked time in long. Return 0 if not booked
     */
    public long getBookedTime();

    /**
     * return a boolean to indicate if the underlying tradable is a nonQ
     */
    public boolean isNonQ();

    /**
     * return the underlying tradable
     */
    public Tradable getTradable();

    /**
     * return min quantity
     */
    public int getMinQuantity();

    /**
     * return quantity allocated
     */
    public int getQuantityAllocated(boolean includeReservedQuantity);

    /**
     * return the side
     */
    public Side getSide();
    
    
    public void dellocateLastAllocated();
}
