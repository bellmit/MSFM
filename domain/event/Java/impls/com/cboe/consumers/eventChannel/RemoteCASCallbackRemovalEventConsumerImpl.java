package com.cboe.consumers.eventChannel;

import com.cboe.idl.cmiUtil.CallbackInformationStruct;
import com.cboe.interfaces.events.RemoteCASCallbackRemovalConsumer;

public class RemoteCASCallbackRemovalEventConsumerImpl extends com.cboe.idl.events.POA_RemoteCASCallbackRemovalEventConsumer implements RemoteCASCallbackRemovalConsumer
{

    private RemoteCASCallbackRemovalConsumer delegate;

    public RemoteCASCallbackRemovalEventConsumerImpl(RemoteCASCallbackRemovalConsumer delegate)
    {
        super();
        this.delegate = delegate;
    }

    public void acceptCallbackRemoval(
            String casOrigin,
            String userId,
            String userSessionIOR,
            String reason,
            int errorCode,
            CallbackInformationStruct callbackInfo)
    {
        delegate.acceptCallbackRemoval(
             casOrigin,
             userId,
             userSessionIOR,
             reason,
             errorCode,
             callbackInfo);
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
