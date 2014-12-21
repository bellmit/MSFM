//
// -----------------------------------------------------------------------------------
// Source file: Product.java
//
// PACKAGE: com.cboe.interfaces.presentation.product;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.product;

import com.cboe.idl.cmiProduct.ProductKeysStruct;
import com.cboe.idl.cmiProduct.ProductNameStruct;
import com.cboe.idl.cmiProduct.ProductStruct;
import com.cboe.idl.cmiStrategy.StrategyLegStruct;

import com.cboe.idl.cmiUtil.DateStruct;
import com.cboe.idl.cmiUtil.DateTimeStruct;
import com.cboe.interfaces.domain.Price;

/**
 * Define a Strategy wrapper for a StrategyStruct
 */
public interface Strategy extends Product
{
    /**
     * Gets the legs for this strategy product
     * @return StrategyLeg[]
     */
   public StrategyLeg[] getStrategyLegs();
    /**
     * Gets the type of this strategy
     * @see com.cboe.idl.cmiConstants.StrategyTypes
     * @return short
     */
    public short getStrategyType();

    public StrategyLegStruct[] getStrategyLegStructs();
}