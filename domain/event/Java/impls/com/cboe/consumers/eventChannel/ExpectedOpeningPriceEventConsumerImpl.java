package com.cboe.consumers.eventChannel;

import org.omg.CORBA.Any;
import org.omg.CORBA.Object;
import org.omg.CosEventComm.Disconnected;

import com.cboe.idl.cmiMarketData.ExpectedOpeningPriceStruct;
import com.cboe.idl.util.RoutingParameterStruct;
import com.cboe.interfaces.events.CurrentMarketConsumer;
import com.cboe.interfaces.events.ExpectedOpeningPriceConsumer;

public class ExpectedOpeningPriceEventConsumerImpl 
    extends com.cboe.idl.events.POA_ExpectedOpeningPriceEventConsumer
    implements ExpectedOpeningPriceConsumer
{
    private ExpectedOpeningPriceConsumer delegate;
    
    public ExpectedOpeningPriceEventConsumerImpl(ExpectedOpeningPriceConsumer eopConsumer)
    {
        super();
        delegate = eopConsumer;
    }

    public void acceptExpectedOpeningPrice(int[] groups, ExpectedOpeningPriceStruct expectedOpeningPrices)
    {
        delegate.acceptExpectedOpeningPrice(groups, expectedOpeningPrices);
    }

    public void acceptExpectedOpeningPricesForClass(RoutingParameterStruct routing, ExpectedOpeningPriceStruct[] expectedOpeningPrices)
    {
        delegate.acceptExpectedOpeningPricesForClass(routing,expectedOpeningPrices );
        
    }

    public Object get_typed_consumer()
    {
        return null;
    }

    public void disconnect_push_consumer()
    {        
    }

    public void push(Any data) throws Disconnected
    {   
    }

}
