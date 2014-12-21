//
// -----------------------------------------------------------------------------------
// Source file: CentralLoggingConsumerImpl.java
//
// PACKAGE: com.cboe.presentation.alarms.events.consumers
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.alarms.events.consumers;

import com.cboe.idl.infrastructureServices.loggingService.corba.Message;

import com.cboe.interfaces.events.CentralLoggingConsumer;

import com.cboe.util.event.EventChannelAdapter;
import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;

import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerINBusinessProperty;

/**
 *@author shanbhag
 */
public class CentralLoggingConsumerImpl implements CentralLoggingConsumer
{
    protected EventChannelAdapter eventChannel;

    public CentralLoggingConsumerImpl()
    {
        super();
        initialize();
    }

    private void initialize()
    {
        getEventChannelAdapter();
    }

    protected EventChannelAdapter getEventChannelAdapter()
    {
        if( eventChannel == null )
        {
            eventChannel = EventChannelAdapterFactory.find();
        }
        return eventChannel;
    }

    protected void dispatchEvent( int channelType, MessageDetail messageDetail )
    {
        ChannelEvent channelEvent =
                eventChannel.getChannelEvent( this, new ChannelKey( channelType, new Integer( 0 ) ), messageDetail );
        eventChannel.dispatch( channelEvent );
    }

    public void log( Message message )
    {
        if( (message != null) )
        {
            if( GUILoggerHome.find().isDebugOn() &&
                    GUILoggerHome.find().isPropertyOn( GUILoggerINBusinessProperty.XTP ) )
            {
                GUILoggerHome.find().debug( "log() - " + "Priority : " + message.priority.value(),
                                            GUILoggerINBusinessProperty.XTP, message );
            }
            dispatchEvent( ChannelType.XTP_ALERT_NOTIFICATION_NEW_UPDATE, new MessageDetail(message, false) );
        }
    }

    public void logSync( Message message )
    {

    }

    public void clearAlarm( Message message )
    {
        if( ( message != null ) )
        {
            if( GUILoggerHome.find().isDebugOn() &&
                    GUILoggerHome.find().isPropertyOn( GUILoggerINBusinessProperty.XTP ) )
            {
                GUILoggerHome.find().debug( "clearAlarm() - " +  "Priority : " + message.priority.value(),
                                            GUILoggerINBusinessProperty.XTP, message );
            }

            dispatchEvent( ChannelType.XTP_ALERT_NOTIFICATION_NEW_UPDATE, new MessageDetail( message, true ) );
        }
    }

    public org.omg.CORBA.Object get_typed_consumer()
    {
        return null;
    }

    public void push( org.omg.CORBA.Any data )
            throws org.omg.CosEventComm.Disconnected
    {
    }

    public void disconnect_push_consumer()
    {
    }
}