package com.cboe.infra.presentation.network;

import java.util.*;

import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerMMBusinessProperty;
import com.cboe.presentation.common.logging.LoggingServiceAdapter;
import com.cboe.presentation.common.properties.AppPropertiesFileFactory;
import com.cboe.presentation.common.properties.PropertiesFile;

import com.cboe.utils.monitoringService.TransportMonitor;
import com.cboe.utils.monitoringService.TransportPSClientListener;
import com.cboe.utils.monitoringService.TransportPSServer;

public class MonitoringServiceProxy
{
    private static boolean monitoringServiceStarted = false;
    private static MonitoringServiceProxy instance = new MonitoringServiceProxy();
    private TransportMonitor monitor = null;
    private Set<TransportPSClientListener> listeners = new HashSet<TransportPSClientListener>();
    private Map<String, SBTLiveNode> servers = new HashMap<String, SBTLiveNode>();
    private long backgroundUpdateInterval = 30 * 1000; // 30 seconds
    private LoggingServiceAdapter loggingServiceAdapter = null;
    private int throttlingInterval = 10;
    private int throttlingSleepTime = 100;

    /**
     * MonitoringServiceProxy is a Singleton.  Use this method if you need a reference to a MonitoringServiceProxy
     * object.  Instances can not be created outside of this class
     */
    public static MonitoringServiceProxy getInstance()
    {
        return instance;
    }

    public void setThrottling(int interval, int sleepTime)
    {
        this.throttlingInterval = interval;
        this.throttlingSleepTime = sleepTime;
    }

    /**
     * Expose the underlying TransportMonitor object.
     */
    public TransportMonitor getTransportMonitor() throws IllegalStateException
    {
        return monitor;
    }

    /**
     * Allows TransportPSClientListeners to be queued up, awaiting the underlying monitor to be initialized.
     */
    public void addClientListener(TransportPSClientListener l)
    {
        listeners.add(l);
        if (monitor != null)
        {
            monitor.addClientListener(l);
        }
    }

    /**
     * Remove a listener from the queue, and also from the underlying (delegate) monitor if it has been started.
     */
    public void removeClientListener(TransportPSClientListener l)
    {
        listeners.remove(l);
        if (monitor != null)
        {
            monitor.removeClientListener(l);
        }
    }

    /**
     * Use the TransportMonitor to poll (individually) the clients in the collection. This method is throttled such that
     * the maximum refresh frequency is 2x BACKGROUND_UPDATE_INTERVAL
     */
    public synchronized void refresh(Collection c)
    {
        refreshGeneral(c);

        // on each refresh, check for new servers in the cloud
        HashMap serverMap = monitor.getServerInfoMap();
        for (Object o : serverMap.keySet())
        {
            String serverName = (String) o;
            String serverInfo = (String) serverMap.get(serverName);
            SBTLiveNode serverNode = servers.get(serverName);

            if (serverNode == null)
            {
                TransportPSServer psServer = monitor.getServer(serverName);
                serverNode = new SBTLiveNode(psServer, serverInfo);
                servers.put(serverName, serverNode);
                Network.getNetwork(SBTLiveNode.class).addNode(serverNode);
            }
        }
    }

    /**
     * Force a poll of an individual client's client/server/general info
     */
    public synchronized void refreshGeneral(Collection collection)
    {
        if (GUILoggerHome.find().isDebugOn() 
            && GUILoggerHome.find().isPropertyOn(GUILoggerMMBusinessProperty.MONITOR))
        {
            GUILoggerHome.find().debug("Polling client information for " + collection.size() + " clients",
                                       GUILoggerMMBusinessProperty.MONITOR);
        }

        int counter = 0;
        for (Object n: collection)
        {
            // only SBTLiveNodes have TransportPSClient
            if (n instanceof SBTLiveNode)
            {
                SBTNode node = (SBTNode) n;
                counter++;
                //every 10 updates, sleep 100 ms
                if (counter % throttlingInterval == 0)
                {
                    if (GUILoggerHome.find().isDebugOn() 
                        && GUILoggerHome.find().isPropertyOn(GUILoggerMMBusinessProperty.MONITOR))
                    {
                        GUILoggerHome.find().debug("MonitoringServiceProxy.refreshGeneral():Updated=" + counter +
                                                   " Now sleeping for " + throttlingSleepTime + " MS",
                                                   GUILoggerMMBusinessProperty.MONITOR);
                    }
                    try
                    {
                        Thread.sleep(throttlingSleepTime);
                    }
                    catch (Exception e) 
                    {
                        GUILoggerHome.find().exception(e);
                    }
                }
                
                refresh(node);
                
                if (GUILoggerHome.find().isDebugOn() 
                    && GUILoggerHome.find().isPropertyOn(GUILoggerMMBusinessProperty.MONITOR) 
                    && node.getType() == SBTNodeType.SERVER)
                {
                    SBTLiveNode tmpNode = (SBTLiveNode) node;
                    SBTLiveNode[] ncList = tmpNode.getNetworkConnectionList();

                    GUILoggerHome.find().debug("SBTLiveNode(NetworkConnection = " + ncList.length + ") for: " + node.getName(), GUILoggerMMBusinessProperty.MESSAGEMON);
                    for (SBTLiveNode nc : ncList)
                    {
                        GUILoggerHome.find().debug("SBTLiveNode(NetworkConnection)---> Name: " + nc.getName() +
                                                   " DisplayName: " + nc.getDisplayName() +
                                                   "  Date: " + nc.getServerBufferUpdateTime() + 
                                                   "  Alive: " + nc.isAlive() + 
                                                   "  Server Discards: " + nc.getServerDiscardCount() +
                                                   "  ServerQueuedBytesCount: " + nc.getServerQueuedBytesCount() + 
                                                   "  ServerQueuedMsgCount: " + nc.getServerQueuedMsgCount() +
                                                   "  ServerReadBufferSize: " + nc.getServerReadBufferSize() +
                                                   "  ServerWriteBufferSize: " + nc.getServerWriteBufferSize(),
                                                   GUILoggerMMBusinessProperty.MONITOR);
                    }
                }
            }
            else
            {
                if (GUILoggerHome.find().isDebugOn() 
                    && GUILoggerHome.find().isPropertyOn(GUILoggerMMBusinessProperty.MONITOR))
                {
                    GUILoggerHome.find()
                            .debug("MonitoringServiceProxy.refresh(): Error - refresh collection contains element that is not an SBTNode (" +
                                   n + "): " + n.getClass().getName(), GUILoggerMMBusinessProperty.MONITOR);
                }
            }
        }
    }

