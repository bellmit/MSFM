//------------------------------------------------------------------------------------------------------------------
// FILE:    RegisteredServerEventReverseDateComparator.java
//
// PACKAGE: com.cboe.internalPresentation.common.comparators;
//
//-------------------------------------------------------------------------------------------------------------------
// Copyright (c) 2000 The Chicago Board Options Exchange. All Rights Reserved.
//
//-------------------------------------------------------------------------------------------------------------------
package com.cboe.internalPresentation.common.comparators;

import com.cboe.interfaces.internalPresentation.tradingSession.RegisteredServerEvent;

import com.cboe.presentation.common.comparators.DateTimeComparator;

public class RegisteredServerEventReverseDateComparator extends DateTimeComparator
{
    public RegisteredServerEventReverseDateComparator()
    {
        super();
    }

    public int compare(Object o1, Object o2)
    {
        RegisteredServerEvent event1 = ( RegisteredServerEvent ) o1;
        RegisteredServerEvent event2 = ( RegisteredServerEvent ) o2;

        if (event1.equals(event2))
        {
            return 0;
        }

        return super.compare(event2.getDateTime(), event1.getDateTime());
    }
}