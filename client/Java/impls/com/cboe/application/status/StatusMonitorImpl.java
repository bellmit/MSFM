package com.cboe.application.status;

import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.shared.UnitTestHelper;
import com.cboe.domain.util.ProductStructBuilder;
import com.cboe.domain.util.QuoteStructBuilder;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.SystemException;
import com.cboe.idl.businessServices.FirmService;
import com.cboe.idl.businessServices.TradingSessionService;
import com.cboe.idl.cmiConstants.PriceTypes;
import com.cboe.idl.cmiConstants.ProductTypes;
import com.cboe.idl.cmiConstants.StatusUpdateReasons;
import com.cboe.idl.cmiOrder.CancelReportStruct;
import com.cboe.idl.cmiOrder.FilledReportStruct;
import com.cboe.idl.cmiOrder.OrderIdStruct;
import com.cboe.idl.cmiOrder.OrderStruct;
import com.cboe.idl.cmiProduct.ProductKeysStruct;
import com.cboe.idl.cmiProduct.ProductNameStruct;
import com.cboe.idl.cmiQuote.QuoteDetailStruct;
import com.cboe.idl.cmiQuote.QuoteEntryStruct;
import com.cboe.idl.cmiQuote.QuoteFilledReportStruct;
import com.cboe.idl.cmiQuote.QuoteStruct;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;
import com.cboe.idl.cmiUtil.DateStruct;
import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.idl.quote.QuoteInfoStruct;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.StatusMonitor;
import com.cboe.util.channel.ChannelAdapter;
import com.cboe.util.channel.ChannelAdapterRegistrar;
import com.cboe.util.channel.ChannelListenerProxy;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class StatusMonitorImpl extends BObject implements StatusMonitor
{
    private StatusWatcher statusWatcher;

    private synchronized StatusWatcher getStatusWatcher()
    {
        if (statusWatcher == null)
        {
            statusWatcher = new StatusWatcher();
            statusWatcher.start();
        }
        return statusWatcher;
    }

    public void create(String aName)
    {
        super.create(aName);

        try
        {
            if (Log.isDebugOn())
            {
                Log.debug(this, "Registering channelAdapterStatus command callback");
            }
            // Register command to start the service
            getBOHome().registerCommand(    this, // Callback Object
                                            "channelAdapterStatus", // External name
                                            "channelAdapterStatusCallback", // Method name
                                            "Prints out status for all channel adapters.", // Method description
                                            new String[] { String.class.getName() },
                                            new String[] {""}
                                        );
            if (Log.isDebugOn())
            {
                Log.debug(this, "Registering activeUsers command callback");
            }
            getBOHome().registerCommand(    this, // Callback Object
                                            "activeUsers", // External name
                                            "activeUsersCallback", // Method name
                                            "Prints out current logged in users.", // Method description
                                            new String[] { String.class.getName() },
                                            new String[] {""}
                                        );

            if (Log.isDebugOn())
            {
                Log.debug(this, "Registering queueMonitorOn command callback");
            }
            getBOHome().registerCommand(    this, // Callback Object
                                            "queueMonitorOn", // External name
                                            "queueMonitorOnCallback", // Method name
                                            "Turns on continuous queue monitor.", // Method description
                                            new String[] { String.class.getName() },
                                            new String[] {""}
                                        );

            if (Log.isDebugOn())
            {
                Log.debug(this, "Registering queueMonitorOff command callback");
            }
            getBOHome().registerCommand(    this, // Callback Object
                                            "queueMonitorOff", // External name
                                            "queueMonitorOffCallback", // Method name
                                            "Turns off continuous queue monitor.", // Method description
                                            new String[] { String.class.getName() },
                                            new String[] {""}
                                        );

        }
        catch (Exception e)
        {
            Log.information(this, "Could not register channelAdapterStatus callback.");
            Log.exception(this, e);
        }
    }

    public String channelAdapterStatusCallback(String input)
    {
        boolean error = false;
        StringBuilder status = new StringBuilder("");
        try {
            ChannelAdapter adapter;
            ChannelListenerProxy proxy;
            Map channels;
            Object[] channelArray;
            List proxies;

            List adapters = ChannelAdapterRegistrar.getAdapters();

            for (int i = 0; i < adapters.size(); i++)
            {
                adapter = (ChannelAdapter)adapters.get(i);
                status.append("\n-- begin -- Adapter # ").append(i).append(" : ").append(adapter);
                channels= adapter.getRegisteredChannels();
                status.append("\n    adapter current queue size = ").append(adapter.getQueueSize());
                status.append("\n    adapter max queue size = ").append(adapter.getMaxQueueSize());
                channelArray = channels.keySet().toArray();
                for (int j = 0; j < channelArray.length; j++)
                {
                    status.append("\n --> channel # ").append(j).append(" : ").append(channelArray[j]);
                    proxies = (List)channels.get(channelArray[j]);
                    for (int k = 0; k < proxies.size(); k++)
                    {
                        proxy = (ChannelListenerProxy)proxies.get(k);
                        status.append("\n     proxy # ").append(k);
                        status.append("\n     proxy listener # ").append(k).append(" : ").append(proxy);
                        status.append("\n     proxy delegate # ").append(k).append(" : ").append(proxy.getDelegateListener());
                        status.append("\n         proxy current queue size = ").append(proxy.getQueueSize());
                        status.append("\n         proxy max queue size = ").append(proxy.getMaxQueueSize());
                    }
                }
                status.append("\n-- end -- Adapter # ").append(i).append(" : ").append(adapter);
            }
        } catch (Exception e)
        {
            Log.exception(this, e);
            error = true;
        }
        if (Log.isDebugOn())
        {
            Log.debug(this, "All ChannelAdapterStatus: Completed = " + error + " -> \n" + status.toString());
        }
        return status.toString();
    }

    public String activeUsersCallback(String input)
    {
        String hostPort;
        Map sessionsByListener = ServicesHelper.getUserSessionQueryHome().getActiveSessions();
        StringBuilder sessions = new StringBuilder("");
        Iterator it = sessionsByListener.keySet().iterator();
        int i = 0;
        sessions.append("\n-- CAS SESSIONS BEGIN -- -- --");
        while (it.hasNext())
        {
            String listenerIOR = (String)it.next();
            //Object listener = RemoteConnectionFactory.find().string_to_object(listenerIOR);
            hostPort = ""; // = RemoteConnectionFactory.find().getHostname(listener) + RemoteConnectionFactory.find().getPort(listener);
            Object sessionManager = sessionsByListener.get(listenerIOR);
            sessions.append("\nCAS Session #").append(i++).append(" : ").append(hostPort).append(" : ").append(sessionManager);
            sessions.append("\n  -->  IOR = ").append(listenerIOR);
        }
        sessions.append("\n-- CAS SESSIONS END   -- -- --");
        return sessions.toString();
    }

    public String queueMonitorOnCallback(String input)
    {
        int threshold = 100;
        try {
            threshold = Integer.parseInt(input);
            getStatusWatcher().setQueueThreshold(threshold);
        } catch (Exception e)
        {
        }

        getStatusWatcher().startWatcher();
        StringBuilder sb = new StringBuilder(65);
        sb.append("(SW) Threshold set = ").append(threshold);
        System.out.println(sb.toString());
        sb.setLength(0);
        sb.append("Status watcher thread enabled with queue threshold = ").append(threshold);
        return sb.toString();
    }

    public String queueMonitorOffCallback(String input)
    {
        getStatusWatcher().stopWatcher();
        return "Status watcher thread disabled";
    }

}