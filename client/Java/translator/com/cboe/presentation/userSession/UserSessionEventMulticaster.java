//
// -----------------------------------------------------------------------------------
// Source file: UserSessionEventMulticaster.java
//
// PACKAGE: com.cboe.presentation.userSession;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.userSession;

import java.awt.AWTEventMulticaster;
import java.util.*;

/**
 * This is the event multicaster class to support the
 * UserSessionListener interface.
 */
public class UserSessionEventMulticaster extends AWTEventMulticaster implements UserSessionListener
{
    /**
     * Constructor to support multicast events.
     * @param listenerA EventListener
     * @param listenerB EventListener
     */
    protected UserSessionEventMulticaster(EventListener listenerA, EventListener listenerB)
    {
        super(listenerA, listenerB);
    }

    /**
     * Add new listener to support multicast events.
     * @param listenerA UserSessionListener
     * @param listenerB UserSessionListener
     * @return UserSessionListener
     */
    public static UserSessionListener add(UserSessionListener listenerA, UserSessionListener listenerB)
    {
        return (UserSessionListener) addInternal(listenerA, listenerB);
    }

    /**
     * Remove a listener from supporting multicast events.
     * @param listeners UserSessionListener
     * @param oldListener UserSessionListener
     * @return UserSessionListener
     */
    public static UserSessionListener remove(UserSessionListener listeners, UserSessionListener oldListener)
    {
        if(listeners == oldListener || listeners == null)
        {
            return null;
        }
        if(listeners instanceof UserSessionEventMulticaster)
        {
            return (UserSessionListener)((UserSessionEventMulticaster)listeners).remove(oldListener);
        }

        return listeners;
    }

    /**
     * Add new listener to support multicast events.
     * @param listenerA EventListener
     * @param listenerB EventListener
     * @return EventListener
     */
    protected static EventListener addInternal(EventListener listenerA, EventListener listenerB)
    {
        if(listenerA == null)
        {
            return listenerB;
        }

        if(listenerB == null)
        {
            return listenerA;
        }

        return new UserSessionEventMulticaster(listenerA, listenerB);
    }

    /**
     * Remove a listener from supporting multicast events.
     * @param oldListener UserSessionListener
     * @return EventListener
     */
    protected EventListener remove(EventListener oldListener)
    {
        if(oldListener == a)
        {
            return b;
        }

        if(oldListener == b)
        {
            return a;
        }

        EventListener a2 = removeInternal(a, oldListener);
        EventListener b2 = removeInternal(b, oldListener);

        if(a2 == a && b2 == b)
        {
            return this;
        }

        return addInternal(a2, b2);
    }
    
    protected static EventListener removeInternal(EventListener l, EventListener oldl) {
    	if (l == oldl || l == null) {
    	    return null;
    	} else if (l instanceof UserSessionEventMulticaster) {
    	    return ((UserSessionEventMulticaster)l).remove(oldl);
    	} else {
    	    return l;		// it's not here
    	}
    }

    /**
     * Delivers event to listeners
     * @param event UserSessionEvent
     */
    public void userSessionChange(UserSessionEvent event)
    {
        ((UserSessionListener)a).userSessionChange(event);
        ((UserSessionListener)b).userSessionChange(event);
    }
}