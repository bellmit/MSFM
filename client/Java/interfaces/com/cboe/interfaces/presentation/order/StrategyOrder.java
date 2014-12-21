//
// -----------------------------------------------------------------------------------
// Source file: StrategyOrder.java
//
// PACKAGE: com.cboe.interfaces.presentation.order
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.order;

import com.cboe.interfaces.presentation.product.SessionStrategy;
import com.cboe.interfaces.presentation.product.SessionStrategyLeg;

public interface StrategyOrder extends Order
{
    /**
     * Convenience method for getting the strategy that this order contains
     * @return SessionStrategy
     */
    public SessionStrategy getSessionStrategy();

    /**
     * Get the order for the given strategy leg
     * @param leg to get order for
     * @return LegOrderDetail
     * @throws IllegalArgumentException if the given leg does not exist within this
     * order's strategy.
     */
    public LegOrderDetail getLegOrder(SessionStrategyLeg leg);

    /**
     * Get the order for the given strategy leg product key
     * @param productKey to get order for
     * @return LegOrderDetail
     * @throws IllegalArgumentException if the given leg does not exist within this
     * order's strategy.
     */
    public LegOrderDetail getLegOrder(int productKey);

    /**
     * Returns true if all mustUsePrices in the strategy's legOrderDetail are Valued Prices and > 0.0
     * @return true or false
     */
    public boolean areAllLegPricesSet();

    /**
     * Returns true if any mustUsePrices in the strategy's legOrderDetail are Valued Prices and > 0.0
     * @return true or false
     */
    public boolean areAnyLegPricesSet();

    /**
     * returns leg order details array directly from strategy order. This is needed since getLegOrderDetails() was
     * overridden in the strategy order implementation.
     * @return leg order details array
     */
    public LegOrderDetail[] getLegOrderDetailsArray();
}