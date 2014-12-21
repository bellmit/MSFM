//
// -----------------------------------------------------------------------------------
// Source file: IECMessageCollection.java
//
// PACKAGE: com.cboe.presentation.omt
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.omt;

import com.cboe.interfaces.presentation.omt.MessageElement;

import com.cboe.util.ChannelKey;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.EventChannelListener;

import com.cboe.presentation.api.APIHome;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.presentation.common.logging.GUILoggerHome;

public abstract class IECMessageCollection
        extends DefaultMessageCollection
        implements EventChannelListener
{
    protected String processMethodLogName = getClass().getName() + ".processEvent: ";
    private static final GUILoggerBusinessProperty loggingProperty = GUILoggerBusinessProperty.OMT;

    protected IECMessageCollection(Object processEventLockObject)
    {
        super(processEventLockObject);
        subscribe();
    }

    protected abstract int[] getChannelTypes();
    protected abstract void processEvent(int channelType, Object eventData);

    public void channelUpdate(ChannelEvent event)
    {
        Object eventData = event.getEventData();
        int channelType = ((ChannelKey) event.getChannel()).channelType;

        synchronized(eventProcessingLockObject)
        {
            processEvent(channelType, eventData);
        }
    }

    public void subscribe()
    {
        APIHome.findOrderManagementTerminalAPI().subscribeOMT(getChannelTypes(), this);
    }

    public void unsubscribe()
    {
        APIHome.findOrderManagementTerminalAPI().unsubscribeOMT(getChannelTypes(), this);
    }

    protected void processBasicEvent(Object eventData)
    {
            MessageElement[] newElements = MessageElementFactory.createMessageElement(eventData);
            addElements(newElements);
    }

    /*
    *  Finds and removes the message element using the messageID.
    */
    protected void processMessageRemoved(Object eventData)
    {
        Long messageID = (Long) eventData;

        //noinspection NonPrivateFieldAccessedInSynchronizedContext
        for(MessageElement element : elements)
        {
            if(element.getMessageId() == messageID)
            {
                removeMessageElement(element);
                break;
            }
        }
    }


    /*
    *      Logs receipt of orders and error conditions to the audit and debug logs.
    */
    protected void logMessage(String eventName, Object parm1, Object parm2)
    {
        logMessage(eventName, new Object[]{parm1, parm2});
    }

    /**
     * Use varargs so we're not limited to logging only two structs
     * @param eventName
     * @param parms
     */
    protected void logMessage(String eventName, Object... parms)
    {
        GUILoggerHome.find().audit(eventName, parms);

        if (GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(loggingProperty))
        {
            GUILoggerHome.find().debug(eventName, loggingProperty, parms);
        }
    }
}
