package com.cboe.application.supplier.proxy;

import com.cboe.domain.supplier.proxy.BaseSupplierProxy;
import com.cboe.domain.util.InstrumentorNameHelper;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.domain.instrumentedChannel.InstrumentedChannelListener;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.channel.ChannelAdapter;
import org.omg.CORBA.UserException;

/**
 * @author Jing Chen
 */
public abstract class InstrumentedCollectorProxy extends BaseSupplierProxy implements InstrumentedChannelListener
{
    private final static String COLLECTOR_PROXY_NAME = "CollectorProxy";
    public InstrumentedCollectorProxy(BaseSessionManager sessionManager, ChannelAdapter adapter, Object hashKey)
    {
        super(sessionManager, adapter);
        setHashKey(hashKey);
        setName();
    }

    public InstrumentedCollectorProxy(BaseSessionManager sessionManager, ChannelAdapter adapter, short queuePolicy)
    {
        super(sessionManager, adapter, queuePolicy);
        setHashKey(hashKey);
        setName();
    }

    public void setName()
    {
        try
        {
            name = InstrumentorNameHelper.createInstrumentorName(new String[]{
                sessionManager.getInstrumentorName(),
                getMessageType(),
                COLLECTOR_PROXY_NAME}, this);
        }
        catch (UserException e)
        {
            Log.exception(e);
        }
    }

    public abstract String getMessageType();

    public void startMethodInstrumentation(boolean privateOnly)
    {
        // do nothing for collectorProxy.
    }

    public void stopMethodInstrumentation()
    {
        // do nothing for collectorProxy.
    }

    public void queueInstrumentationInitiated()
    {
        // do nothing for collectorProxy
    }
}
