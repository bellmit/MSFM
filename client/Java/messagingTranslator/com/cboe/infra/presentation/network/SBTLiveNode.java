package com.cboe.infra.presentation.network;

// standard Java stuff

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.OutputStream;
import java.util.*;

import org.omg.CosNotifyFilter.ConstraintExp;

import com.cboe.interfaces.presentation.processes.CBOEProcess;
import com.cboe.interfaces.presentation.processes.OrbNameAlias;

import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerMMBusinessProperty;
import com.cboe.presentation.common.processes.ProcessPattern;
import com.cboe.presentation.environment.EnvironmentManagerFactory;
import com.cboe.presentation.orbNameAlias.OrbNameAliasCache;

import com.cboe.CommandConsole.CommandConsole;
import com.cboe.infra.presentation.util.RegExStringUtil;
import com.cboe.utils.monitoringService.ECClient;
import com.cboe.utils.monitoringService.EventChannel;
import com.cboe.utils.monitoringService.TransportPSClient;
import com.cboe.utils.monitoringService.TransportPSServer;
import com.cboe.utils.monitoringService.TransportPSSubject;
import com.cboe.utils.monitoringService.TransportPSNetworkConnection;

public class SBTLiveNode extends AbstractSBTNode implements PropertyChangeListener, CBOEProcess
{
    // Property constants
    public static final String NODE_NAME    = "nodeName";
    public static final String HOST_NAME    = "hostName";
    public static final String UNKNOWN_NAME = "Unknown" ;

    String orbName = UNKNOWN_NAME;
    String poaName = UNKNOWN_NAME;
    String clientInfo = UNKNOWN_NAME;
    short portNum = -1;
    String realOrbName = ""; // For command console use.

    // a single node represents either a client or a server
    TransportPSClient transportClient = null;
    TransportPSServer transportServer = null;
    long clientWriteBufferSize = 0;
    long clientReadBufferSize = 0;
    long clientQueuedMsgCount = 0;
    long clientQueuedBytesCount = 0;

    long serverWriteBufferSize = 0;
    long serverReadBufferSize = 0;
    long serverQueuedMsgCount = 0;
    long serverQueuedBytesCount = 0;
    long serverDiscardCount = 0;

    ECClient ecClient = null;
    Date createTime = null;
    transient private Collection<PropertyChangeListener> listeners = new Vector<PropertyChangeListener>();
    transient private Map<String, SBTSniffer> subjectKeysSniffer = new HashMap<String, SBTSniffer>();
    transient private Map<String, SubjectData> lastSubjects = new HashMap<String, SubjectData>();
    transient private Map<String, ChannelData> lastChannels = new HashMap<String, ChannelData>();
    transient private Map<String, SBTNetworkConnectionLiveNode> connectionMap = new HashMap<String, SBTNetworkConnectionLiveNode>();

    private Long clientWriteBufferSizeLong = 0L;
    private Long clientReadBufferSizeLong = 0L;
    private Long clientQueuedMsgCountLong = 0L;
    private Long clientQueuedBytesCountLong = 0L;

    private Long serverWriteBufferSizeLong = 0L;
    private Long serverReadBufferSizeLong = 0L;
    private Long serverQueuedMsgCountLong = 0L;
    private Long serverQueuedBytesCountLong = 0L;
    private Long serverDiscardCountLong = 0L;

    private OrbNameAlias orbNameAlias;

    //*************************************************************************
    // CTORS
    //*************************************************************************

    public SBTLiveNode()
    {
        super("Deserialized Node");
        createTime = new Date();
        orbNameAlias = OrbNameAliasCache.getInstance().getOrbNameAlias(getOrbName());
    }

    public SBTLiveNode(String name)
    {
        super(name);

        try
        {
            ProcessDetailFinder pdf = ProcessDetailFinder.getInstance();
            orbName = nodeName.replaceFirst("^/", "");
            portNum = pdf.getPortNum(orbName);
            setHost(pdf.getHost(orbName));
            realOrbName = pdf.getOrbName(orbName);
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(e);
        }

        createTime = new Date();
        orbNameAlias = OrbNameAliasCache.getInstance().getOrbNameAlias(getOrbName());
    }

    public SBTLiveNode(String name, SBTNodeType type)
    {
        super(name, type);
        if (hostName == null)
        {
            hostName = UNKNOWN_NAME;
        }

        try
        {
            ProcessDetailFinder pdf = ProcessDetailFinder.getInstance();
            orbName = nodeName.replaceFirst("^/", "");
            portNum = pdf.getPortNum(orbName);
            setHost(pdf.getHost(orbName));
            realOrbName = pdf.getOrbName(orbName);
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(e);
        }

        createTime = new Date();
        orbNameAlias = OrbNameAliasCache.getInstance().getOrbNameAlias(getOrbName());
    }

    public SBTLiveNode(TransportPSClient psClient, PropertyChangeListener observer)
    {
        this(psClient.getClientName());
        transportClient = psClient;
        clientInfo = transportClient.getClientInfo();
        listeners.add(observer);
        transportClient.addPropertyChangeListener(this);
        ecClient = MonitoringServiceProxy.getInstance().getTransportMonitor().getECClient(getName());

        try
        {
            parseName();
            orbName = nodeName.replaceFirst("^/", "");
            ProcessDetailFinder pdf = ProcessDetailFinder.getInstance();
            portNum = pdf.getPortNum(orbName);
            setHost(pdf.getHost(orbName));
            realOrbName = pdf.getOrbName(orbName);
        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(e);
        }

        if (hostName == null)
        {
            hostName = UNKNOWN_NAME;
        }

        createTime = new Date();
        orbNameAlias = OrbNameAliasCache.getInstance().getOrbNameAlias(getOrbName());
    }

