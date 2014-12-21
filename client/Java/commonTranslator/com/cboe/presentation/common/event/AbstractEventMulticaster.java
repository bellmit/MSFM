//
// -----------------------------------------------------------------------------------
// Source file: AbstractEventMulticaster.java
//
// PACKAGE: com.cboe.presentation.common.event
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.event;

import java.awt.*;
import java.util.*;

public abstract class AbstractEventMulticaster extends AWTEventMulticaster
{
    protected AbstractEventMulticaster(EventListener a, EventListener b)
    {
        super(a, b);
    }

    /**
     * Adds the correct implementation fo EventListener to support
     * multicast events, rather than always adding a new AWTEventMulticaster.
     */
    abstract protected EventListener add(EventListener a, EventListener b);

    /**
     * Overriding remove(EventListener) because that method calls the static
     * AWTEventMulticaster.addInternal() which may add a new
     * AWTEventMulticaster to this, when the subclass expects only instances
     * of a different type of EventListener to be added (e.g., only instances
     * of itself).
     *
     * Instead of calling the static addInternal(), this will call the
     * subclass's implementation of add(EventListener, EventListener), so that
     * the correct implementation of EventListener can be added.
     * @param oldl the EventListener to be removed
     */
    protected EventListener remove(EventListener oldl)
    {
        if(oldl == a)
        {
            return b;
        }
        if(oldl == b)
        {
            return a;
        }
        EventListener a2 = removeInternal(a, oldl);
        EventListener b2 = removeInternal(b, oldl);
        if(a2 == a && b2 == b)
        {
            return this;	// it's not here
        }
        // the only change from super.remove() is to call this.add() instead of the static addInternal()
        return add(a2, b2);
    }
}
