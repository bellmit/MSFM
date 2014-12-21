//
// ------------------------------------------------------------------------
// FILE: AlarmConsumerImpl.java
// 
// PACKAGE: com.cboe.presentation.alarms.events.consumers
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2005 The Chicago Board Options Exchange.  All Rights Reserved.
// ------------------------------------------------------------------------
//

package com.cboe.presentation.alarms.events.consumers;

import com.cboe.exceptions.ExceptionDetails;

import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.util.event.EventChannelAdapterFactory;

import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerINBusinessProperty;

/**
 * @author torresl@cboe.com
 */
public class AlarmConsumerImpl
{
    protected EventChannelAdapter eventChannel;

    public AlarmConsumerImpl()
    {
        eventChannel = null;
    }

    protected EventChannelAdapter getEventChannelAdapter()
    {
        if (eventChannel == null)
        {
            eventChannel = EventChannelAdapterFactory.find();
        }
        return eventChannel;
    }

    protected void dispatchEvent(int channelType, long requestId, Object object)
    {
        ChannelEvent channelEventAll =
                getEventChannelAdapter().getChannelEvent(this, new ChannelKey(channelType, new Integer(0)), object);
        ChannelEvent channelEventByRequest =
                getEventChannelAdapter().getChannelEvent(this, new ChannelKey(channelType, new Long(requestId)),
                                                         object);
        getEventChannelAdapter().dispatch(channelEventByRequest);
        getEventChannelAdapter().dispatch(channelEventAll);
    }

    protected void dispatchException(long requestId, ExceptionDetails exceptionDetails, String exceptionName, int channelType)
    {
        if(GUILoggerHome.find().isDebugOn() &&
           GUILoggerHome.find().isPropertyOn(GUILoggerINBusinessProperty.ALARM_EXCEPTIONS))
        {
            GUILoggerHome.find().debug(exceptionName + " (requestId=" + requestId + ')',
                                       GUILoggerINBusinessProperty.ALARM_EXCEPTIONS, exceptionDetails);
        }
        dispatchEvent(channelType, requestId, exceptionDetails);

    }

    public void acceptAlreadyExistsException(long requestId, ExceptionDetails exceptionDetails)
    {
        dispatchException(requestId, exceptionDetails, "acceptAlreadyExistsException",
                          ChannelType.IC_ACCEPT_ALREADY_EXISTS_EXCEPTION);
    }

    public void acceptDataValidationException(long requestId, ExceptionDetails exceptionDetails)
    {
        dispatchException(requestId, exceptionDetails, "acceptDataValidationException",
                          ChannelType.IC_ACCEPT_DATA_VALIDATION_EXCEPTION);
    }

    public void acceptNotAcceptedException(long requestId, ExceptionDetails exceptionDetails)
    {
        dispatchException(requestId, exceptionDetails, "acceptNotAcceptedException",
                          ChannelType.IC_ACCEPT_NOT_ACCEPTED_EXCEPTION);
    }

    public void acceptNotFoundException(long requestId, ExceptionDetails exceptionDetails)
    {
        dispatchException(requestId, exceptionDetails, "acceptNotFoundException",
                          ChannelType.IC_ACCEPT_NOT_FOUND_EXCEPTION);
    }

    public void acceptSystemException(long requestId, ExceptionDetails exceptionDetails)
    {
        dispatchException(requestId, exceptionDetails, "acceptSystemException",
                          ChannelType.IC_ACCEPT_SYSTEM_EXCEPTION);
    }

    public void acceptTransactionFailedException(long requestId, ExceptionDetails exceptionDetails)
    {
        dispatchException(requestId, exceptionDetails, "acceptTransactionFailedException",
                          ChannelType.IC_ACCEPT_TRANSACTION_FAILED_EXCEPTION);
    }
}
