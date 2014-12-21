//
// -----------------------------------------------------------------------------------
// Source file: ExchangeIndicatorComparator.java
//
// PACKAGE: com.cboe.presentation.common.comparators
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.comparators;

import java.util.*;

import com.cboe.interfaces.presentation.marketData.ExchangeIndicator;

/**
 * Sorts the ExchangeIndicators based on their exchanges' positions in the array returned by
 * ExchangeCharacterTypes.getExchangeOrder().
 *
 * Any exchanges that aren't found in that array will be sorted last.
 */
public class ExchangeIndicatorComparator implements Comparator<ExchangeIndicator>
{
    private ExchangePreferredOrderComparator delegateComparator;

    public ExchangeIndicatorComparator()
    {
        delegateComparator = new ExchangePreferredOrderComparator();
    }

    public int compare(ExchangeIndicator exchangeA, ExchangeIndicator exchangeB)
    {
        return delegateComparator.compare(exchangeA.getExchange(), exchangeB.getExchange());
    }
}