    public SBTLiveNode(TransportPSServer server, String serverInfo)
    {
        this(server.getServerName());
        clientInfo = serverInfo;
        // serverInfo should look like this:
        // <ServerType>:<userOwner>@<serverHost>
        StringTokenizer infoPieces = new StringTokenizer(serverInfo, ":");
        if (infoPieces.countTokens() > 1)
        {
            infoPieces.nextToken();
            String userHost = infoPieces.nextToken();
            StringTokenizer parts = new StringTokenizer(userHost, "@");
            if (parts.countTokens() > 1)
            {
                this.userOwner = parts.nextToken();
                this.hostName = parts.nextToken();
            }
        }

        this.transportServer = server;
        this.nodeType = SBTNodeType.SERVER;
        createTime = new Date();
        orbNameAlias = OrbNameAliasCache.getInstance().getOrbNameAlias(getOrbName());
    }

    //*************************************************************************
    // Simple properties
    //*************************************************************************
    public boolean isAlive()
    {
        boolean rv = false;
        if (transportClient != null)
        {
            rv = transportClient.getIsAlive();
        }
        else if (transportServer != null)
        {
            rv = transportServer.getIsAlive();
        }
        return rv;
    }

    public String getServerName()
    {
        String rv;
        if (transportClient != null && transportClient.getServerName() != null)
        {
            rv = transportClient.getServerName();
        }
        else if (transportServer != null && transportServer.getServerName() != null)
        {
            rv = transportServer.getServerName();
        }
        else
        {
            rv = UNKNOWN_NAME;
        }
        return rv;
    }

    /**
     * Overrides super.setIgnoringAdmin(boolean) to notify property change listeners of this event
     */
    public void setIgnoringAdmin(boolean ignore)
    {
        if (ignore != ignoreAdmin)
        {
            super.setIgnoringAdmin(ignore);
            firePropertyChangeEvent("IGNORE_ADMIN", ignoreAdmin, ignore);
        }
    }

    public boolean isQueued()
    {
        boolean rv = false;
        if (transportClient != null)
        {
            rv = (transportClient.getServerBufferStats().getMsgQueueCount() > 0);
        }
        return rv;
    }

    public String getHost()
    {
        return hostName;
    }

    /**
     * Set the host name - normally hostname is derived from the underlying TransportPSClient But in cases where
     * clientInfo is null or unparseable, we need a backup.  For this reason, We use the ProcessDetailFinder to divine
     * this information (and other stuff not available from TransportPSClient) and call setHost() after the node is
     * constructed.  Note that host name is a bound property.
     */
    public void setHost(String theHostName)
    {
        if (hostName == null || (theHostName != null & !(hostName.equals(theHostName))))
        {
            String old = hostName;
            hostName = theHostName;
            firePropertyChangeEvent(HOST_NAME, old, hostName);
        }
    }

    public Date getLastUpdateTime()
    {
        Date rv = new Date();
        if (transportClient != null)
        {
            rv = transportClient.getTimeUpdated();
        }
        return rv;
    }

    public long getClientWriteBufferSize()
    {
        if (transportClient != null)
        {
            clientWriteBufferSize = transportClient.getBufferStats().getWriteBufferCount();
        }
        //updateClientBufferStats();
        return clientWriteBufferSize;
    }

    public long getClientReadBufferSize()
    {
        if (transportClient != null)
        {
            clientReadBufferSize = transportClient.getBufferStats().getReadBufferCount();
        }
        //updateClientBufferStats();
        return clientReadBufferSize;
    }

    public long getClientQueuedMsgCount()
    {
        if (transportClient != null)
        {
            clientQueuedMsgCount = transportClient.getBufferStats().getMsgQueueCount();
        }
        //updateClientBufferStats();
        return clientQueuedMsgCount;
    }

    public long getClientQueuedBytesCount()
    {
        if (transportClient != null)
        {
            clientQueuedBytesCount = transportClient.getBufferStats().getMsgQueueByteCount();
        }
        //updateClientBufferStats();
        return clientQueuedBytesCount;
    }

    public Date getClientBufferUpdateTime()
    {
        Date rv = new Date();
        if (transportClient != null)
        {
            rv = transportClient.getBufferStats().getTimeUpdated();
        }
        return rv;
    }

    public long getServerWriteBufferSize()
    {
        if (transportClient != null)
        {
            serverWriteBufferSize = transportClient.getServerBufferStats().getWriteBufferCount();
        }
//        updateServerBufferStats();
        return serverWriteBufferSize;
    }

    public long getServerReadBufferSize()
    {
        if (transportClient != null)
        {
            serverReadBufferSize = transportClient.getServerBufferStats().getReadBufferCount();
        }
//        updateServerBufferStats();
        return serverReadBufferSize;
    }


    public long getServerQueuedMsgCount()
    {
        if (transportClient != null)
        {
            serverQueuedMsgCount = transportClient.getServerBufferStats().getMsgQueueCount();
        }
//        updateServerBufferStats();
        return serverQueuedMsgCount;
    }

    public long getServerQueuedBytesCount()
    {
        if (transportClient != null)
        {
            serverQueuedBytesCount = transportClient.getServerBufferStats().getMsgQueueByteCount();
        }
//        updateServerBufferStats();
        return serverQueuedBytesCount;
    }

    public long getServerDiscardCount()
    {
        StringBuffer b = new StringBuffer();
        if (transportClient != null)
        {
            long prevServerDiscardCount = serverDiscardCount;
            serverDiscardCount = transportClient.getServerBufferStats().getDiscardedCount();
            if(prevServerDiscardCount != serverDiscardCount)
            {
                b.append("Server Discards changed from--(") ;
                b.append(prevServerDiscardCount);
                b.append( ") to -->(");
                b.append(serverDiscardCount);
                b.append(")--for--");
                b.append(getHostName());
                b.append(getName());
                GUILoggerHome.find().alarm( b.toString() );
            }           
        }
        return serverDiscardCount;
    }

    public Date getServerBufferUpdateTime()
    {
        Date rv = new Date();
        if (transportClient != null)
        {
            rv = transportClient.getServerBufferStats().getTimeUpdated();
        }
        return rv;
    }

    public Date getUpdateTime(String propertyName)
    {
        Date rv = createTime;
        if (transportClient != null)
        {
            rv = transportClient.getTimeUpdated(propertyName);
        }
        return rv;
    }

