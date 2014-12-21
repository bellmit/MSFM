package com.cboe.infra.presentation.network;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.regex.*;

import com.cboe.interfaces.presentation.processes.CBOEProcess;
import com.cboe.interfaces.presentation.processes.OrbNameAlias;
import com.cboe.interfaces.presentation.processes.OrbProcessCache;

import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.util.event.EventChannelListener;

import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerMMBusinessProperty;
import com.cboe.presentation.common.properties.AppPropertiesFileFactory;
import com.cboe.presentation.environment.EnvironmentManagerFactory;
import com.cboe.presentation.environment.EnvironmentProperties;

import com.cboe.infra.presentation.filter.Filter;
import com.cboe.infra.presentation.filter.FilterUtilities;
import com.cboe.utils.monitoringService.TransportPSClient;
import com.cboe.utils.monitoringService.TransportPSClientListener;
import com.cboe.utils.monitoringService.TransportPSSubject;

public class RealTimeNetwork extends Network
        implements TransportPSClientListener, PropertyChangeListener, OrbProcessCache, EventChannelListener
{
    protected MonitoringServiceProxy msProxy = MonitoringServiceProxy.getInstance();
    protected String monPrefix;
    protected Map nameKeysNode = null;
    protected Pattern omitClientsPattern;
    protected EventChannelAdapter eventChannel;
    private static final String PROPERTIES_SECTION = "Options";
    private static final String OMIT_CLIENTS_MASK_KEY = "DENY_CLIENTS_NAMED";
    private HashMap processCache;

    /**
     * Specified by TransportPSClientListener
     */
    public void clientAdded(TransportPSClient client)
    {
        if (GUILoggerHome.find().isDebugOn()
            && GUILoggerHome.find().isPropertyOn(GUILoggerMMBusinessProperty.MESSAGEMON))
        {
            GUILoggerHome.find().debug("clientAdded(" + client.getClientName() + ") from clientAdded(...)",
                                       GUILoggerMMBusinessProperty.MESSAGEMON);
        }

        Collection c = FilterUtilities.filterCollection(networkNodes, new TransportClientFilter(client));
        if (c.isEmpty())
        {
            addClientNode(client);
        }
    }

    /**
     * Specified by TransportPSClientListener
     */
    public void subjectAdded(TransportPSSubject subj)
    {
        // need this to make livenetworkview update in real time!
        TransportPSClient parent = subj.getParent();
        if (!  addClientNode(parent))
        {
            if (GUILoggerHome.find().isDebugOn()
                && GUILoggerHome.find().isPropertyOn(GUILoggerMMBusinessProperty.SUBJECTS))
            {
                GUILoggerHome.find().debug("clientAdded(" + parent.getClientName() + ") from subjectAdded(...)",
                                           GUILoggerMMBusinessProperty.SUBJECTS);
            }
        }
        else
        {
            SBTLiveNode node = null;
            synchronized(nameKeysNode)
            {
                node = (SBTLiveNode) nameKeysNode.get(parent.getClientName());
            }
            fireNodeUpdatedEvent(Arrays.asList(new SBTLiveNode[]{node}));
        }
    }

    /**
     * Specified by PropertyChangeListener.  The network is a propertychangelistener for each node in the network.  When
     * the properties of a node change, the network relays notification to other interested parties. This method gets
     * called when a bound property is changed.
     * @param evt A PropertyChangeEvent object describing the event source and the property that has changed.
     */

    public void propertyChange(PropertyChangeEvent evt)
    {
//        buffer.add(evt.getSource());
        Collection updated = new ArrayList();
        updated.add(evt.getSource());
        this.fireNodeUpdatedEvent(updated);
    }


    public SBTNode[] sendsTo(String subjectName)
    {
        Collection participating = new HashSet();
        if (GUILoggerHome.find().isDebugOn()
            && GUILoggerHome.find().isPropertyOn(GUILoggerMMBusinessProperty.MESSAGEMON))
        {
            GUILoggerHome.find().debug("RealTimeNetwork::sendsTo - Network contains " + networkNodes.size() + " nodes.",
                                       GUILoggerMMBusinessProperty.MESSAGEMON);
        }
        Iterator iter = getNetworkElements().iterator();
        while (iter.hasNext())
        {
            SBTLiveNode lvNode = (SBTLiveNode) iter.next();
            if (lvNode.listensOnSubject(subjectName))
            {
                participating.add(lvNode);
            }
        }
        return (SBTNode[]) participating.toArray(new SBTLiveNode[0]);
    }


    /**
     * Create an SBTLiveNode for each TransportPSClient reported by TransportMonitor. Register as a
     * TransportPSClientListener so that we know when new clients come online. For each new SBTNode, be sure to call
     * super.addNode() rather than accessing the networkNodes variable directly, so that Network has a chance to
     * validate the node against the ProcessWatcher (PW).
     */

    protected RealTimeNetwork()
    {
        nameKeysNode = new HashMap();
        processCache = new HashMap();
        String omitClientsSetting = AppPropertiesFileFactory.find().getValue(PROPERTIES_SECTION, OMIT_CLIENTS_MASK_KEY);
        try
        {
            omitClientsPattern = Pattern.compile(omitClientsSetting);
        }
        catch (Throwable t)
        {
            omitClientsPattern = Pattern.compile(""); // match anything
        }
        EnvironmentProperties currentEnvironment = EnvironmentManagerFactory.find().getCurrentEnvironment();
        monPrefix = currentEnvironment.getMonitorPrefix();
        if (GUILoggerHome.find().isDebugOn()
            && GUILoggerHome.find().isPropertyOn(GUILoggerMMBusinessProperty.MESSAGEMON))
        {
            GUILoggerHome.find().debug("RealTimeNetwork (CTOR) : getting Transport Monitor object",
                                       GUILoggerMMBusinessProperty.MESSAGEMON);
        }

        msProxy.addClientListener(this);
        msProxy.startMonitoringService();

        // create a consumer for the buffer to rip through and fire a single nodeUpdated event
//        Thread updateBufferConsumer = new Thread()
//        {
//            public void run()
//            {
//                while ( true )
//                {
//                    // block until a minimum of five nodes have been
//                    //updated or 30 seconds passes.
//                    Collection updates = buffer.getBuffer();
//                    RealTimeNetwork.this.fireNodeUpdatedEvent(updates);
//                }
//            }
//        };
//        updateBufferConsumer.start();
        eventChannel = EventChannelAdapterFactory.find();
        eventChannel.setDynamicChannels(true);
        ChannelKey channelKey = new ChannelKey(ChannelType.INSTRUMENTATION_ORBNAME_ALIAS_UPDATE, new Integer(0));
        eventChannel.addChannelListener(eventChannel, this, channelKey);

    }

    protected boolean addClientNode(TransportPSClient client)
    {
        // by checking to see if the client name starts with
        // "/"+monPrefix, we filter out clients that we are
        // not interested in when we deal with RT servers hosting
        // multiple, virtual environments
        boolean isEnvironmentMember = client.getClientName().startsWith("/" + monPrefix);
        // rt servers, though, don't have an environment prefix, since
        // they sometimes server multiple environments.  We still want them, though
        boolean isRtServer = client.getClientName().startsWith("/_");
        if (isRtServer)
        {
            if (GUILoggerHome.find().isDebugOn()
                && GUILoggerHome.find().isPropertyOn(GUILoggerMMBusinessProperty.MESSAGEMON))
            {
                GUILoggerHome.find().debug("Got info for RT Server " + client.getClientName(),
                                           GUILoggerMMBusinessProperty.MESSAGEMON);
            }
        }
        if (!isEnvironmentMember && !isRtServer)
        {
            return false;
        }

        Matcher m = omitClientsPattern.matcher(client.getClientName());
        if (m.matches())
        {
            if (GUILoggerHome.find().isDebugOn())
            {
                GUILoggerHome.find().debug("Filtering out client " + client.getClientName(),
                                           GUILoggerMMBusinessProperty.MESSAGEMON);
            }
            return false;
        }

        synchronized(nameKeysNode)
        {
            SBTLiveNode newNode = (SBTLiveNode) nameKeysNode.get(client.getClientName());
            if (newNode == null)
            {
                newNode = new SBTLiveNode(client, this);
                nameKeysNode.put(client.getClientName(), newNode);
                processCache.put(newNode.getOrbName(), newNode);
                if (isRtServer)
                {
                    if (GUILoggerHome.find().isDebugOn()
                        && GUILoggerHome.find().isPropertyOn(GUILoggerMMBusinessProperty.MESSAGEMON))
                    {
                        GUILoggerHome.find().debug("Adding Rt server " + client.getClientName() + " to network",
                                                   GUILoggerMMBusinessProperty.MESSAGEMON);
                    }
                }
                super.addNode(newNode);
                return true;
            }
            else
            {
                return false;
            }
        }
    }

    public class TransportClientFilter implements Filter
    {
        private TransportPSClient matchClient = null;

        public boolean accept(Object o)
        {
            return ((SBTLiveNode) o).getName().equals(matchClient.getClientName());
        }

        public TransportClientFilter(TransportPSClient client)
        {
            matchClient = client;
        }
    }

    /* (non-Javadoc)
     * @see com.cboe.interfaces.presentation.processes.OrbProcessCache#getAllProcesses(com.cboe.util.event.EventChannelListener)
     */
    public CBOEProcess[] getAllProcesses(EventChannelListener listener)
    {
        return (CBOEProcess[]) getNetwork(SBTLiveNode.class).getNetworkElements().toArray(new CBOEProcess[0]);
    }

    /* (non-Javadoc)
     * @see com.cboe.interfaces.presentation.processes.OrbProcessCache#unsubscribeAllProcesses(com.cboe.util.event.EventChannelListener)
     */
    public void unsubscribeAllProcesses(EventChannelListener listener)
    {
        if (listener == null)
        {
            throw new IllegalArgumentException("listener may not be null.");
        }

        ChannelKey channelKey = new ChannelKey(ChannelType.INSTRUMENTATION_PROCESS_UPDATE, new Integer(0));
        eventChannel.removeChannelListener(eventChannel, listener, channelKey);
    }

    /* (non-Javadoc)
     * @see com.cboe.interfaces.presentation.processes.OrbProcessCache#subscribeAllProcesses(com.cboe.util.event.EventChannelListener)
     */
    public void subscribeAllProcesses(EventChannelListener listener)
    {


        if (listener == null)
        {
            throw new IllegalArgumentException("listener may not be null.");
        }

        ChannelKey channelKey = new ChannelKey(ChannelType.INSTRUMENTATION_PROCESS_UPDATE, new Integer(0));
        eventChannel.addChannelListener(eventChannel, listener, channelKey);
    }

    /* (non-Javadoc)
     * @see com.cboe.interfaces.presentation.processes.OrbProcessCache#cleanUp()
     */
    public void cleanUp()
    {

    }

    /* (non-Javadoc)
     * @see com.cboe.util.channel.ChannelListener#channelUpdate(com.cboe.util.channel.ChannelEvent)
     */
    public void channelUpdate(ChannelEvent event)
    {
        ChannelKey channelKey = (ChannelKey) event.getChannel();
        if (channelKey.channelType == ChannelType.INSTRUMENTATION_ORBNAME_ALIAS_UPDATE)
        {
            OrbNameAlias alias = (OrbNameAlias) event.getEventData();
            SBTLiveNode node = (SBTLiveNode) processCache.get(alias.getOrbName());
            if (node != null)
            {
                node.setOrbNameAlias(alias);
                channelKey = new ChannelKey(ChannelType.INSTRUMENTATION_PROCESS_UPDATE, new Integer(0));
                ChannelEvent channelEvent = eventChannel.getChannelEvent(this, channelKey, node);
                eventChannel.dispatch(channelEvent);
            }
        }
    }
} // end RealTimeNetwork
