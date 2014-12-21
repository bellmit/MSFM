package com.cboe.consumers.eventChannel;

import com.cboe.interfaces.events.*;
import com.cboe.idl.cmiProduct.ProductKeysStruct;
import com.cboe.idl.cmiIntermarketMessages.AdminStruct;

public class IntermarketAdminMessageEventConsumerImpl extends com.cboe.idl.events.POA_IntermarketAdminMessageEventConsumer implements IntermarketAdminMessageConsumer
{

    private IntermarketAdminMessageConsumer delegate;

    public IntermarketAdminMessageEventConsumerImpl(IntermarketAdminMessageConsumer imAdminMessageConsumer)
    {
        super();
        delegate = imAdminMessageConsumer;
    }

    public void acceptIntermarketAdminMessage(String sessionName, String sourceExchange, ProductKeysStruct productKeys, AdminStruct adminMessage)
    {
        delegate.acceptIntermarketAdminMessage(sessionName, sourceExchange, productKeys, adminMessage);
    }
    public void acceptBroadcastIntermarketAdminMessage(
                      String sessionName,
                      String sourceExchange,
                      AdminStruct adminMessage)
    {
        delegate.acceptBroadcastIntermarketAdminMessage(sessionName, sourceExchange, adminMessage);
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
