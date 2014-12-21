package com.cboe.interfaces.domain.session;

import com.cboe.idl.cmiSession.SessionProductStruct;

/**
 * A session element product is used to maintain product state information
 * about a product in a trading session.
 *
 * @author John Wickberg
 */
public interface SessionElementProduct {

    /**
     * Gets the product key for this session product.
     * 
     * @return product key
     */
    int getProductKey();
    
    /**
     * Gets product state.
     */
    short getProductState();
    
    
    /**
     * Gets new-old product state.
     */
    boolean getProductCreateState();

    /**
     * Gets owning session class.
     *
     * @return owning session class
     */
    SessionElementClass getSessionClass();

    /**
     * Sets the state for the product.
     *
     * @param newState new state for this product
     * @param sequenceNumber transaction sequence number of state change
     */
    void setProductState(short newState, int sequenceNumber);
    
    void setProductState(short newState, int sequenceNumber, boolean isJustCreated);
    
    
    SessionProductStruct toSessionProductStruct();
}
