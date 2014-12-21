//
// -----------------------------------------------------------------------------------
// Source file: UserSessionEvent.java
//
// PACKAGE: com.cboe.presentation.userSession;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.userSession;

import java.util.*;

/**
 * Represents an event that occurred for a UserSession.
 */
public class UserSessionEvent extends EventObject
{
    private Date actionTime = null;
    private int actionType;
    public static final transient int LOGGED_OUT_EVENT = 1;
    public static final transient int LOG_OUT_PENDING_EVENT = 2;
    public static final transient int LOGGED_IN_EVENT = 3;
    public static final transient int PASSWORD_CHANGE_EVENT = 4;
    public static final transient int FORCED_LOGGED_OUT_EVENT = 5;

    /**
     * Constructor
     * @param source that the event occurred for.
     */
    protected UserSessionEvent(Object source)
    {
        super(source);
    }

    /**
     * Constructor
     * @param source that the event occurred for
     * @param actionTime that event occurred
     * @param actionType type of event. Must be one of the predefined events.
     */
    public UserSessionEvent(Object source, Date actionTime, int actionType)
    {
        this(source);
        this.actionTime = actionTime;

        if(actionType != LOGGED_OUT_EVENT && actionType != LOGGED_IN_EVENT && actionType != PASSWORD_CHANGE_EVENT &&
           actionType != LOG_OUT_PENDING_EVENT && actionType != FORCED_LOGGED_OUT_EVENT)
        {
            throw new IllegalArgumentException("Invalid actiontype: " + actionType);
        }
        this.actionType = actionType;
    }

    /**
     * Returns the time the event occurred.
     * @return Date that event occurred
     */
    public Calendar getActionTime()
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(actionTime);
        return cal;
    }

    /**
     * Returns the action type. Should represent one of the predefined constants in this event.
     * @return action type
     */
    public int getActionType()
    {
        return actionType;
    }
}