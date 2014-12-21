//
// -----------------------------------------------------------------------------------
// Source file: StrategyDefaultImpl.java
//
// PACKAGE: com.cboe.presentation.product
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.product;

import com.cboe.idl.cmiStrategy.StrategyLegStruct;
import com.cboe.idl.cmiConstants.StrategyTypes;

import com.cboe.interfaces.presentation.product.Strategy;
import com.cboe.interfaces.presentation.product.StrategyLeg;

public class StrategyDefaultImpl extends ProductDefaultImpl implements Strategy
{
    protected StrategyDefaultImpl()
    {
        super();
    }

    /**
     * Clones this object by returning another instance of this class.
     */
    public Object clone() throws CloneNotSupportedException
    {
        return new StrategyDefaultImpl();
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
            if(obj instanceof StrategyDefaultImpl)
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
     * @return Default product key for the StrategyDefaultImpl is returned
     */
    public int getProductKey()
    {
        return DEFAULT_STRATEGY_PRODUCT_KEY;
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
