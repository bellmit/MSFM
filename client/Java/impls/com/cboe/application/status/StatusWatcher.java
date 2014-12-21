package com.cboe.application.status;

import com.cboe.interfaces.application.StatusMonitor;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.util.channel.ChannelAdapter;
import com.cboe.util.channel.ChannelListenerProxy;
import com.cboe.util.channel.ChannelAdapterRegistrar;
import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import java.util.*;

public class StatusWatcher extends Thread
{
    private boolean monitorEnabled;
    private int queueThreshold;

    public StatusWatcher()
    {
        super();
        queueThreshold = 0;
    }

    public void setQueueThreshold(int queueThreshold)
    {
        this.queueThreshold = queueThreshold;
    }

    public void stopWatcher()
    {
        monitorEnabled = false;
        System.out.println("(SW) Status Watcher disabled");
    }

    public void startWatcher()
    {
        monitorEnabled = true;
    }

    public void run()
    {
        ChannelAdapter adapter;
        ChannelListenerProxy proxy;
        Map channels;
        HashMap<ChannelListenerProxy,String> proxyHash;
        Object[] channelArray;
        List proxies;
        List adapters;
        int adapterQueueSize;
        int proxyQueueSize;
        int proxyMaxQueue;
        int idleThreads;

        try {
            while (true)
            {
                while (monitorEnabled)
                {
                    proxyHash = new HashMap<ChannelListenerProxy,String>();
                    adapters = ChannelAdapterRegistrar.getAdapters();
                    for (int i = 0; i < adapters.size(); i++)
                    {
                    	try
                    	{
	                        adapter = (ChannelAdapter)adapters.get(i);
	                        channels= adapter.getRegisteredChannels();
	                        channelArray = channels.keySet().toArray();
	                        adapterQueueSize = adapter.getQueueSize();
	                        for (int j = 0; j < channelArray.length; j++)
	                        {
	                            proxies = (List)channels.get(channelArray[j]);
	                            for (int k = 0; k < proxies.size(); k++)
	                            {
	                            	try
	                            	{
		                                proxy = (ChannelListenerProxy)proxies.get(k);
		                                proxyQueueSize = proxy.getQueueSize();
		                                proxyMaxQueue = proxy.getMaxQueueSize();
		                                idleThreads = adapter.getPool().getIdleThreadCount();
		                                if (proxyQueueSize > queueThreshold)
		                                {
		                                    StringBuilder status = new StringBuilder(180);
		                                    status.append("(SW)threadPool (").append(adapter.getPool().getName());
		                                    status.append(") number of idle threads:").append(idleThreads).append("\n");
		                                    status.append("(SW) A#");
		                                    status.append(i);
		                                    status.append(" (");
		                                    status.append(adapter);
		                                    status.append(")\n");
		                                    status.append("(SW) ==> Pr#");
		                                    status.append(k);
		                                    status.append(" (");
		                                    status.append(proxy.getDelegateListener());
		                                    status.append(") ");
		                                    status.append(" Queue(");
		                                    status.append(proxyQueueSize);
		                                    status.append(") Max(");
		                                    status.append(proxyMaxQueue);
		                                    status.append(")");
		                                    proxyHash.put(proxy, status.toString());
		                                }
	                            	}
	                            	catch (Exception e1)
	                            	{
	                            		Log.exception("StatusWatcher ->", e1);
	                            	}
	                            }
	                        }
                    	}
                    	catch (Exception e2)
                    	{
                    		Log.exception("StatusWatcher ->", e2);
                    	}
                    }
                    Iterator<String> proxyMessages = proxyHash.values().iterator();
                    StringBuilder sb = new StringBuilder(100);
                    while (proxyMessages.hasNext())
                    {
                        String proxyInfo = proxyMessages.next();
                        sb.setLength(0);
                        sb.append(Calendar.getInstance().getTime().toString()).append(":").append(proxyInfo);
                        System.out.println(sb.toString());
                    }
                    sb = null;  // if garbage collection runs, let this be collected too

                    Thread.currentThread().sleep(10000);
                }
                Thread.currentThread().sleep(10000);
            }
        } catch (Exception e)
        {
            Log.exception("StatusWatcher ->", e);
        }
    }
}