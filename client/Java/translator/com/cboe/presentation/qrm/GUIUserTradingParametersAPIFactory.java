package com.cboe.presentation.qrm;

//------------------------------------------------------------------------------------------------------------------
// FILE:    GUIUserTradingParametersAPIFactory.java
//
// PACKAGE: com.cboe.presentation.common
//
//-------------------------------------------------------------------------------------------------------------------
// Copyright (c) 2000 The Chicago Board Options Exchange. All Rights Reserved.
//
//-------------------------------------------------------------------------------------------------------------------


// Imports
// java packages

// local packages

/**
 *  This class can be used for ....
 *
 *
 *  @author Alex Brazhnichenko
 *  Creation date (3/8/00 1:05:22 PM)
 *  @version 03/08/2000
 */

public class GUIUserTradingParametersAPIFactory implements UserTradingParametersAPIFactory {
    private static GUIUserTradingParametersAPI tradingParametersAPI;
/**
 * GUIUserTradingParametersAPIFactory constructor comment.
 */
public GUIUserTradingParametersAPIFactory() {
    super();
}
/**
 * find method comment.
 */
public GUIUserTradingParametersAPI find()
{
    if ( tradingParametersAPI == null )
    {
        tradingParametersAPI = new GUIUserTradingParametersAPIImpl();
    }
        
    return tradingParametersAPI;
}
}
