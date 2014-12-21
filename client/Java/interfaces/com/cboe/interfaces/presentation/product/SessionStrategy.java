//
// -----------------------------------------------------------------------------------
// Source file: SessionStrategy.java
//
// PACKAGE: com.cboe.interfaces.presentation.product;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.product;

import com.cboe.idl.cmiSession.SessionStrategyLegStruct;

/**
 * Defines the interface exposed by a session strategy product.
 */
public interface SessionStrategy extends SessionProduct, Strategy
{
    /**
     * Gets the legs for this strategy product
     * @return SessionStrategyLeg[]
     */
   public SessionStrategyLeg[] getSessionStrategyLegs();

    /**
     * @deprecated use for backwards compatibility 
     */
   public SessionStrategyLegStruct[] getSessionStrategyLegStructs();

   public void updateStrategy(Strategy newStrategy);
}

