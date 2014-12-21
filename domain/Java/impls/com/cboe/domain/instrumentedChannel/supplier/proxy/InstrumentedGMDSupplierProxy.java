package com.cboe.domain.instrumentedChannel.supplier.proxy;

import com.cboe.domain.supplier.proxy.GMDSupplierProxy;
import com.cboe.domain.util.InstrumentorNameHelper;
import com.cboe.interfaces.domain.GMDProxyHome;
import com.cboe.interfaces.domain.instrumentedChannel.InstrumentedChannelListener;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.channel.ChannelAdapter;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import org.omg.CORBA.UserException;

public abstract class InstrumentedGMDSupplierProxy extends GMDSupplierProxy implements InstrumentedChannelListener
{
    private final static String GMD_CONSUMER_PROXY_NAME = "GMDConsumerProxy";

    public InstrumentedGMDSupplierProxy(BaseSessionManager sessionManager,
                                        ChannelAdapter adapter,
                                        boolean gmdProxy,
                                        GMDProxyHome home,
                                        Object hashKey)
    {
        super(sessionManager, adapter, gmdProxy, home, hashKey);
        try
        {
            name = InstrumentorNameHelper.createInstrumentorName(
                    new String[]{sessionManager.getInstrumentorName(),
                                 getMessageType(),
                                 GMD_CONSUMER_PROXY_NAME
                    },
                    this);
        }
        catch(UserException e)
        {
            Log.exception(e);
        }
    }

    public InstrumentedGMDSupplierProxy(BaseSessionManager sessionManager,
                                        ChannelAdapter adapter,
                                        boolean gmdProxy,
                                        GMDProxyHome home,
                                        org.omg.CORBA.Object hashKey)
    {
        super(sessionManager, adapter, gmdProxy, home, hashKey);
        try
        {
            name = InstrumentorNameHelper.createInstrumentorName(
                    new String[]{sessionManager.getInstrumentorName(),
                                 getMessageType(),
                                 GMD_CONSUMER_PROXY_NAME
                    },
                    this);
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
