//
// -----------------------------------------------------------------------------------
// Source file: UserMarketDataEventChannelSnapshot.java
//
// PACKAGE: com.cboe.presentation.api
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api;

import org.omg.CORBA.UserException;

import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.ChannelType;
import com.cboe.util.ChannelKey;

import com.cboe.presentation.common.logging.GUILoggerHome;

public class UserMarketDataEventChannelSnapshot extends AbstractEventChannelSnapshot
{
    private String sessionName;
    private int productKey;

    public UserMarketDataEventChannelSnapshot(int timeout, String sessionName, int productKey)
    {
        super(timeout);
        this.sessionName = sessionName;
        this.productKey = productKey;
    }

    protected String getExceptionMessage()
    {
        return "Timed out while waiting for UserMarketData event for session '" + sessionName +
               "', productKey " + productKey;
    }

    protected String getTimeoutMessage()
    {
        return "Timed out while waiting for UserMarketData event for session '" + sessionName +
               "', productKey " + productKey;
    }

    protected void subscribeEventChannel()
    {
        try
        {
            APIHome.findMarketQueryAPI().getUserMarketDataByProduct(sessionName, productKey, this);
        }
        catch(UserException e)
        {
            isExceptionThrown = true;
            GUILoggerHome.find().exception(e);
        }
    }

    protected void unsubscribeEventChannel()
    {
        try
        {
            APIHome.findMarketQueryAPI().unsubscribeUserMarketDataByProduct(sessionName, productKey, this);
        }
        catch(UserException e)
        {
            GUILoggerHome.find().exception(e);
        }
    }

    /**
     * channelUpdate is called by the event channel adapter when it dispatches an event to the
     * registered listeners.
     */
    public void channelUpdate(ChannelEvent event)
    {
        int channelType = ((ChannelKey) event.getChannel()).channelType;

        switch(channelType)
        {
            case ChannelType.CB_USER_MARKET_DATA_BY_PRODUCT:
                // eventData is a UserMarketDataStruct
                eventChannelData = event.getEventData();
                isChannelUpdated = true;
                break;
        }
    }
}
