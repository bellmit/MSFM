package com.cboe.interfaces.presentation.api;

import com.cboe.interfaces.presentation.user.UserStructModel;
import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.AuthorizationException;

/**
 * FIX-specific APIs
 */ 
public interface FIXMarketMakerAPI extends MarketMakerAPI
{
    static public final String FIX_TRANSLATOR_NAME = "FIX_TRANSLATOR";

    /**
     * Obtain the reference to the the Validated User
     * @return Validated User Information
     */
    public UserStructModel getValidFIXUser()
            throws SystemException, CommunicationException, AuthorizationException;
    
}
