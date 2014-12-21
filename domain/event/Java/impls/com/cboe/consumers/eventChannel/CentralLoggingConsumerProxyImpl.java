//
// -----------------------------------------------------------------------------------
// Source file: CentralLoggingConsumerProxyImpl.java
//
// PACKAGE: com.cboe.consumers.eventChannel
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.consumers.eventChannel;

import com.cboe.interfaces.events.CentralLoggingConsumer;

import com.cboe.idl.infrastructureServices.loggingService.corba.POA_LoggingServer;
import com.cboe.idl.infrastructureServices.loggingService.corba.Message;

/**
 * @author shanbhag
 */
public class CentralLoggingConsumerProxyImpl
                    extends POA_LoggingServer
                    implements CentralLoggingConsumer
{

    protected CentralLoggingConsumer delegate;

    public CentralLoggingConsumerProxyImpl( CentralLoggingConsumer delegate )
    {
        super();
        this.delegate = delegate;
    }

    public void log( Message message )
    {
        delegate.log( message );
    }

    public void logSync( Message message )
    {
        delegate.logSync(message);
    }

    public void clearAlarm( Message message )
    {
        delegate.clearAlarm(message);
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