    //*************************************************************************
    // java.lang.Object overrides
    //*************************************************************************
    public boolean equals(Object o)
    {
        boolean rv = false;
        if (o != null && o instanceof SBTLiveNode)
        {
            // use reference equality here!
            SBTLiveNode castedNode = (SBTLiveNode) o;
            if (this.transportClient != null)
            {
                rv = this.transportClient == castedNode.transportClient;
            }
            else if (this.transportServer != null)
            {
                rv = this.transportServer == castedNode.transportServer;
            }
        }
        return rv;
    }

    /**
     * Note that this method conforms with the general notion of java.lang.Clone. Specifically:  x != x.clone() &&
     * x.getClass().equals(x.clone().getClass()) However, it is <em>not true</em> that x.clone().equals(x), since this
     * method returns a static copy of the node (i.e. the underlying TransportPSClient is not present).
     * @return Object a static copy of this node.
     */
    public Object clone()
    {
        SBTLiveNode copy = null;
        try
        {
            copy = (SBTLiveNode) super.clone();
        }
        catch (CloneNotSupportedException cnse)
        {
            copy = new SBTLiveNode(this.nodeName, this.nodeType);
            copy.setHost(this.hostName);
            copy.userOwner = this.userOwner;
            copy.setIgnoringAdmin(this.ignoreAdmin);
        }
        // update the lastSubjects with latest timely info
        copy.getSubjects();
        copy.transportClient = null;
        copy.transportServer = null;
        //update lastChannels with latest timely info
        copy.getChannels();
        copy.ecClient = null;
        return copy;
    }

    public int hashCode()
    {
        int rv = super.hashCode();
        if (transportClient != null)
        {
            rv = transportClient.hashCode();
        }
        else if (transportServer != null)
        {
            rv = transportServer.hashCode();
        }
        return rv;
    }

    //*************************************************************************
    // Topics
    //*************************************************************************
    public Topic[] getGlobalPublishList()
    {
        List<Topic> topics = new ArrayList<Topic>();
        Collection<TransportPSSubject> subjects = getSubjects();
        TransportPSSubject[] subs = new ArrayList<TransportPSSubject>(subjects).toArray(new TransportPSSubject[0]);

        // to create a Topic, we need the Channel name, and the extent name
        // but since Channels and Extents are CBOE extractions, we will have
        // to manually parse them from the subject names that are provided to us
        for (TransportPSSubject sub : subs)
        {
            if (!talksOnSubject(sub))
            {
                continue; // publish only!
            }
            Topic t = TopicCache.getInstance().getTopic(sub.getSubject(), "global");

            if (t != null)
            {
                topics.add(t);
            }
        }
        return topics.toArray(new Topic[0]);
    }

    public Topic[] getGlobalSubscribeList()
    {
        List<Topic> topics = new ArrayList<Topic>();
        Collection<TransportPSSubject> subjects = getSubjects();
        TransportPSSubject[] subs = new ArrayList<TransportPSSubject>(subjects).toArray(new TransportPSSubject[0]);

        // to create a Topic, we need the Channel name, and the extent name
        // but since Channels and Extents are CBOE extractions, we will have
        // to manually parse them from the subject names that are provided to us
        for (TransportPSSubject sub : subs)
        {
            if (!listensOnSubject(sub))
            {
                continue; // subscribes only!
            }
            Topic t = TopicCache.getInstance().getTopic(sub.getSubject(), "global");
            if (t != null)
            {
                topics.add(t);
            }
        }

        return topics.toArray(new Topic[0]);
    }

    /**
     * Get all subjects.   For each channel, filter out the subjects that do not belong to the current channel.   For
     * those remaining, filter out those that are not local or are "admin" subjects.  For the remaiing, create a topic
     * for each channel/subject pair.
     */
    public Topic[] getLocalPublishList()
    {
        List<Topic> topics = new ArrayList<Topic>();
        Collection<TransportPSSubject> subjects = getSubjects();
        TransportPSSubject[] subs = new ArrayList<TransportPSSubject>(subjects).toArray(new TransportPSSubject[0]);

        // to create a Topic, we need the Channel name, and the extent name
        // but since Channels and Extents are CBOE extractions, we will have
        // to manually parse them from the subject names that are provided to us
        for (TransportPSSubject sub : subs)
        {
            if (!talksOnSubject(sub))
            {
                continue; // publish only!
            }
            Topic t = TopicCache.getInstance().getTopic(sub.getSubject(), "local");

            if (t != null)
            {
                topics.add(t);
            }
        }
        return topics.toArray(new Topic[0]);
    }

    /**
     *
     */
    public Topic[] getLocalSubscribeList()
    {
        List<Topic> topics = new ArrayList<Topic>();
        Collection<TransportPSSubject> subjects = getSubjects();
        TransportPSSubject[] subs = new ArrayList<TransportPSSubject>(subjects).toArray(new TransportPSSubject[0]);

        // to create a Topic, we need the Channel name, and the extent name
        // but since Channels and Extents are CBOE extractions, we will have
        // to manually parse them from the subject names that are provided to us
        for (TransportPSSubject sub : subs)
        {
            if (!listensOnSubject(sub))
            {
                continue; // subscribes only!
            }
            Topic t = TopicCache.getInstance().getTopic(sub.getSubject(), "local");
            if (t != null)
            {
                topics.add(t);
            }
        }

        return topics.toArray(new Topic[0]);
    }

    //*************************************************************************
    // EC Channels & related stuff
    //*************************************************************************
    public boolean belongsToChannel(EventChannel channel)
    {
        return getChannels().contains(channel);
    }

    public EventChannel getChannel(String channelName)
    {
        EventChannel rv = null;
        if (getECClient() != null)
        {
            rv = ecClient.getEventChannel(channelName);
        }
        return rv;
    }

    public Collection<EventChannel> getChannels()
    {
        ArrayList<EventChannel> channelList = new ArrayList<EventChannel>();
        if (getECClient() != null)
        {
            lastChannels.clear();
            String[] channelNames = ecClient.getEventChannels();
            for (String channelName : channelNames)
            {
                EventChannel ec = ecClient.getEventChannel(channelName);
                if (ec != null)
                {
                    channelList.add(ec);
                    lastChannels.put(ec.getEcName(), new ChannelData(ec));
                }
            }
        }
        return channelList;
    }

