package com.cboe.interfaces.domain;

import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.idl.cmiUser.ExchangeAcronymStruct;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;
import com.cboe.idl.cmiOrder.*;
import com.cboe.exceptions.*;

/**
 * AgentAssignedOrder is the held order, cancel request, cancel replace request for NBBO
 * Agent held order.  It is not the same as order.  Using the order key to find the related order object.
 */
public interface AgentAssignedOrder
{
	// Define some constants to be used for type
	public static final short NEW_HELD_ORDER = 1;
	public static final short CANCEL_REQUEST = 2;
	public static final short CANCEL_REPLACE_REQUEST = 3;

    // Define some constants to be used for status
    public static final char PROCESSED = 'P';
    public static final char HOLD = 'H';
    
    /**
     * Returns the database unique identifier for the AgentAssignedOrder. It is NOT the
     * same as the order key that the AgentAssignedOrder is based on.
     */
    public long getUniqueId();
        
    /**
     * Returns the order object key the AgentAssignedOrder object is based on.
     */
    public long getAssignedOrderKey();
    
    /**
     * Returns the status of the AgentAssignedOrder (Processed or Hold, see constants)
     */
    public char getStatus();
    
    /**
     * Returns the user assigned cancel Id if the type of this AgentAssignedOrder
     * is cancel or cancel replace.
     */
    public String getUserAssignedCancelId();
        
    /**
     * Returns the cancel quantity if the type of this AgentAssignedOrder
     * is cancel or cancel replace.
     */
    public int getCancelQuantity();
    
    /**
     * Updates the canceled quantity
     * @param quantity
     */
    public void setCancelQuantity(int quantity);
    
    /**
     * Returns the cancel type (see cmiConstants.CancelTypes) if the type of this AgentAssignedOrder
     * is cancel or cancel replace.
     */
    public short getCancelType();
        
    /**
     * Returns the cancel replacement order key if the type of this AgentAssignedOrder
     * is cancel replace.
     */
    public long getReplacementOrderKey();  
    
    /**
     * Indicates if the object has been held before, particularly useful for held order
     * 
     */    
    public boolean hasBeenHeld();
    
    /**
     * Indicates if the object is a held order type
     */
    public boolean isHeldOrder();
    
    /**
     * Indicates if the object is a cancel request type
     */
    public boolean isCancelRequest();
    
    /**
     * Indicates if the object is a cancel or cancel replace type
     */
    public boolean isCancelOrCancelReplace();
    
    /**
     * Indicates if the object is a cancel replace type
     */
    public boolean isCancelReplaceRequest();  
    
    /**
     * Returns the last activity time logged for this entry
     */
    public long getLastActivityTime();
        
    /**
     * Sets the last activity time for the entry
     */
    public void setLastActivityTime(long newActivityTime);
    
    public void setStatus(char aValue);
}




