package com.cboe.application.marketData;

import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.interfaces.application.MarketDataRequestPublisherHome;
import com.cboe.interfaces.application.MarketDataRequestPublisher;

/**
 * @author Jing Chen
 */
public class MarketDataRequestPublisherHomeImpl extends BOHome implements MarketDataRequestPublisherHome
{
    private MarketDataRequestPublisher marketDataRequestPublisher;
    public MarketDataRequestPublisherHomeImpl()
    {
        super();
    }

    public MarketDataRequestPublisher create()
        throws Exception
    {
        if (marketDataRequestPublisher == null)
        {
            MarketDataRequestPublisherImpl publisher = new MarketDataRequestPublisherImpl();
            //Every BOObject create MUST have a name...if the object is to be a managed object.
            publisher.create(String.valueOf(publisher.hashCode()));

            //Every bo object must be added to the container.
            addToContainer(publisher);

            publisher.initialize();

            marketDataRequestPublisher = publisher;
        }
        return marketDataRequestPublisher;
    }

    public MarketDataRequestPublisher find()
        throws Exception
    {
        return create();
    }

}