    /**
     * Force a poll of an individual SBTNode; our data will be refreshed when it responds to the poll.
     */
    public void refresh(SBTNode node)
    {
        if (node.getType() == SBTNodeType.SERVER || node.getType() == SBTNodeType.NETWORK_CONNECTION)
        {
            monitor.pollServerInformation(node.getName());
        }
        else
        {
            monitor.pollClientInformation(node.getName());
        }
    }

    /**
     * Force a poll of an individual client; our data will be refreshed when it responds to the poll.
     */
    public void refreshClient(String clientName)
    {
        monitor.pollClientInformation(clientName);
    }
    
    /**
     * Force a poll of an individual client; our data will be refreshed when it responds to the poll.
     */
    public void refreshServer(String serverName)
    {
        monitor.pollServerInformation(serverName);
    }

    /**
     * Stop the monitoring service.
     */
    public void stopMonitoring()
    {
        if (monitor != null)
        {
            
            for (Iterator i= listeners.iterator(); i.hasNext();)
            {
                monitor.removeClientListener((TransportPSClientListener)i.next());
            }
            
            monitor.stopMonitoring();
        }
    }

    /**
     * It is possible to have a valid instance of the MonitoringServiceProxy that is not using the MonitoringService to
     * poll yet.  In fact, this is a common scenario when the user has not specified a default connection, and must
     * manually choose to connect to a particular environment.
     */
    public void startMonitoringService()
    {
        try
        {
            synchronized(MonitoringServiceProxy.class)
            {
                if (!monitoringServiceStarted)
                {

                    // by now, all properties must be present in the System.getProperties() bag
                    Properties props = System.getProperties();
                    if (GUILoggerHome.find().isDebugOn() 
                        && GUILoggerHome.find().isPropertyOn(GUILoggerMMBusinessProperty.MONITOR))
                    {
                        GUILoggerHome.find().debug( "Monitoring Service Proxy- DEBUG: Obtaining TransportMonitor instance",
                                                    GUILoggerMMBusinessProperty.MONITOR);
                    }
                    monitor = TransportMonitor.getInstance(props, loggingServiceAdapter);
                    if (GUILoggerHome.find().isDebugOn() 
                        && GUILoggerHome.find().isPropertyOn(GUILoggerMMBusinessProperty.MONITOR))
                    {
                        GUILoggerHome.find().debug("Monitoring Service Proxy- DEBUG: Obtaining ECClientHome instance",
                                                   GUILoggerMMBusinessProperty.MONITOR);
                    }
                    // add the client listeners that have been queued up for notification:
                    for (TransportPSClientListener l : listeners)
                    {
                        monitor.addClientListener(l);
                    }
                    
                    if (GUILoggerHome.find().isDebugOn() 
                        && GUILoggerHome.find().isPropertyOn(GUILoggerMMBusinessProperty.MONITOR))
                    {
                        GUILoggerHome.find().debug("MonitoringServiceProxy: monitor.startMonitoring",
                                                   GUILoggerMMBusinessProperty.MONITOR);
                    }

                    monitor.startMonitoring();
                    monitoringServiceStarted = true;
                }
            } // end synchronized block
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    } // end startMonitoringService

    private MonitoringServiceProxy()
    {
        PropertiesFile props = AppPropertiesFileFactory.find();

        loggingServiceAdapter = new LoggingServiceAdapter(GUILoggerHome.find(), GUILoggerMMBusinessProperty.MONITOR);

        String backgroundUpdateIntervalSetting = props.getValue("Options", "BACKGROUND_UPDATE_INTERVAL");
        try
        {
            backgroundUpdateInterval = Long.parseLong(backgroundUpdateIntervalSetting);
        }
        catch (NumberFormatException nfe)
        {
            GUILoggerHome.find()
                    .alarm("MonitoringServiceProxy.<INIT>: backgrounUpdateIntervalSetting was unparsable - reverting to 30 seconds");
            // nothing - default was 30 seconds (30000)
        }
    }
} // end MonitoringServiceProxy
