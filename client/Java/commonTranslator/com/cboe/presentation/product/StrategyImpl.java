//
// -----------------------------------------------------------------------------------
// Source file: SessionProductImpl.java
//
// PACKAGE: com.cboe.presentation.product;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.product;

import com.cboe.interfaces.presentation.product.Strategy;
import com.cboe.interfaces.presentation.product.StrategyLeg;

import com.cboe.idl.cmiStrategy.StrategyStruct;
import com.cboe.idl.cmiStrategy.StrategyLegStruct;
import com.cboe.idl.cmiProduct.ProductStruct;


/**
 * SessionProduct implementation for a SessionProductStruct from the API.
 */
class StrategyImpl extends ProductImpl implements Strategy
{
      private short strategyType;
      private StrategyLegStruct[] strategyLegs = null;
    /**
     * Constructor
     * @param sessionStrategyStruct to represent
     */
    protected StrategyImpl(StrategyStruct strategy)
    {
        super(strategy.product);
        strategyType = strategy.strategyType;
        strategyLegs = strategy.strategyLegs;
    }
    /**
     *  Default constructor.
     */
    protected StrategyImpl()
    {
        super();
    }
    /**
     * Gets the legs for this strategy product
     * @return SessionStrategyLeg[]
     */
    public StrategyLeg[] getStrategyLegs()
    {
        int legLength = this.strategyLegs.length;
        StrategyLeg[] strategyLegs = new StrategyLegImpl[legLength];
        for (int i=0; i<legLength; i++)
        {
//            strategyLegs[i] = new StrategyLegImpl(this.strategyLegs[i]);
            strategyLegs[i] = ProductFactoryHome.find().create(this.strategyLegs[i]);
        }
        return strategyLegs;
    }

    /**
     * Gets the type of this strategy
     * @see com.cboe.idl.cmiConstants.StrategyTypes
     * @return short
     */
    public short getStrategyType()
    {
       return strategyType;
    }

    public StrategyLegStruct[] getStrategyLegStructs()
    {
       return strategyLegs;
    }

}

