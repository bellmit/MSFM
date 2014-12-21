//
// -----------------------------------------------------------------------------------
// Source file: ExchangeVolumeComparator.java
//
// PACKAGE: com.cboe.presentation.common.comparators
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.comparators;

import java.util.*;

import com.cboe.interfaces.presentation.marketData.ExchangeVolume;

/**
 * Sorts the ExchangeVolumes based on their exchanges' positions in the array returned by
 * ExchangeCharacterTypes.getExchangeOrder().
 *
 * Any exchanges that aren't found in that array will be sorted last.
 */
public class ExchangeVolumeComparator implements Comparator<ExchangeVolume>
{
    private ExchangePreferredOrderComparator delegateComparator;

    public ExchangeVolumeComparator()
    {
        delegateComparator = new ExchangePreferredOrderComparator();
    }

    public int compare(ExchangeVolume exchangeA, ExchangeVolume exchangeB)
    {
        return delegateComparator.compare(exchangeA.getExchange(), exchangeB.getExchange());
    }
}
