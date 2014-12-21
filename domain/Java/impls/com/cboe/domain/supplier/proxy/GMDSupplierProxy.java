package com.cboe.domain.supplier.proxy;

import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.interfaces.domain.GMDProxyHome;
import com.cboe.util.channel.ChannelAdapter;

public abstract class GMDSupplierProxy extends CallbackSupplierProxy
{
    private boolean gmdProxy;
    private GMDProxyHome parent;

    public boolean getGMDStatus()
    {
        return gmdProxy;
    }

    public GMDSupplierProxy(BaseSessionManager sessionManager, ChannelAdapter adapter, boolean gmdProxy, GMDProxyHome home, org.omg.CORBA.Object object)
    {
        super(sessionManager, adapter, object);
        this.parent = home;
        this.gmdProxy = gmdProxy;
    }

    public GMDSupplierProxy(BaseSessionManager sessionManager, ChannelAdapter adapter, boolean gmdProxy, GMDProxyHome home, Object hashKey)
    {
        super(sessionManager, adapter, hashKey);
        this.parent = home;
        this.gmdProxy = gmdProxy;
    }

    public void cleanUp()
    {
        if (gmdProxy)
        {
            parent.cleanUpGMDProxy(this);
        }
        parent = null;
        super.cleanUp();
    }
}