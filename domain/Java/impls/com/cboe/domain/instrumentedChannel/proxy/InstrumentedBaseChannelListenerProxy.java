/**
 * @author Jing Chen
 */
package com.cboe.domain.instrumentedChannel.proxy;

import com.cboe.domain.instrumentedChannel.InstrumentedThreadPool;
import com.cboe.domain.util.InstrumentorUserData;
import com.cboe.interfaces.domain.instrumentedChannel.InstrumentedChannelListener;
import com.cboe.interfaces.domain.instrumentedChannel.InstrumentedChannelListenerProxy;
import com.cboe.util.channel.ChannelAdapter;
import com.cboe.util.channel.proxy.BaseChannelListenerProxy;
import com.cboe.util.channel.proxy.ProxyThreadCommand;

public abstract class InstrumentedBaseChannelListenerProxy extends BaseChannelListenerProxy implements InstrumentedChannelListenerProxy
{
    private volatile InstrumentorUserData userData;

    /**
     * Deafult constructor.
     */
    public InstrumentedBaseChannelListenerProxy(InstrumentedChannelListener listener, ChannelAdapter adapter, InstrumentedThreadPool threadPool)
    {
        super(listener, adapter, threadPool);
        listener.queueInstrumentationInitiated();
    }

    protected ProxyThreadCommand initializeThreadCommand()
    {
        return new InstrumentedProxyThreadCommandImpl(this);
    }

    public String getName()
    {
        return ((InstrumentedChannelListener)listener).getName();
    }

    public void addUserData(String key, String value)
    {
        InstrumentorUserData userData = getMyUserData();
        userData.addValue(key, value);
    }

    public void removeUserData(String key, String value)
    {
        InstrumentorUserData userData = getMyUserData();
        userData.removeValueForKey(key, value);
    }

    
    private synchronized InstrumentorUserData createMyUserData()
    {
        if(userData == null)
        {
            userData = new InstrumentorUserData();
        }
        return userData;
    }

    private InstrumentorUserData getMyUserData()
    {
    	InstrumentorUserData ud = userData;
        if(ud == null)
        {
            ud = createMyUserData();
        }
        return ud;
    }

    public Object getUserData()
    {
        return getMyUserData();
    }

    /**
     * This method implements the equals method to allow this object to be hashable
     */
    public boolean equals(Object obj)
    {
        if ((obj != null) && (obj instanceof InstrumentedBaseChannelListenerProxy))
        {
            return (getDelegateListener().equals(((InstrumentedBaseChannelListenerProxy) obj).getDelegateListener()));
        }
        return false;
    }
    /**
     * This method implements the hashcode method to allow this object to be hashable
     */
    public int hashCode() {
        return getDelegateListener().hashCode();
    }
}
