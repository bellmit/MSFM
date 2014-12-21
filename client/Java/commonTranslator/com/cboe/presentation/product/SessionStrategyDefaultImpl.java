//
// -----------------------------------------------------------------------------------
// Source file: SessionStrategyDefaultImpl.java
//
// PACKAGE: com.cboe.presentation.product
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.product;

import com.cboe.idl.cmiSession.SessionStrategyLegStruct;
import com.cboe.idl.cmiStrategy.StrategyLegStruct;
import com.cboe.idl.cmiConstants.StrategyTypes;

import com.cboe.interfaces.presentation.product.SessionStrategy;
import com.cboe.interfaces.presentation.product.SessionStrategyLeg;
import com.cboe.interfaces.presentation.product.Strategy;
import com.cboe.interfaces.presentation.product.StrategyLeg;

public class SessionStrategyDefaultImpl extends SessionProductDefaultImpl implements SessionStrategy
{
    protected SessionStrategyDefaultImpl()
    {
        super();
    }

    /**
     * Clones this object by returning another instance of this class.
     */
    public Object clone() throws CloneNotSupportedException
    {
        return new SessionStrategyDefaultImpl();
    }

    /**
     * If <code>obj</code> is an instance of this class true is return,
     * false otherwise.
     * @param obj to compare
     * @return true if equal, false if not.
     */
    public boolean equals(Object obj)
    {
        boolean isEqual = super.equals(obj);

        if(isEqual)
        {
            if(obj instanceof SessionStrategyDefaultImpl)
            {
                isEqual = true;
            }
            else
            {
                isEqual = false;
            }
        }
        return isEqual;
    }

    /**
     * Get the product key for this Product.
     * @return Default product key for the SessionStrategyDefaultImpl is returned
     */
    public int getProductKey()
    {
        return DEFAULT_STRATEGY_PRODUCT_KEY;
    }

    /**
     * Gets the legs for this strategy product
     * @return SessionStrategyLeg[]
     */
    public SessionStrategyLeg[] getSessionStrategyLegs()
    {
        return new SessionStrategyLeg[0];
    }

    /**
     * @deprecated use for backwards compatibility
     */
    public SessionStrategyLegStruct[] getSessionStrategyLegStructs()
    {
        return new SessionStrategyLegStruct[0];
    }

    public void updateStrategy(Strategy newStrategy)
    {
    }

    /**
     * Gets the legs for this strategy product
     * @return StrategyLeg[]
     */
    public StrategyLeg[] getStrategyLegs()
    {
        return new StrategyLeg[0];
    }

    public StrategyLegStruct[] getStrategyLegStructs()
    {
        return new StrategyLegStruct[0];
    }

    /**
     * Gets the type of this strategy
     * @see StrategyTypes
     * @return short
     */
    public short getStrategyType()
    {
        return DEFAULT_STRATEGY_TYPE;
    }
}
