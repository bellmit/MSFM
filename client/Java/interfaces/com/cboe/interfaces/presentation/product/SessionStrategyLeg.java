//
// -----------------------------------------------------------------------------------
// Source file: SessionStrategyLeg.java
//
// PACKAGE: com.cboe.interfaces.presentation.product;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.product;

/**
 * Defines the interface exposed by a strategy leg.
 */
public interface SessionStrategyLeg extends StrategyLeg
{
    /**
     * Gets the session product that is used by this leg
     * @return SessionProduct
     */
    public SessionProduct getSessionProduct();
}

