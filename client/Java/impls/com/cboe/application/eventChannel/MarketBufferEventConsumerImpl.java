package com.cboe.application.eventChannel;

/**
 * MarketBuffer listener object listens on the CBOE event channel.
 * There are multiple MarketBuffer listener objects per client, one per channel.
 */
import com.cboe.idl.cmiMarketData.*;
import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.util.RoutingParameterStruct;
import com.cboe.interfaces.events.*;
import com.cboe.util.*;
import com.cboe.util.event.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.systemsManagementService.*;
import com.cboe.infrastructureServices.orbService.*;
import com.cboe.infrastructureServices.eventService.*;
import com.cboe.infrastructureServices.traderService.*;

public class MarketBufferEventConsumerImpl extends com.cboe.idl.events.POA_MarketBufferEventConsumer implements MarketBufferConsumer
{
    private MarketBufferConsumer delegate;

    public MarketBufferEventConsumerImpl(MarketBufferConsumer marketBufferConsumer)
    {
        delegate = marketBufferConsumer;
        // todo
    }

    public void acceptMarketBuffer(int serverKey, int mdcassetKey, byte[] buffer)
    {
        delegate.acceptMarketBuffer(serverKey, mdcassetKey, buffer);
    }

    public org.omg.CORBA.Object get_typed_consumer()
    {
        return null;
    }

    public void push(org.omg.CORBA.Any data)
        throws org.omg.CosEventComm.Disconnected 
    { }

    public void disconnect_push_consumer()
    { }
}
