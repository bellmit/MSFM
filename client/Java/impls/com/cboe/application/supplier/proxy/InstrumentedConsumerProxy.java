package com.cboe.application.supplier.proxy;

import com.cboe.domain.supplier.proxy.CallbackSupplierProxy;
import com.cboe.domain.util.InstrumentorNameHelper;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.domain.instrumentedChannel.InstrumentedChannelListener;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.channel.ChannelAdapter;
import org.omg.CORBA.UserException;

/**
 * @author Jing Chen
 */
public abstract class InstrumentedConsumerProxy extends CallbackSupplierProxy implements InstrumentedChannelListener
{
    private final static String CONSUMER_PROXY_NAME = "ConsumerProxy";
    public InstrumentedConsumerProxy(BaseSessionManager sessionManager, ChannelAdapter adapter, org.omg.CORBA.Object object)
    {
        super(sessionManager, adapter, object);
        setName();
    }

    public InstrumentedConsumerProxy(BaseSessionManager sessionManager, ChannelAdapter adapter, org.omg.CORBA.Object object, short queuePolicy)
    {
        super(sessionManager, adapter, object, queuePolicy);
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
    public abstract String getMessageType();
}
