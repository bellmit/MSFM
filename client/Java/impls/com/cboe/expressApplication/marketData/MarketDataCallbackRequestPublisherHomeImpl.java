package com.cboe.expressApplication.marketData;

import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.interfaces.expressApplication.MarketDataCallbackRequestPublisherHome;
import com.cboe.interfaces.expressApplication.MarketDataCallbackRequestPublisher;

public class MarketDataCallbackRequestPublisherHomeImpl extends BOHome implements MarketDataCallbackRequestPublisherHome
{
    private MarketDataCallbackRequestPublisher marketDataCallbackRequestPublisher;
    public MarketDataCallbackRequestPublisherHomeImpl()
    {
        super();
    }

    public MarketDataCallbackRequestPublisher create()
        throws Exception
    {
        if (marketDataCallbackRequestPublisher == null)
        {
            MarketDataCallbackRequestPublisherImpl publisher = new MarketDataCallbackRequestPublisherImpl();
            //Every BOObject create MUST have a name...if the object is to be a managed object.
            publisher.create(String.valueOf(publisher.hashCode()));

            //Every bo object must be added to the container.
            addToContainer(publisher);

            publisher.initialize();

            marketDataCallbackRequestPublisher = publisher;
        }
        return marketDataCallbackRequestPublisher;
    }

    public MarketDataCallbackRequestPublisher find()
        throws Exception
    {
        return create();
    }
}
