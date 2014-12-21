/**
 * 
 */
package com.cboe.domain.supplier.proxy;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.util.ThreadPool;
import com.cboe.util.channel.ChannelAdapter;
import com.cboe.util.channel.ChannelListener;
import com.cboe.util.event.ChannelEventBlocker;
import com.cboe.util.channel.proxy.ProxyThreadCommand;
import com.cboe.util.channel.proxy.BlockedProxyThreadCommandImpl;

/**
 * @author Gijo Joseph
 *
 */
public class BlockedSupplierChannelListenerProxy extends 
		SupplierChannelListenerProxy {
	
	public BlockedSupplierChannelListenerProxy(ChannelListener listener, ChannelAdapter adapter, ThreadPool threadPool)
    {
        super(listener, adapter, threadPool);
	}
	
    protected ProxyThreadCommand initializeThreadCommand()
    {
   		return new BlockedProxyThreadCommandImpl(this);
    }

	public void setChannelEventBlocker(ChannelEventBlocker eventBlocker)
	{
		if (command != null)
			((BlockedProxyThreadCommandImpl)command).setEventBlocker(eventBlocker);
		else
			Log.alarm("BlockedSupplierChannelListenerProxy found with command set to null!!");
	}
}
