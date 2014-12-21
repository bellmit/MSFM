//
// -----------------------------------------------------------------------------------
// Source file: SessionStrategyLeg.java
//
// PACKAGE: com.cboe.interfaces.presentation.product;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.product;

import com.cboe.interfaces.presentation.product.SessionStrategyLeg;
import com.cboe.presentation.product.ProductHelper;
import com.cboe.idl.cmiSession.SessionStrategyLegStruct;
import com.cboe.interfaces.presentation.product.SessionProduct;

/**
 * Defines the interface exposed by a strategy leg.
 */
class SessionStrategyLegImpl extends StrategyLegImpl implements SessionStrategyLeg
{
      private String tradingSession = null;
      private SessionProduct sessionProduct = null;
   /**
     * Constructor
     * @param sessionStrategyLegStruct to represent
     */
    protected SessionStrategyLegImpl(SessionStrategyLegStruct sessionStrategyLegStruct)
    {
        super(sessionStrategyLegStruct.product, sessionStrategyLegStruct.ratioQuantity, sessionStrategyLegStruct.side);
        tradingSession = sessionStrategyLegStruct.sessionName;
    }

    /**
     *  Default constructor.
     */
    protected SessionStrategyLegImpl()
    {
        super();
    }

    /**
     * Gets the session product that is used by this leg
     * @return SessionProduct
     */
    public SessionProduct getSessionProduct()
    {
        if(sessionProduct == null)
        {
            sessionProduct = ProductHelper.getSessionProduct(tradingSession, getProductKey());
        }
        return sessionProduct;
    }

}

