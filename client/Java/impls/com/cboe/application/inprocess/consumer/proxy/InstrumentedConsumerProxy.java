package com.cboe.application.inprocess.consumer.proxy;

import com.cboe.interfaces.domain.instrumentedChannel.InstrumentedEventChannelListener;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.domain.util.InstrumentorNameHelper;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import org.omg.CORBA.UserException;

public abstract class InstrumentedConsumerProxy extends BaseConsumerProxy implements InstrumentedEventChannelListener
{
    protected BaseSessionManager sessionManager;
    protected String name;
    private final static String CONSUMER_PROXY_NAME = "ConsumerProxy";

    public InstrumentedConsumerProxy(Object hashKey, BaseSessionManager sessionManager)
    {
        setHashKey(hashKey);
        this.sessionManager = sessionManager;
        setName();
    }
    public void setName()
    {
        try
        {
            name = InstrumentorNameHelper.createInstrumentorName(new String[]{
                sessionManager.getInstrumentorName(),
                getMessageType(),
                CONSUMER_PROXY_NAME}, this);
        }
        catch(UserException e)
        {
            Log.exception(e);
        }
    }
    public String getName()
    {
        return name;
    }

    public abstract String getMessageType();
}
