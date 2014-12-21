package com.cboe.consumers.eventChannel;

import com.cboe.interfaces.events.RemoteCASRecoveryConsumer;


public class RemoteCASRecoveryEventConsumerImpl extends com.cboe.idl.events.POA_RemoteCASRecoveryEventConsumer implements RemoteCASRecoveryConsumer
{

    private RemoteCASRecoveryConsumer delegate;

    public RemoteCASRecoveryEventConsumerImpl(RemoteCASRecoveryConsumer delegate)
    {
        super();
        this.delegate = delegate;
    }

    public void acceptMarketDataRecoveryForGroup(int groupKey)
    {
        delegate.acceptMarketDataRecoveryForGroup(groupKey);
    }

    public void acceptMDXRecoveryForGroup(int mdxGroupKey)
    {
        delegate.acceptMDXRecoveryForGroup(mdxGroupKey);
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
