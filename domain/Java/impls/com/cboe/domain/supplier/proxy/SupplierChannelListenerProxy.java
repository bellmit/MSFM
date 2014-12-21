package com.cboe.domain.supplier.proxy;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.util.ThreadPool;
import com.cboe.util.channel.ChannelAdapter;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.channel.ChannelListener;
import com.cboe.util.channel.proxy.BaseChannelListenerProxy;
import com.cboe.interfaces.domain.supplier.SupplierChannelListener;

/**
 * BaseConsumerProxy serves as the abstract base proxy to all of the callback
 * consumer proxies.
 *
 * @author Derek T. Chambers-Boucher
 * @version 06/25/1999
 */

public class SupplierChannelListenerProxy extends BaseChannelListenerProxy implements SupplierChannelListener
{
    protected SupplierListenerProxyUserData userData;

    /**
     * BaseConsumerProxy constructor.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @param hashKey CMI Callback Consumer IOR.
     */
    public SupplierChannelListenerProxy(ChannelListener listener, ChannelAdapter adapter, ThreadPool threadPool)
    {
        super(listener, adapter, threadPool);
        userData = new SupplierListenerProxyUserData();
    }

    public short getQueuePolicy()
    {
        return ((BaseSupplierProxy)getDelegateListener()).getQueuePolicy();
    }

    /**
     * <code>hashCode()</code> returns the hash code for this object.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @return the int value of the hash code.
     */
    public int hashCode()
    {
        return getDelegateListener().hashCode();
    }

    /**
     * <code>equals()</code> returns a boolean value representing the truth of the
     * equality of this object and the passed object.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @return true if the passed Object is equivalent to this instance; false otherwise.
     *
     * @param obj the Object to check equality with.
     */
    public boolean equals(Object obj)
    {
        // check the equivalence of the IOR strings.
        if (obj instanceof SupplierChannelListenerProxy)
        {
            return getDelegateListener().equals(((SupplierChannelListenerProxy)obj).getDelegateListener());
        }
        else
        {
            return false;
        }
    }

    /**
     * This method updates the listener with the given ChannelEvent.
     *
     * @author Derek T. Chambers-Boucher
     *
     * @param event com.cboe.application.event.ChannelEvent
     */
    public void channelUpdate(ChannelEvent event)
    {
        try {
            getDelegateListener().channelUpdate(event);
        } catch (LostConnectionException e)
        {
            Log.debug("SupplierChannelListenerProxy -> Lost Connection: removing listener " + this.getDelegateListener());
		if (adapter != null) 
		{
		    adapter.removeChannelListener(this.getDelegateListener());
		}
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
