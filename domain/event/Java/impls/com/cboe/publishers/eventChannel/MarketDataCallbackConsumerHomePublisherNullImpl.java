//
// -----------------------------------------------------------------------------------
// Source file: MarketDataCallbackConsumerHomePublisherNullImpl.java
//
// PACKAGE: com.cboe.publishers.eventChannel
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.publishers.eventChannel;

import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.DataValidationException;

import com.cboe.interfaces.events.IECMarketDataCallbackConsumerHome;
import com.cboe.interfaces.events.MarketDataCallbackConsumer;

import com.cboe.util.ChannelKey;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.foundationFramework.BOHome;

public class MarketDataCallbackConsumerHomePublisherNullImpl extends BOHome
        implements IECMarketDataCallbackConsumerHome
{
    private MarketDataCallbackConsumerPublisherImpl publisher;

    public MarketDataCallbackConsumerHomePublisherNullImpl()
    {
        super();
    }

    public MarketDataCallbackConsumer create()
    {
        return find();
    }

    public MarketDataCallbackConsumer find()
    {
        return publisher;
    }

    public void start()
    {
        try
        {
            // null delegate
            publisher = new MarketDataCallbackConsumerPublisherImpl(null);
            publisher.create(String.valueOf(publisher.hashCode()));
            //Every BObject must be added to the container.
            addToContainer(publisher);
        }
        catch(Exception e)
        {
            Log.exception(e);
        }
    }

    public void addFilter(ChannelKey channelKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        // NO IMPL NEEDED FOR THE PUBLISHER
    }

    public void removeFilter(ChannelKey channelKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        // NO IMPL NEEDED FOR THE PUBLISHER
    }

    public String getFullChannelName() throws Exception
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
