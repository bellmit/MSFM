package com.cboe.consumers.eventChannel;

import com.cboe.interfaces.events.RemoteCASSessionManagerConsumer;

public class RemoteCASSessionManagerEventConsumerImpl extends com.cboe.idl.events.POA_RemoteCASSessionManagerEventConsumer implements RemoteCASSessionManagerConsumer
{

    private RemoteCASSessionManagerConsumer delegate;

    public RemoteCASSessionManagerEventConsumerImpl(RemoteCASSessionManagerConsumer delegate)
    {
        super();
        this.delegate = delegate;
    }

    public void logout(String casOrigin, String userSessionIOR, String userId)
    {
        delegate.logout(casOrigin, userSessionIOR, userId);
    }

    
    public org.omg.CORBA.Object get_typed_consumer()
    {
        return null;
    }

    public void push(org.omg.CORBA.Any data)
        throws org.omg.CosEventComm.Disconnected
    {
    }

    public void disconnect_push_consumer()
    {
    }
}
