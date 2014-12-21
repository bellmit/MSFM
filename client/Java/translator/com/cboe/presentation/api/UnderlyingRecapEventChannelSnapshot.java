package com.cboe.presentation.api;

import com.cboe.interfaces.presentation.product.SessionProductClass;
import com.cboe.interfaces.presentation.product.SessionReportingClass;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.api.AbstractEventChannelSnapshot;
import com.cboe.presentation.api.APIHome;
import org.omg.CORBA.UserException;

public class UnderlyingRecapEventChannelSnapshot extends AbstractEventChannelSnapshot
{
    private String underlyingSessionName;
    private int underlyingClassKey;
    private int underlyingProductKey;

    private UnderlyingRecapEventChannelSnapshot()
    {
        this(0);
    }

    private UnderlyingRecapEventChannelSnapshot(int timeout)
    {
        super(timeout);
    }

    public UnderlyingRecapEventChannelSnapshot(SessionReportingClass reportingClass)
    {
        this(0, reportingClass);
    }

    public UnderlyingRecapEventChannelSnapshot(int timeout, SessionReportingClass reportingClass)
    {
        this(timeout, reportingClass.getSessionProductClass());
    }

    public UnderlyingRecapEventChannelSnapshot(SessionProductClass productClass)
    {
        this(0, productClass);
        setSessionProductClass(productClass);
    }

    public UnderlyingRecapEventChannelSnapshot(int timeout, SessionProductClass productClass)
    {
        this(timeout);
        setSessionProductClass(productClass);
    }

    private void setSessionProductClass(SessionProductClass productClass)
    {
        this.underlyingSessionName = productClass.getUnderlyingSessionName();
        this.underlyingProductKey = productClass.getUnderlyingProduct().getProductKeysStruct().productKey;
        this.underlyingClassKey = productClass.getUnderlyingProduct().getProductKeysStruct().classKey;
        isChannelUpdated = false;
    }

    protected void subscribeEventChannel()
    {
        try
        {
//          APIHome.findMarketQueryAPI().subscribeRecapForClass(underlyingSessionName, underlyingClassKey, this);
//          APIHome.findMarketQueryAPI().subscribeRecapForProduct(underlyingSessionName, underlyingProductKey, this);
            APIHome.findMarketQueryAPI().subscribeRecapForProductV2(underlyingSessionName, underlyingProductKey, this);
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
//            APIHome.findMarketQueryAPI().unsubscribeRecapForClass(underlyingSessionName, underlyingClassKey, this);
            APIHome.findMarketQueryAPI().unsubscribeRecapForProduct(underlyingSessionName, underlyingProductKey, this);
        }
        catch(UserException e)
        {
            GUILoggerHome.find().exception(e);
        }
    }

    protected String getExceptionMessage()
    {
//        return "Timed out while waiting for RecapStruct event for underlying session '"+underlyingSessionName+"', underlying classKey "+underlyingClassKey;
        return "Timed out while waiting for RecapStruct event for underlying session '"+underlyingSessionName+"', underlying productKey "+underlyingProductKey;
    }

    // eventChannelData will be instance of RecapStruct
    public void channelUpdate(ChannelEvent event)
    {
        int channelType = ((ChannelKey)event.getChannel()).channelType;
        switch(channelType)
        {
            case ChannelType.CB_RECAP_BY_PRODUCT:
                super.eventChannelData = event.getEventData();
                isChannelUpdated = true;
                break;
        }
    }

    protected String getTimeoutMessage()
    {
        return "Timed out while waiting for RecapStruct event for underlying session '" + underlyingSessionName + "', underlying productKey " + underlyingProductKey;
    }

    protected Object getEventData()
    {
        return this.eventChannelData;
    }
}

