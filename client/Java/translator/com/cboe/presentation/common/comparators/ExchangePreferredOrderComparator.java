//
// -----------------------------------------------------------------------------------
// Source file: ExchangePreferredOrderComparator.java
//
// PACKAGE: com.cboe.presentation.common.comparators
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.comparators;

import java.util.*;

import com.cboe.presentation.common.formatters.ExchangeCharacterTypes;

/**
 * Sorts the exchanges based on their positions in the array returned by
 * ExchangeCharacterTypes.getExchangeOrder().
 *
 * Any exchanges that aren't found in that array will be sorted last.
 */
public class ExchangePreferredOrderComparator implements Comparator<String>
{
    private List<String> exchangeOrderList;

    public ExchangePreferredOrderComparator()
    {
        String[] exchangeOrder = ExchangeCharacterTypes.getExchangeOrder();
        exchangeOrderList = Arrays.asList(exchangeOrder);
    }

    public int compare(String exchangeA, String exchangeB)
    {
        int retVal;
        // get the index of the ExchangeIndicators' exchanges in the "order" list
        Integer indexA = exchangeOrderList.indexOf(exchangeA);
        Integer indexB = exchangeOrderList.indexOf(exchangeB);

        // if neither exchange is in the list, compare them alphabetically
        if(indexA == -1 && indexB == -1)
        {
            retVal = exchangeA.compareTo(exchangeB);
        }
        // if exhcangeA is in the list, but exchangeB isn't, sort exchangeA first
        else if(indexB == -1)
        {
            retVal = -1;
        }
        // if exhcangeB is in the list, but exchangeA isn't, sort exchangeB first
        else if(indexA == -1)
        {
            retVal = 1;
        }
        // if they're both in the list compare their indices
        else
        {
            retVal = indexA.compareTo(indexB);
        }
        return retVal;
    }
}
