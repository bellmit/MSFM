//------------------------------------------------------------------------------------------------------------------
// FILE:    TradingSessionEventReverseDateComparator.java
//
// PACKAGE: com.cboe.internalPresentation.common.comparators;
//
//-------------------------------------------------------------------------------------------------------------------
// Copyright (c) 2000 The Chicago Board Options Exchange. All Rights Reserved.
//
//-------------------------------------------------------------------------------------------------------------------
package com.cboe.internalPresentation.common.comparators;

import com.cboe.interfaces.internalPresentation.tradingSession.TradingSessionEvent;

import com.cboe.presentation.common.comparators.DateTimeComparator;

public class TradingSessionEventReverseDateComparator extends DateTimeComparator
{
    public TradingSessionEventReverseDateComparator()
    {
        super();
    }

    public int compare(Object o1, Object o2)
    {
        TradingSessionEvent event1 = ( TradingSessionEvent ) o1;
        TradingSessionEvent event2 = ( TradingSessionEvent ) o2;

        if (event1.equals(event2))
        {
            return 0;
        }

        return super.compare(event2.getDateTime(), event1.getDateTime());
    }
}