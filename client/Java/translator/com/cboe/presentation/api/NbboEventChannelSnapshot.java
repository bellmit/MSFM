package com.cboe.presentation.api;

import org.omg.CORBA.UserException;

import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;

import com.cboe.presentation.common.logging.GUILoggerHome;

public class NbboEventChannelSnapshot extends AbstractEventChannelSnapshot
{
    private String sessionName;
    private int productKey;

    private NbboEventChannelSnapshot()
    {
        this(0);
    }
    private NbboEventChannelSnapshot(int timeout)
    {
        super(timeout);
    }

    public NbboEventChannelSnapshot(String sessionName, int productKey)
    {
        this(0, sessionName, productKey);
    }

    public NbboEventChannelSnapshot(int timeout, String sessionName, int productKey)
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
            APIHome.findMarketQueryAPI().subscribeNBBOForProductV2(sessionName, productKey, this);
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
            APIHome.findMarketQueryAPI().unsubscribeNBBOForProductV2(sessionName, productKey, this);
        }
        catch(UserException e)
        {
            GUILoggerHome.find().exception(e);
        }
    }

    protected String getCommunicationExceptionDetail()
    {
        return "Timed out while waiting for NBBO event for session '"+sessionName+"', " +
               "productKey " + productKey;
    }

    // eventChannelData will be instance of CurrentMarketStruct (or CurrentMarketContainerImpl for V3)
    public void channelUpdate(ChannelEvent event)
    {
        int channelType = ((ChannelKey)event.getChannel()).channelType;

        switch(channelType)
        {
            case ChannelType.CB_NBBO_BY_PRODUCT:
                super.eventChannelData = event.getEventData();
                isChannelUpdated = true;
                break;
        }
    }

    // String to be used as CommunicationException detail message
    protected String getExceptionMessage()
    {
        return "Timed out while waiting for NBBO event for session '" + sessionName + "', " +
               "productKey " + productKey;
    }

    protected String getTimeoutMessage()
    {
        return "Timed out while waiting for NBBO event for session '" + sessionName + "', " +
               "productKey " + productKey;
    }


    protected Object getEventData()
    {
        return this.eventChannelData;
    }
}