    /**
     * Check to see if this node is listed as participating in this channel, according to the underlying ECClient.  Note
     * that this is a literal name match, and does not account for multiple monitorChannelPrefix values
     * @return True if the channel name is among the array of Strings reported by the ECClient object belonging to this
     *         client, false otherwise.
     */
    public boolean belongsToChannel(String channelName)
    {
        boolean rv = false;
        if (getECClient() != null)
        {
            String[] channelNames = ecClient.getEventChannels();
            for (String name : channelNames)
            {
                //if( RegExStringUtil.strip( MONITOR_PREFIX, channelNames[k] ).equals( channelName ) )
                if (name.equals(channelName))
                {
                    rv = true;
                    break;
                }
            }
        }
        else
        {
            if (GUILoggerHome.find().isDebugOn())
            {
                GUILoggerHome.find().debug(
                        "SBTLiveNode(" + getName() + ")::belongsToChannel(String): getECClient() returned NULL",
                        GUILoggerMMBusinessProperty.CHANNELS);
            }
        }
        return rv;
    }

    public Map<String, ChannelData> getLastChannels()
    {
        return lastChannels;
    }


    //*************************************************************************
    // Filters & related stuff
    //*************************************************************************
    public Collection<String[]> getECFilters(String channel)
    {
        Collection<String[]> rv = new ArrayList<String[]>();
        EventChannel ec = ecClient.getEventChannel(channel);
        for (Iterator iterator = ec.getECInterfaceNames().iterator(); iterator.hasNext();)
        {
            String ecName = (String) iterator.next();
            String[] inclFilters = ec.getECInterface(ecName).getInclusionFilters();
            for (int m = 0; m < inclFilters.length; m++)
            {
                rv.add(new String[]{ecName, "Unknown", inclFilters[m]});

            }
            String[] exclFilters = ec.getECInterface(ecName).getExclusionFilters();
            for (int p = 0; p < exclFilters.length; p++)
            {
                rv.add(new String[]{ecName, "Unknown", exclFilters[p]});
            }
        }
        return rv;
    }

    public Collection<String[]> getFilters(String channel) throws ServiceNotAvailableException
    {
        Collection<String[]> filterEntries = new ArrayList<String[]>();
        if (realOrbName == null || realOrbName.equals("Unknown") || portNum == -1)
        {
            throw new ServiceNotAvailableException("No CommandConsole object was bound for " + realOrbName + "@" +
                                                   hostName + ":" + portNum +
                                                   ".  \n\nThe most likely cause is bad or missing process configuration data in " +
                                                   EnvironmentManagerFactory.find().getCurrentEnvironment()
                                                           .getProcessListRef());
        }

        if (isDN())
        {
            throw new ServiceNotAvailableException(
                    "Filters not available for DN via CommandConsole interface.  Use DNConsoleUtil.");
        }

        try
        {
            CommandConsole cc = CommandConsoleProxy.getInstance().getCommandConsole(realOrbName, hostName, portNum);
            ConstraintExp[] filters = cc.getFiltersForChannel(channel);
            for (ConstraintExp filter : filters)
            {
                String[] filterEntry = new String[3];
                filterEntry[0] = filter.event_types[0].domain_name;
                filterEntry[1] = filter.event_types[0].type_name;
                filterEntry[2] = filter.constraint_expr;
                filterEntries.add(filterEntry);
            }
        }
        catch (NullPointerException npe)
        {
            GUILoggerHome.find().exception(
                    "!ERROR! SBTLiveNode.getFilters() - no CommandConsole configuration data available for this node.",
                    npe);
            throw new ServiceNotAvailableException(
                    "No command console object bound for " + realOrbName + "@" + hostName + ":" + portNum);
        }
        catch (org.omg.CORBA.BAD_OPERATION boe)
        {
            GUILoggerHome.find().exception("!ERROR! SBTLiveNode.getFilters() - process " + realOrbName +
                                           " does not support the correct version of the CommandConsole IDL ", boe);
            throw new ServiceNotAvailableException("No filter data was retrieved for " + realOrbName + "@" + hostName +
                                                   ":" + portNum +
                                                   ".\n\nThis process uses an older version of CommandConsole.idl that does not support getFiltersForChannel() ");
        }
        catch (Throwable t)
        {
            GUILoggerHome.find().exception(
                    "!ERROR! SBTLiveNode.getFilters() - cmdConsole.getFiltersForChannel() failed: " + t.getMessage(),
                    t);
            throw new ServiceNotAvailableException(
                    "Unknown error retrieving filters for " + realOrbName + "@" + hostName +
                    ":" + portNum +
                    ".  \n\nThe most likely cause is bad or missing process configuration data in " +
                    EnvironmentManagerFactory.find().getCurrentEnvironment()
                            .getProcessListRef() + "\nActual exception is " +
                                                 t.getMessage());
        }

        return filterEntries;
    }

    //*************************************************************************
    // Subjects & related stuff
    //*************************************************************************
    /**
     * Returns the subjects this client is subscribed to, or an empty Collection if
     * no subscriptions.
     */
    public Collection<TransportPSSubject> getSubjects()
    {
        Collection<TransportPSSubject> rv = new ArrayList<TransportPSSubject>();
        if (transportClient != null)
        {
            rv.addAll(transportClient.getSubjects());
//            lastSubjects.clear();
            lastSubjects = new HashMap<String, SubjectData>();
            for (TransportPSSubject subject : rv)
            {
                lastSubjects.put(subject.getSubject(), new SubjectData(subject));
            }
        }
        return rv;
    }

    /**
     * Returns the subjects this client is subscribed to that are attached to the channel provided.  Note that the
     * subject->channel relationship is inferred from the subject name; Transports have no notion of a "channel".
     */
    public Collection<TransportPSSubject> getSubjects(String channelName)
    {
        ArrayList<TransportPSSubject> rv = new ArrayList<TransportPSSubject>();
        for (TransportPSSubject transportPSSubject : getSubjects())
        {
            String subjectName = transportPSSubject.getSubject().replaceFirst("/", "");
            if (subjectName.startsWith(channelName))
            {
                rv.add(transportPSSubject);
            }
        }
        return rv;
    }

