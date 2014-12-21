// -----------------------------------------------------------------------------------
// Source file: BusinessModelCollectionListenerEventMulticaster.java
//
// PACKAGE: com.cboe.presentation.common.businessModels;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.businessModels;

import java.util.*;

import com.cboe.interfaces.presentation.common.businessModels.BusinessModelCollectionListener;
import com.cboe.interfaces.presentation.common.businessModels.BusinessModelCollectionEvent;

import com.cboe.presentation.common.event.AbstractEventMulticaster;

/**
 * This is the event multicaster class to support the BusinessModelCollectionListener interface.
 */
public class BusinessModelCollectionListenerEventMulticaster extends AbstractEventMulticaster implements BusinessModelCollectionListener
{
    /**
     * Constructor to support multicast events.
     * @param listenerA BusinessModelCollectionListener
     * @param listenerB BusinessModelCollectionListener
     */
    protected BusinessModelCollectionListenerEventMulticaster(BusinessModelCollectionListener listenerA, BusinessModelCollectionListener listenerB)
    {
        super(listenerA, listenerB);
    }

    protected EventListener add(EventListener a, EventListener b)
    {
        return add((BusinessModelCollectionListener) a, (BusinessModelCollectionListener) b);
    }

    /**
     * Add new listener to support multicast events.
     * @param listenerA BusinessModelCollectionListener
     * @param listenerB BusinessModelCollectionListener
     * @return BusinessModelCollectionListener
     */
    public static BusinessModelCollectionListener add(BusinessModelCollectionListener listenerA, BusinessModelCollectionListener listenerB)
    {
        if(listenerA == null)
        {
            return listenerB;
        }
        if(listenerB == null)
        {
            return listenerA;
        }
        return new BusinessModelCollectionListenerEventMulticaster(listenerA, listenerB);
    }

    /**
     * Remove listener to support multicast events.
     * @param listenerA BusinessModelCollectionListener
     * @param listenerB BusinessModelCollectionListener
     * @return BusinessModelCollectionListener
     */
    public static BusinessModelCollectionListener remove(BusinessModelCollectionListener listenerA, BusinessModelCollectionListener listenerB)
    {
        EventListener listener = removeInternal(listenerA, listenerB);
        return (BusinessModelCollectionListener)listener;
    }

    /**
     * Interface method used to forward event to listeners
     * @param event EventObject to forward
     */
    public void elementAdded(BusinessModelCollectionEvent event)
    {
        ((BusinessModelCollectionListener)a).elementAdded(event);
        ((BusinessModelCollectionListener)b).elementAdded(event);
    }

    /**
     * Interface method used to forward event to listeners
     * @param event EventObject to forward
     */
    public void elementUpdated(BusinessModelCollectionEvent event)
    {
        ((BusinessModelCollectionListener)a).elementUpdated(event);
        ((BusinessModelCollectionListener)b).elementUpdated(event);
    }

    /**
     * Interface method used to forward event to listeners
     * @param event EventObject to forward
     */
    public void elementRemoved(BusinessModelCollectionEvent event)
    {
        ((BusinessModelCollectionListener)a).elementRemoved(event);
        ((BusinessModelCollectionListener)b).elementRemoved(event);
    }
}