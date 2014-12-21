//
// -----------------------------------------------------------------------------------
// Source file: TradingSessionComparator.java
//
// PACKAGE: com.cboe.presentation.common.comparators
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.comparators;

import java.util.*;

import com.cboe.interfaces.presentation.tradingSession.TradingSession;

public class TradingSessionComparator implements Comparator
{
    public TradingSessionComparator()
    {
        super();
    }

    public int compare(Object o1, Object o2)
    {
        return ((TradingSession)o1).compareTo(o2);
    }
}