    public boolean talksOnSubject(TransportPSSubject subject)
    {
        boolean rv = false;
        if (subject != null && subject.getMsgsSent() > 0)
        {
            rv = true;
        }
        return rv;
    }

    public boolean talksOnSubject(String subjectName)
    {
        boolean rv = false;
        TransportPSSubject s = getSubject(subjectName);
        if (s != null && s.getMsgsSent() > 0)
        {
            rv = true;
        }
        else if (s == null)
        {
            if (GUILoggerHome.find().isDebugOn())
            {
                GUILoggerHome.find().debug(
                        "Could not retrieve subject name : " + subjectName + " in talksOnSubject(...)",
                        GUILoggerMMBusinessProperty.SUBJECTS);
            }

        }
        return rv;
    }

    public boolean listensOnSubject(TransportPSSubject subject)
    {
        boolean rv = false;
        if (subject != null && subject.getMsgsRecv() > 0)
        {
            rv = true;
        }
        return rv;
    }

    public boolean listensOnSubject(String subjectName)
    {
        boolean rv = false;
        TransportPSSubject s = getSubject(subjectName);
        if (s != null && s.getMsgsRecv() > 0)
        {
            rv = true;
        }
        return rv;
    }

    public Map getLastSubjects()
    {
        return lastSubjects;
    }

    public List<TransportPSSubject> getSubjectsForChannel(String channelName)
    {
        if (!channelName.startsWith("/"))
        {
            StringBuffer buf = new StringBuffer(channelName);
            buf.insert(0, "/");
            channelName = buf.toString();
        }
        ArrayList<TransportPSSubject> rv = new ArrayList<TransportPSSubject>();
        for (TransportPSSubject transportPSSubject : getSubjects())
        {
            if (transportPSSubject.getSubject().startsWith(channelName))
            {
                rv.add(transportPSSubject);
            }
        }
        return rv;
    }


    //*************************************************************************
    // Message sniffing
    //*************************************************************************
    /**
     * Create a subject sniffer and get it ready, but do not automatically
     * start sniffing messages
     */
    public SBTSniffer createSniffer(String subjectName, OutputStream out)
    {
        SBTSniffer rv = subjectKeysSniffer.get(subjectName);
        if (rv == null)
        {
            TransportPSSubject subj = getSubject(subjectName);
            if (subj != null)
            {
                rv = new SBTSniffer(subj, out);
                subjectKeysSniffer.put(subjectName, rv);
            }
        }
        return rv;

    }

    /**
     * Start sniffing (recording) messages.  No default destination is supplied.  This method is equivalent to invoking
     * startSniffing(subjectName,null); To obtain the message data obtained by sniffing, add a SnifferListener to the
     * SBTSniffer
     */
    public SBTSniffer startSniffing(String subjectName)
    {
        return startSniffing(subjectName, null);
    }

    /**
     * Start sniffing (recording) messages -- send sniffer output to <code>out</code>
     * @param subjectName The subjectName of messages to sniff
     * @param out The destination to record message data/contents to
     * @return The SBTSniffer created (or retrieved if already sniffing on this subject)
     */
    public SBTSniffer startSniffing(String subjectName, OutputStream out)
    {
        SBTSniffer rv = createSniffer(subjectName, out);
        rv.startSniffing();
        return rv;
    }

    public boolean stopSniffing(String subjectName)
    {
        boolean rv = false;
        SBTSniffer sniffer = subjectKeysSniffer.get(subjectName);
        if (sniffer != null)
        {
            sniffer.stopSniffing();
            rv = true;
        }

        return rv;
    }

    //*************************************************************************
    // Property change support
    //*************************************************************************
    public void addPropertyChangeListener(PropertyChangeListener l)
    {
        listeners.add(l);
    }


    public void removePropertyChangeListener(PropertyChangeListener l)
    {
        listeners.remove(l);
    }


    public void propertyChange(PropertyChangeEvent pce)
    {
        if (pce.getPropertyName().equals(TransportPSClient.CLIENT_INFO))
        {
            String newValue = (String) pce.getNewValue();
            if (clientInfo == null || !clientInfo.equals(newValue))
            {
                parseName();
                firePropertyChangeEvent(pce.getPropertyName(), pce.getOldValue(), pce.getNewValue());
            }
        }

        if (!pce.getPropertyName().equals(TransportPSClient.IS_ALIVE))
        {
            return;
        }
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug("Node " + this.getName() + " firing ACTIVE/INACTIVE property change " + pce,
                                       GUILoggerMMBusinessProperty.MESSAGEMON);
        }

