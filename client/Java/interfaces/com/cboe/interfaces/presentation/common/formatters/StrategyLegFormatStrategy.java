//
// -----------------------------------------------------------------------------------
// Source file: StrategyLegFormatStrategy.java
//
// PACKAGE: com.cboe.interfaces.presentation.common.formatters;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.common.formatters;

import com.cboe.idl.cmiOrder.LegOrderEntryStruct;
import com.cboe.idl.cmiOrder.LegOrderDetailStruct;
import com.cboe.idl.cmiOrder.LegOrderEntryStructV2;

import com.cboe.interfaces.presentation.product.StrategyLeg;

/**
 * Defines a contract for a class that formats StrategyLegs
 */
public interface StrategyLegFormatStrategy extends FormatStrategy
{
    public static final String STRATEGY_LEG_SIDE = "Buy or Sell";   
    public static final String STRATEGY_LEG_PRODUCT = "The product of the leg";     
    public static final String FULL_STRATEGY_LEG_NAME = "The full name of the strategy leg";
    public static final String FULL_STRATEGY_LEG_DESCRIPTION = "Ratio, side and Reporting Class Symbol";
    public static final String BRIEF_STRATEGY_LEG_NAME = "The brief name of the strategy leg";
    public static final String BRIEF_STRATEGY_LEG_DESCRIPTION =
            "Ratio, 1-char side and Reporting Class Symbol";

    /**
     * Defines a method for formatting SessionStrategyLeg
     * @param leg to format
     * @return formatted string
     */
    public String format(StrategyLeg leg);
    public String format(StrategyLeg leg, LegOrderEntryStructV2 legOrdEntryStruct);
    public String format(StrategyLeg leg, LegOrderDetailStruct legOrdDetailStruct);
    public String format(StrategyLeg leg, String styleName);
}
