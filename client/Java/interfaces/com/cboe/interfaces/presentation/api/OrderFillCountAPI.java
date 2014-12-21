//
// -----------------------------------------------------------------------------------
// Source file: OrderFillCountAPI.java
//
// PACKAGE: com.cboe.interfaces.presentation.api
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2011 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.api;

import com.cboe.interfaces.presentation.product.ProductClass;
import com.cboe.interfaces.domain.Price;

/**
 * Provides an interface to get the current daily counts of the user's Gross and Net order fill
 * quantities and dollar values.
 *
 * The counts are updated as orders are filled or busted.
 */
public interface OrderFillCountAPI
{
    /**
     * Return the user's total gross order quantity that has traded today for the ProductClass.
     */
    int getGrossOrderQuantityTraded(ProductClass pc);

    /**
     * Return the user's total cumulative gross price of the orders that have traded today for the ProductClass.
     */
    Price getGrossOrderDollarValueTraded(ProductClass pc);

    /**
     * Return the user's total net order quantity that has traded today for the ProductClass.
     *
     * Net quantity is long positions (long call/short put, long stock, long futures) - short positions
     * (short call/long put, short stock, short futures)
     */
    int getNetOrderQuantityTraded(ProductClass pc);

    /**
     * Return the user's total cumulative net price of the orders that have traded today for the ProductClass.
     *
     * Net dollar value is the dollar value of long positions (long call/short put, long stock, long futures) - the
     * dollar value of short positions (short call/long put, short stock, short futures).
     */
    Price getNetOrderDollarValueTraded(ProductClass pc);
}