        firePropertyChangeEvent(pce.getPropertyName(), pce.getOldValue(), pce.getNewValue());
    }


    /**
     * This method attempts to divine the node type based on the existing naming conventions at CBOE.  The reliability
     * and robustness of this approach has not been investigated in detail, but I would be extremely dubious of relying
     * on this method.
     */
    protected SBTNodeType deriveTypeFromName(String name)
    {
        ProcessPattern processPattern = ProcessPattern.getInstance();
        String casPattern = processPattern.getCASPattern("", "");
//        String icsPattern = processPattern.getICSPattern("", "");
        String dnPattern = processPattern.getDNPattern("", "");
//        String bcPattern = processPattern.getBCPattern("", "");
//        String gcPattern = processPattern.getGCPattern("", "");
        String frontendPattern = processPattern.getFrontendPattern("", "");
        String serverPattern = processPattern.getRTServerPattern("", "");
        String cFIXPattern = processPattern.getCFIXPattern("", "");

        if (name.matches(dnPattern))
        {
            // this is a DN	client
            return SBTNodeType.DN_NODE;
        }
        else if (name.matches(casPattern) || name.matches(cFIXPattern))
        {
            // this is a CAS client
            return SBTNodeType.CAS_NODE;
        }
        else
        {
            if (name.matches(frontendPattern))
            {
                // this is a FrontEnd client
                return SBTNodeType.FE_NODE;
            }
            else
            {
                if (name.matches(serverPattern))
                {
                    return SBTNodeType.SERVER;
                }
                else
                {
                    // this is a GC client **OR** a BC client
                    return SBTNodeType.OR_NODE;
                }
            }
        }
    }

    /*
    Assigns values to host and userOwner from name parsing;
    */
    protected void parseName()
    {
        String info = getClientInfo();

        if (info == null)
        {
        }
        try
        {
            StringTokenizer toker = new StringTokenizer(info, "@:");
            toker.nextToken();
            userOwner = toker.nextToken();
            hostName = toker.nextToken();
        }
        catch (Exception noclientinfo)
        {
            try
            {
                if (getType() == SBTNodeType.DN_NODE)
                {
                    hostName = getName().substring(getName().lastIndexOf("ste"), getName().length() - 4);
                }
                else
                {
                    hostName = getName().substring(getName().lastIndexOf("ste"), getName().length());
                }
            }
            catch (Exception trycas)
            {
                int index = getName().lastIndexOf("devcas");
                if (index > -1)
                {
                    hostName = getName().substring(index, getName().length());
                }
            }
        } // end no-client-info catch
    } // end parseName

    //*************************************************************************
    // non-public accessor methods
    //*************************************************************************
    protected String getClientInfo()
    {
        if (transportClient != null)
        {
            clientInfo = transportClient.getClientInfo();
        }
        return clientInfo;
    }


    protected TransportPSSubject getSubject(String subjectName)
    {
        TransportPSSubject rv = null;
        for (TransportPSSubject transportPSSubject : getSubjects())
        {
            if (transportPSSubject.getSubject().equals(subjectName))
            {
                rv = transportPSSubject;
                break;
            }
        }
        return rv;
    }

    protected String getExtent(TransportPSSubject subject)
    {
        String subjectName = subject.getSubject();
        subjectName = RegExStringUtil.extract("---.+---", subjectName);
        return subjectName.substring(3, subjectName.length() - 3);
    }

    protected ECClient getECClient()
    {
        if (ecClient == null)
        {
            ecClient = MonitoringServiceProxy.getInstance().getTransportMonitor().getECClient(getName());
        }
        return ecClient;
    }


    //*************************************************************************
    // Utility methods
    //*************************************************************************
    /**
     * Notify interested parties that an underlying property of this node has changed -
     * note that this level, this method provides a central access point for event channel,
     * talarian, and PW-type property changes.
     */
    protected void firePropertyChangeEvent(String propName, Object oldValue, Object newValue)
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug(
                    "SBTLiveNode.firePropertyChangeEvent(" + propName + "," + oldValue + "," + newValue + ")",
                    GUILoggerMMBusinessProperty.MESSAGEMON);
        }

        PropertyChangeEvent morphedEvent = new PropertyChangeEvent(this, propName, oldValue, newValue);
        for (PropertyChangeListener listener : listeners)
        {
            listener.propertyChange(morphedEvent);
        }
    }

    protected void updateClientBufferStats()
    {
        if (transportClient != null)
        {
            clientWriteBufferSize = transportClient.getBufferStats().getWriteBufferCount();
            clientReadBufferSize = transportClient.getBufferStats().getReadBufferCount();
            clientQueuedMsgCount = transportClient.getBufferStats().getMsgQueueCount();
            clientQueuedBytesCount = transportClient.getBufferStats().getMsgQueueByteCount();
        }
    }

    protected void updateServerBufferStats()
    {
        if (transportClient != null)
        {
            serverWriteBufferSize = transportClient.getServerBufferStats().getWriteBufferCount();
            serverReadBufferSize = transportClient.getServerBufferStats().getReadBufferCount();
            serverQueuedMsgCount = transportClient.getServerBufferStats().getMsgQueueCount();
            serverQueuedBytesCount = transportClient.getServerBufferStats().getMsgQueueByteCount();
            serverDiscardCount = transportClient.getServerBufferStats().getDiscardedCount();
        }
    }


    public Long getClientReadBufferSizeLong()
    {
        if (clientReadBufferSizeLong != getClientReadBufferSize())
        {
            clientReadBufferSizeLong = getClientReadBufferSize();
        }
        return clientReadBufferSizeLong;
    }

    public Long getClientWriteBufferSizeLong()
    {
        if (clientWriteBufferSizeLong != getClientWriteBufferSize())
        {
            clientWriteBufferSizeLong = getClientWriteBufferSize();
        }
        return clientWriteBufferSizeLong;
    }

    public Long getClientQueuedMsgCountLong()
    {
        if (clientQueuedMsgCountLong != getClientQueuedMsgCount())
        {
            clientQueuedMsgCountLong = getClientQueuedMsgCount();
        }
        return clientQueuedMsgCountLong;
    }

    public Long getClientQueuedBytesCountLong()
    {
        if (clientQueuedBytesCountLong != getClientQueuedBytesCount())
        {
            clientQueuedBytesCountLong = getClientQueuedBytesCount();
        }
        return clientQueuedBytesCountLong;
    }

    public Long getServerReadBufferSizeLong()
    {
        if (serverReadBufferSizeLong != getServerReadBufferSize())
        {
            serverReadBufferSizeLong = getServerReadBufferSize();
        }
        return serverReadBufferSizeLong;
    }

    public Long getServerWriteBufferSizeLong()
    {
        if (serverWriteBufferSizeLong != getServerWriteBufferSize())
        {
            serverWriteBufferSizeLong = getServerWriteBufferSize();
        }
        return serverWriteBufferSizeLong;
    }

    public Long getServerQueuedMsgCountLong()
    {
        if (serverQueuedMsgCountLong != getServerQueuedMsgCount())
        {
            serverQueuedMsgCountLong = getServerQueuedMsgCount();
        }
        return serverQueuedMsgCountLong;
    }

    public Long getServerQueuedBytesCountLong()
    {
        if (serverQueuedBytesCountLong != getServerQueuedBytesCount())
        {
            serverQueuedBytesCountLong = getServerQueuedBytesCount();
        }
        return serverQueuedBytesCountLong;
    }

    public Long getServerDiscardCountLong()
    {
        if (serverDiscardCountLong != getServerDiscardCount())
        {
            serverDiscardCountLong = getServerDiscardCount();
        }
        return serverDiscardCountLong;
    }

    public short getPortNumber()
    {
        return portNum;
    }

    public String getOrbName()
    {
        return orbName;
    }

    public String getRealOrbName()
    {
        return realOrbName;
    }

    public void setPortNumber(short port)
    {
        this.portNum = port;
    }

    public SBTNetworkConnectionLiveNode[] getNetworkConnectionList()
    {
        if (transportServer != null)
        {
            for (Object obj : transportServer.getNetworkConnectionList())
            {
                TransportPSNetworkConnection transport = (TransportPSNetworkConnection) obj;

                SBTNetworkConnectionLiveNode connection  = connectionMap.get(transport.getNetworkConnectionName());
                if (connection == null)
                {
                    connectionMap.put(transport.getNetworkConnectionName(), new SBTNetworkConnectionLiveNode(transport)); 
                }
            }
        }

        return connectionMap.values().toArray(new SBTNetworkConnectionLiveNode[0]);
    }

    /**
     * CBOEProcess Implementation
     */
    /* (non-Javadoc)
     * @see com.cboe.interfaces.presentation.processes.CBOEProcess#getProcessName()
     */
    public String getProcessName()
    {
        return getName();
    }

    /* (non-Javadoc)
     * @see com.cboe.interfaces.presentation.processes.CBOEProcess#getHostName()
     */
    public String getHostName()
    {
        return getHost();
    }
    
    /* (non-Javadoc)
     * @see com.cboe.interfaces.presentation.processes.CBOEProcess#getPort()
     */
    public int getPort()
    {
        return getPortNumber();
    }

    /* (non-Javadoc)
     * @see com.cboe.interfaces.presentation.processes.CBOEProcess#getProcessType()
     */
    public int getProcessType()
    {
        return 0;
    }

    /* (non-Javadoc)
     * @see com.cboe.interfaces.presentation.processes.CBOEProcess#getOnlineStatus()
     */
    public short getOnlineStatus()
    {
        return 0;
    }

    /* (non-Javadoc)
     * @see com.cboe.interfaces.presentation.processes.CBOEProcess#getOnlineStatusOriginator()
     */
    public String getOnlineStatusOriginator()
    {
        return "";
    }

    /* (non-Javadoc)
     * @see com.cboe.interfaces.presentation.processes.CBOEProcess#getOnlineStatusReasonCode()
     */
    public short getOnlineStatusReasonCode()
    {
        return 0;
    }

    /* (non-Javadoc)
     * @see com.cboe.interfaces.presentation.processes.CBOEProcess#getMasterSlaveStatus()
     */
    public short getMasterSlaveStatus()
    {
        return 0;
    }

    /* (non-Javadoc)
     * @see com.cboe.interfaces.presentation.processes.CBOEProcess#getPoaStatus()
     */
    public short getPoaStatus()
    {
        return 0;
    }

    /* (non-Javadoc)
     * @see com.cboe.interfaces.presentation.processes.CBOEProcess#getPoaStatusOriginator()
     */
    public String getPoaStatusOriginator()
    {
        return "";
    }

    /* (non-Javadoc)
     * @see com.cboe.interfaces.presentation.processes.CBOEProcess#getPoaStatusReasonCode()
     */
    public short getPoaStatusReasonCode()
    {
        return 0;
    }

    /* (non-Javadoc)
     * @see com.cboe.interfaces.presentation.processes.CBOEProcess#getOnlinePoaStatusCombo()
     */
    public short getOnlinePoaStatusCombo()
    {
        return 0;
    }

    /* (non-Javadoc)
     * @see com.cboe.interfaces.presentation.processes.CBOEProcess#isCAS()
     */
    public boolean isCAS()
    {
        return false;
    }

    /* (non-Javadoc)
     * @see com.cboe.interfaces.presentation.processes.CBOEProcess#getDisplayName()
     */
    public String getDisplayName()
    {
        if (orbNameAlias != null)
        {
            return orbNameAlias.getDisplayName();
        }
        return nodeName;
    }

    /* (non-Javadoc)
     * @see com.cboe.interfaces.presentation.processes.CBOEProcess#getCluster()
     */
    public String getCluster()
    {
        if (orbNameAlias != null)
        {
            return orbNameAlias.getCluster();
        }
        return getType().toString();
    }

    /* (non-Javadoc)
     * @see com.cboe.interfaces.presentation.processes.CBOEProcess#getSubCluster()
     */
    public String getSubCluster()
    {
        if (orbNameAlias != null)
        {
            return orbNameAlias.getSubCluster();
        }
        String host = getHost();
        if (host == null || host.length() == 0)
        {
            host = "unknown";
        }
        return host;
    }

    /* (non-Javadoc)
     * @see com.cboe.interfaces.presentation.processes.CBOEProcess#getClusterSubClusterName()
     */
    public String getClusterSubClusterName()
    {
        return null;
    }

    /* (non-Javadoc)
     * @see com.cboe.interfaces.presentation.processes.CBOEProcess#getFirmName()
     */
    public String getFirmName()
    {
        return null;
    }

    /* (non-Javadoc)
     * @see com.cboe.interfaces.presentation.processes.CBOEProcess#getCurrentUsersSize()
     */
    public long getCurrentUsersSize()
    {
        return 0;
    }

    /* (non-Javadoc)
     * @see com.cboe.interfaces.presentation.processes.CBOEProcess#getCurrentMaxQueueSize()
     */
    public long getCurrentMaxQueueSize()
    {
        return 0;
    }

    /* (non-Javadoc)
     * @see com.cboe.interfaces.presentation.processes.CBOEProcess#getLastStatusUpdateTime()
     */
    public Date getLastStatusUpdateTime()
    {
        return null;
    }

    /* (non-Javadoc)
     * @see com.cboe.interfaces.presentation.processes.CBOEProcess#getLastStatusUpdateTimeMillis()
     */
    public long getLastStatusUpdateTimeMillis()
    {
        return 0;
    }

    /* (non-Javadoc)
     * @see com.cboe.interfaces.presentation.processes.CBOEProcess#getCurrentMemoryUsage()
     */
    public long getCurrentMemoryUsage()
    {
        return 0;
    }

    /* (non-Javadoc)
     * @see com.cboe.interfaces.presentation.processes.CBOEProcess#getHeapMemorySize()
     */
    public long getHeapMemorySize()
    {
        return 0;
    }

    /* (non-Javadoc)
     * @see com.cboe.interfaces.presentation.processes.CBOEProcess#getCommandLineArgs()
     */
    public Properties getCommandLineArgs()
    {
        return null;
    }

    /* (non-Javadoc)
     * @see com.cboe.interfaces.presentation.processes.CBOEProcess#getXMLConfiguration()
     */
    public String getXMLConfiguration()
    {
        return null;
    }

    /* (non-Javadoc)
     * @see com.cboe.interfaces.presentation.processes.CBOEProcess#getVersionString()
     */
    public String getVersionString()
    {
        return null;
    }

    /* (non-Javadoc)
     * @see com.cboe.interfaces.presentation.processes.CBOEProcess#getHardwareLocation()
     */
    public String getHardwareLocation()
    {
        return null;
    }

    /* (non-Javadoc)
     * @see com.cboe.interfaces.presentation.processes.CBOEProcess#getHardwareManufacturer()
     */
    public String getHardwareManufacturer()
    {
        return null;
    }

    /* (non-Javadoc)
     * @see com.cboe.interfaces.presentation.processes.CBOEProcess#getHardwareModel()
     */
    public String getHardwareModel()
    {
        return null;
    }

    /* (non-Javadoc)
     * @see com.cboe.interfaces.presentation.processes.CBOEProcess#getIpAddress()
     */
    public String getIpAddress()
    {
        return null;
    }

    /* (non-Javadoc)
     * @see com.cboe.interfaces.presentation.processes.CBOEProcess#getNetworkConnectivityProvider()
     */
    public String getNetworkConnectivityProvider()
    {
        return null;
    }

    /* (non-Javadoc)
     * @see com.cboe.interfaces.presentation.processes.CBOEProcess#getSoftwareVendor()
     */
    public String getSoftwareVendor()
    {
        return null;
    }

    /* (non-Javadoc)
     * @see com.cboe.interfaces.presentation.processes.CBOEProcess#getIpAddressAndPort()
     */
    public String getIpAddressAndPort()
    {
        return getHostName() + ":" + getPortNumber();
    }

    /* (non-Javadoc)
     * @see com.cboe.interfaces.presentation.processes.CBOEProcess#getFrontEnd()
     */
    public String getFrontEnd()
    {
        return null;
    }

    /* (non-Javadoc)
     * @see com.cboe.interfaces.presentation.processes.CBOEProcess#isSubscribedForInstrumentors()
     */
    public boolean isSubscribedForInstrumentors()
    {
        return false;
    }

    /* (non-Javadoc)
     * @see com.cboe.interfaces.presentation.common.businessModels.BusinessModel#getKey()
     */
    public Object getKey()
    {
        return null;
    }

    public void setOrbNameAlias(OrbNameAlias alias)
    {
        this.orbNameAlias = alias;
    }

    public String getProcessMode()
    {
        return DEFAULT_PROCESS_MODE;
    }

    public boolean isIcsManager()
    {
        return false;
    }

    public boolean isIcsWorker()
    {
        return false;
    }

    /**
     * Identifies if the process is an ICS.
     */
    public boolean isICS()
    {
        return false;
    }

    public String[] getLogicalNames()
    {
        return new String[]{getOrbName()};
    }

