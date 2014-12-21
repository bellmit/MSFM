//
// -----------------------------------------------------------------------------------
// Source file: DsmParameterStruct.java
//
// PACKAGE: com.cboe.interfaces.presentation.marketData;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2009 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.marketData;

import com.cboe.interfaces.presentation.product.Product;
import com.cboe.idl.cmiUtil.PriceStruct;

/**
 * parameters passed to StrategyUtility in order to calculate the DSM for a strategy. each leg of the strategy will
 * have one instance of this structure
 */
public class DsmParameterStruct
{
    public char        side;
    public double      ratio;
    public PriceStruct bidPrice;
    public PriceStruct askPrice;
    public Product     product;
}
