package com.cboe.domain.instrumentedChannel.supplier.proxy;

import com.cboe.domain.instrumentedChannel.InstrumentedThreadPool;
import com.cboe.domain.instrumentedChannel.proxy.InstrumentedBaseChannelListenerProxy;
import com.cboe.domain.supplier.proxy.BaseSupplierProxy;
import com.cboe.domain.supplier.proxy.LostConnectionException;
import com.cboe.domain.supplier.proxy.SupplierListenerProxyUserData;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.domain.instrumentedChannel.InstrumentedChannelListener;
import com.cboe.interfaces.domain.supplier.SupplierChannelListener;
import com.cboe.util.channel.ChannelAdapter;
import com.cboe.util.channel.ChannelEvent;

/**
 * @author Jing Chen
 */

public class InstrumentedSupplierChannelListenerProxy extends InstrumentedBaseChannelListenerProxy implements SupplierChannelListener
{
    protected SupplierListenerProxyUserData userData;
    public InstrumentedSupplierChannelListenerProxy(InstrumentedChannelListener listener, ChannelAdapter adapter, InstrumentedThreadPool threadPool)
    {
        super(listener, adapter, threadPool);
        userData = new SupplierListenerProxyUserData();
    }

    public short getQueuePolicy()
    {
        return ((BaseSupplierProxy)getDelegateListener()).getQueuePolicy();
    }

    public void channelUpdate(ChannelEvent event)
    {
        try {
            getDelegateListener().channelUpdate(event);
        } catch (LostConnectionException e)
        {
            Log.debug("InstrumentedSupplierChannelListenerProxy -> Lost Connection: removing listener " + this.getDelegateListener());
            adapter.removeChannelListener(this.getDelegateListener());
        }
    }

    public void cleanUp()
    {
        ((BaseSupplierProxy)getDelegateListener()).cleanUp();
        super.cleanUp();
    }

    public Object getListenerUserData()
    {
        return userData;
    }

    public void addListenerUserData(String data)
    {
        userData.addValue(data);
    }

    public void removeListenerUserData(String data)
    {
        userData.removeValue(data);
    }
}
