package com.cboe.domain.instrumentedChannel.supplier;

import com.cboe.domain.instrumentedChannel.InstrumentedThreadPool;
import com.cboe.domain.instrumentedChannel.supplier.proxy.InstrumentedSupplierChannelListenerProxy;
import com.cboe.domain.supplier.BaseSupplier;
import com.cboe.domain.supplier.proxy.BaseSupplierProxy;
import com.cboe.infrastructureServices.foundationFramework.HomeFactory;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.domain.instrumentedChannel.InstrumentedChannelListener;
import com.cboe.util.ThreadPool;
import com.cboe.util.channel.ChannelListener;
import com.cboe.util.channel.ChannelListenerProxy;

/**
 * @author Jing Chen
 */

public abstract class InstrumentedBaseSupplier extends BaseSupplier
{
    public InstrumentedBaseSupplier()
    {
        super();
    }

    public InstrumentedBaseSupplier(boolean initializeThreadPool)
    {
        super(initializeThreadPool);
    }

    protected ThreadPool getThreadPool()
    {
        return getInstrumentedGlobalThreadPool();
    }

    public ChannelListenerProxy getListenerProxy(ChannelListener listener)
    {
        //It should be safe to cast listener to InstrumentedChannelListener at this point.  listener has been validated through validListener call.
        return new InstrumentedSupplierChannelListenerProxy((InstrumentedChannelListener)listener, this, (InstrumentedThreadPool)getThreadPool());
    }

    protected synchronized InstrumentedThreadPool createInstrumentedGlobalThreadPool()
    {
        if (threadPool == null)
        {
	    	try {
	            	com.cboe.interfaces.domain.InstrumentedGlobalThreadPoolHome home =
	                    (com.cboe.interfaces.domain.InstrumentedGlobalThreadPoolHome)HomeFactory.getInstance()
	                        .findHome(com.cboe.interfaces.domain.InstrumentedGlobalThreadPoolHome.HOME_NAME);
	            	threadPool = (InstrumentedThreadPool)home.find();
	    	}
	    	catch (CBOELoggableException e)
			{
	            	Log.exception(e);
	            	// a really ugly way to get around the missing exception in the interface...
	            	throw new NullPointerException("Could not find InstrumentedGlobalThreadPoolHome.");
	    	}
        }
        return (InstrumentedThreadPool)threadPool;
    }

    protected InstrumentedThreadPool getInstrumentedGlobalThreadPool()
    {
        ThreadPool tp = threadPool;
        if (tp == null)
    	{
            	tp = createInstrumentedGlobalThreadPool();
    	}
        return (InstrumentedThreadPool)tp;
    }

    protected boolean validListener(ChannelListener listener)
    {
        if (listener != null)
        {
            if (!proxyClass.isAssignableFrom(listener.getClass()))
            {
                throw new ClassCastException("listener not assignable from " + proxyClass.getName());
            }
            InstrumentedSupplierChannelListenerProxy proxy = (InstrumentedSupplierChannelListenerProxy)channels.getProxy(listener);
            if (proxy != null) {
                if (((BaseSupplierProxy)listener).getQueuePolicy() != proxy.getQueuePolicy()) {
                    throw new IllegalArgumentException("Listener already has a queue policy = " + proxy.getQueuePolicy());
                }
            }
            return true;
        }
        else
        {
            return false;
        }
    }
}
