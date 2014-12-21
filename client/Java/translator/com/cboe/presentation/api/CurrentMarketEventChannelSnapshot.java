package com.cboe.presentation.api;

import com.cboe.interfaces.presentation.api.TraderAPI;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.api.AbstractEventChannelSnapshot;
import com.cboe.presentation.api.APIHome;
import org.omg.CORBA.UserException;

public class CurrentMarketEventChannelSnapshot extends AbstractEventChannelSnapshot
{
    private String sessionName;
    private int productKey;

    private CurrentMarketEventChannelSnapshot()
    {
        this(0);
    }
    private CurrentMarketEventChannelSnapshot(int timeout)
    {
        super(timeout);
    }

    public CurrentMarketEventChannelSnapshot(String sessionName, int productKey)
    {
        this(0, sessionName, productKey);
    }

    public CurrentMarketEventChannelSnapshot(int timeout, String sessionName, int productKey)
    {
        this(timeout);
        this.sessionName = sessionName;
        this.productKey = productKey;
        isChannelUpdated = false;
    }

    protected void subscribeEventChannel()
    {
        try
        {
            APIHome.findMarketQueryAPI().subscribeCurrentMarketForProductV3(sessionName, productKey, this);
        }
        catch(UserException e)
        {
            this.isExceptionThrown = true;
            GUILoggerHome.find().exception(e);
        }
    }

    protected void unsubscribeEventChannel()
    {
        try
        {
            APIHome.findMarketQueryAPI().unsubscribeCurrentMarketForProductV3(sessionName, productKey, this);
        }
        catch(UserException e)
        {
            GUILoggerHome.find().exception(e);
        }
    }

    protected String getCommunicationExceptionDetail()
    {
        return "Timed out while waiting for CurrentMarket event for session '"+sessionName+"', productKey "+productKey;
    }

    // eventChannelData will be instance of CurrentMarketStruct (or CurrentMarketContainerImpl for V3)
    public void channelUpdate(ChannelEvent event)
    {
        int channelType = ((ChannelKey)event.getChannel()).channelType;

        switch(channelType)
        {
            case ChannelType.CB_CURRENT_MARKET_BY_PRODUCT_V3:
                super.eventChannelData = event.getEventData();
                isChannelUpdated = true;
                break;
        }
    }

    // String to be used as CommunicationException detail message
    protected String getExceptionMessage()
    {
        return "Timed out while waiting for CurrentMarket event for session '" + sessionName + "', productKey " + productKey;
    }

    protected String getTimeoutMessage()
    {
        return "Timed out while waiting for CurrentMarket event for session '" + sessionName + "', productKey " + productKey;
    }


    protected Object getEventData()
    {
        return this.eventChannelData;
    }
}