//    public void dump()
//    {
//        if (GUILoggerHome.find().isDebugOn())
//        {
//            GUILoggerHome.find().debug("SBTLiveNode--------->", GUILoggerMMBusinessProperty.MONITOR);
//            
//            GUILoggerHome.find().debug("    Name: " + nodeName, GUILoggerMMBusinessProperty.MONITOR);
//            GUILoggerHome.find().debug("    Type: " + nodeType, GUILoggerMMBusinessProperty.MONITOR);
//            GUILoggerHome.find().debug("    OrbName: " + orbName, GUILoggerMMBusinessProperty.MONITOR);
//            GUILoggerHome.find().debug("    RealOrbName: " + realOrbName, GUILoggerMMBusinessProperty.MONITOR);
//            GUILoggerHome.find().debug("    DisplayName: " + this.getDisplayName(), GUILoggerMMBusinessProperty.MONITOR);
//            GUILoggerHome.find().debug("    HostName: " + hostName, GUILoggerMMBusinessProperty.MONITOR);
//            GUILoggerHome.find().debug("    Port: " + portNum, GUILoggerMMBusinessProperty.MONITOR);
//            GUILoggerHome.find().debug("    UserOwner: " + userOwner, GUILoggerMMBusinessProperty.MONITOR);
//            GUILoggerHome.find().debug("    ServerName: " + this.getServerName(), GUILoggerMMBusinessProperty.MONITOR);
//            GUILoggerHome.find().debug("    PoaName: " + poaName, GUILoggerMMBusinessProperty.MONITOR);
//            GUILoggerHome.find().debug("    ClientInfo: " + clientInfo, GUILoggerMMBusinessProperty.MONITOR);
//            
//            if (orbNameAlias != null)
//            {
//                GUILoggerHome.find().debug("    orbNameAlias----------> Yes", GUILoggerMMBusinessProperty.MONITOR);
//                GUILoggerHome.find().debug("            OrbName " + orbNameAlias.getOrbName(), GUILoggerMMBusinessProperty.MONITOR);
//                GUILoggerHome.find().debug("            DisplayName " + orbNameAlias.getDisplayName(), GUILoggerMMBusinessProperty.MONITOR);
//                GUILoggerHome.find().debug("            Cluster " + orbNameAlias.getCluster(), GUILoggerMMBusinessProperty.MONITOR);
//                GUILoggerHome.find().debug("            SubCluster " + orbNameAlias.getSubCluster(), GUILoggerMMBusinessProperty.MONITOR);
//                GUILoggerHome.find().debug("            ClusterSubClusterName " + orbNameAlias.getClusterSubClusterName(), GUILoggerMMBusinessProperty.MONITOR);
//            }
//            else
//            {
//                GUILoggerHome.find().debug("    orbNameAlias----------> No", GUILoggerMMBusinessProperty.MONITOR);
//            }
//        }
//    }

} // end SBTLiveNode
