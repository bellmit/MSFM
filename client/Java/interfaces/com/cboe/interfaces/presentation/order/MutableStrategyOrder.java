//
// -----------------------------------------------------------------------------------
// Source file: MutableStrategyOrder.java
//
// PACKAGE: com.cboe.interfaces.presentation.order
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.order;

import com.cboe.interfaces.presentation.product.SessionStrategy;
import com.cboe.interfaces.presentation.product.SessionStrategyLeg;

public interface MutableStrategyOrder extends StrategyOrder, MutableOrder
{
    public static final String PROPERTY_STRATEGY_PRODUCT = "PROPERTY_STRATEGY_PRODUCT";

    public void setSessionStrategy(SessionStrategy strategy);

    public MutableLegOrderDetail getMutableLegOrder(SessionStrategyLeg leg);

    public MutableLegOrderDetail getMutableLegOrder(int productKey);
}