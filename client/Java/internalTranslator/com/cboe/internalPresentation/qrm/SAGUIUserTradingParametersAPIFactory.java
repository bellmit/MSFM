package com.cboe.internalPresentation.qrm;

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
import com.cboe.internalPresentation.api.SystemAdminAPIFactory;
import com.cboe.presentation.qrm.*;

/**
 *  This class can be used for ....
 *
 *
 *  @author Alex Brazhnichenko
 *  Creation date (3/8/00 1:05:22 PM)
 *  @version 03/08/2000
 */

public class SAGUIUserTradingParametersAPIFactory implements UserTradingParametersAPIFactory {
    private static GUIUserTradingParametersAPI tradingParametersAPI;
/**
 * GUIUserTradingParametersAPIFactory constructor comment.
 */
public SAGUIUserTradingParametersAPIFactory() {
    super();
}
/**
 * find method comment.
 */
public GUIUserTradingParametersAPI find()
{
    if ( tradingParametersAPI == null )
    {
        tradingParametersAPI = new SAGUIUserTradingParametersAPIImpl();
    }

    return tradingParametersAPI;
}
}
