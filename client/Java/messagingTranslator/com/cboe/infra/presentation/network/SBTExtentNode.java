package com.cboe.infra.presentation.network;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import com.cboe.EventService.ExtentMap.ExtentMapNodeChannelInfo;
import com.cboe.EventService.ExtentMap.ExtentProperties;
import com.cboe.EventService.ExtentMap.NodeChannelInfo;
import com.cboe.presentation.environment.EnvironmentManagerFactory;
import com.cboe.presentation.environment.EnvironmentProperties;

public class SBTExtentNode extends AbstractSBTNode
{
    protected Topic[] localPublishList = null;
    protected Topic[] localSubscribeList = null;
    protected Topic[] globalPublishList = null;
    protected Topic[] globalSubscribeList = null;
    protected HashMap nodeChannelInfo = new HashMap();
    protected String channelPrefix = System.getProperty( "EVENT_CHANNEL_PREFIX", "Prod" );

    public SBTExtentNode( String name )
    {
        super( name );
        ExtentMapNodeChannelInfo[] channels = null;
// long startChannels = - System.currentTimeMillis();
        if ( this.nodeType == SBTNodeType.DN_NODE )
        {
            channels = ExtentMapProxy.getInstance().retrieveDNInfo( nodeName );
        }
        else
        {
            channels = ExtentMapProxy.getInstance().retrieveNodeInfo( nodeName );
        }
        if( channels != null && channels.length > 0 )
        {
            for( int k = 0;k < channels.length;++k )
            {
                ExtentMapNodeChannelInfo ch = channels[k];
                //nodeChannelInfo.put( RegExStringUtil.strip( channelPrefix, ch.eventChannelID ), ch );
                nodeChannelInfo.put(ch.eventChannelID, ch);
            }
        }
    }

    public SBTExtentNode( String name, SBTNodeType type )
    {
        super( name, type );
        ExtentMapNodeChannelInfo[] channels = null;
        if ( nodeType == SBTNodeType.DN_NODE )
        {
            channels = ExtentMapProxy.getInstance().retrieveDNInfo( nodeName );
        }
        else
        {
            channels = ExtentMapProxy.getInstance().retrieveNodeInfo( nodeName );
        }
        if( channels != null && channels.length > 0 )
        {
            for( int k = 0;k < channels.length;++k )
            {
                //nodeChannelInfo.put( RegExStringUtil.strip( channelPrefix, channels[k].eventChannelID ), channels[k] );
                nodeChannelInfo.put(channels[k].eventChannelID, channels[k]);
            }
        }
    }



    public Topic[] getGlobalSubscribeList()
    {
        if( globalSubscribeList == null )
        {
            Iterator iter = nodeChannelInfo.keySet().iterator();
            ArrayList globals = new ArrayList();
            while( iter.hasNext() )
            {
                // for each channel:
                NodeChannelInfo ci = (NodeChannelInfo)nodeChannelInfo.get( iter.next() );
                int numberSubscribeExtents = ci.globalExtents.subExtents.length;
                if( numberSubscribeExtents > 0 )
                {
                    ExtentProperties[] globalSubs = ci.globalExtents.subExtents;
                    for( int j = 0;j < globalSubs.length;++j )
                    {
                        //globals.add( new Topic( RegExStringUtil.strip( channelPrefix, ci.eventChannelID ), globalSubs[j].extentName ) );
                        globals.add(new Topic(ci.eventChannelID, globalSubs[j].extentName, globalSubs[j].filterFlag ));
                    }
                }
            }
            globalSubscribeList = (Topic[])globals.toArray( new Topic[0] );
        }
        return globalSubscribeList;
    }

    public Topic[] getLocalPublishList()
    {
        if( localPublishList == null )
        {
            Iterator iter = nodeChannelInfo.keySet().iterator();
            java.util.ArrayList locals = new java.util.ArrayList();
            while( iter.hasNext() )
            {
                // for each channel:
                NodeChannelInfo ci = (NodeChannelInfo)nodeChannelInfo.get( iter.next() );
                int numberPublishExtents = ci.localExtents.pubExtents.length;
                if( numberPublishExtents > 0 )
                {
                    ExtentProperties[] localPubs = ci.localExtents.pubExtents;
                    for( int j = 0;j < localPubs.length;++j )
                    {
                        //Topic t = new Topic(RegExStringUtil.strip(channelPrefix, ci.eventChannelID), localPubs[j].extentName);
                        Topic t = new Topic( ci.eventChannelID , localPubs[j].extentName, localPubs[j].filterFlag );
                        locals.add( t );
                    }
                }
            }
            localPublishList = (Topic[])locals.toArray( new Topic[0] );
        }
        return localPublishList;
    }

    public NodeChannelInfo getChannelInfo( String channelName )
    {
        //NodeChannelInfo rv = (NodeChannelInfo)nodeChannelInfo.get( RegExStringUtil.strip( channelPrefix, channelName ) );
        NodeChannelInfo rv = ( NodeChannelInfo ) nodeChannelInfo.get(channelName);
        return rv;
    }

    /**
     * Often, the channelName that is passed in is from the monitoring service,
     * which can have a different prefix naming scheme than the extent map.
     * Therefore, we convert the monitoring service channel name to the extent
     * map channel name if necessary.
     * @param channelName
     * @return True if the extent map has publish or subscribe  entries for this node
     * on the provided channel.
     */
    public boolean belongsToChannel( String channelName )
    {
        EnvironmentProperties currentEnvironment = EnvironmentManagerFactory.find().getCurrentEnvironment();
        String monitorPrefixValue = currentEnvironment.getMonitorPrefix();
        String[] monitorPrefixes = monitorPrefixValue.split(",");
        for( int idx = 0; idx < monitorPrefixes.length; idx++ )
        {
            String prefix = monitorPrefixes[idx];
            if ( channelName.startsWith(prefix) )
            {
                // replace the monitoring service channel prefix with the extent map version
                String extentMapPrefixValue = currentEnvironment.getExtentMapPrefix();
                String[] extentMapPrefixes = extentMapPrefixValue.split(",");
                channelName = extentMapPrefixes[idx] + channelName.substring( prefix.length() );
                break;
            }
        }
        // getChannelInfo will strip out the channel channelPrefix for us
        return getChannelInfo( channelName ) != null;
    }

    public Topic[] getLocalSubscribeList()
    {
        if( localSubscribeList == null )
        {
            Iterator iter = nodeChannelInfo.keySet().iterator();
            ArrayList locals = new ArrayList();
            while( iter.hasNext() )
            {
                // for each channel:
                NodeChannelInfo ci = (NodeChannelInfo)nodeChannelInfo.get( iter.next() );
                int numberSubscribeExtents = ci.localExtents.subExtents.length;
                if( numberSubscribeExtents > 0 )
                {
                    ExtentProperties[] localSubs = ci.localExtents.subExtents;
                    for( int j = 0;j < localSubs.length;++j )
                    {
                        //Topic t = new Topic( RegExStringUtil.strip( channelPrefix, ci.eventChannelID ), localSubs[j].extentName );
                        Topic t = new Topic(ci.eventChannelID, localSubs[j].extentName, localSubs[j].filterFlag);
                        locals.add( t );
                    }
                }
            }
            localSubscribeList = (Topic[])locals.toArray( new Topic[0] );
        }
        return localSubscribeList;
    }

    public Topic[] getGlobalPublishList()
    {
        if( globalPublishList == null )
        {
            Iterator iter = nodeChannelInfo.keySet().iterator();
            ArrayList globals = new ArrayList();
            while( iter.hasNext() )
            {
                // for each channel:
                NodeChannelInfo ci = (NodeChannelInfo)nodeChannelInfo.get( iter.next() );
                int numberPublishExtents = ci.globalExtents.pubExtents.length;
                if( numberPublishExtents > 0 )
                {
                    ExtentProperties[] globalPubs = ci.globalExtents.pubExtents;
                    for( int j = 0;j < globalPubs.length;++j )
                    {
                        //globals.add( new Topic( RegExStringUtil.strip( channelPrefix, ci.eventChannelID ), globalPubs[j].extentName ) );
                        globals.add(new Topic(ci.eventChannelID, globalPubs[j].extentName, globalPubs[j].filterFlag));
                    }
                }
            }
            globalPublishList = (Topic[])globals.toArray( new Topic[0] );
        }
        return globalPublishList;
    }


    public boolean listensOnSubject( String subjectName )
    {
        return false;
    }


    public Collection getChannelInfo()
    {
        return nodeChannelInfo.values();
    }

    protected SBTNodeType deriveTypeFromName( String name )
    {
        SBTNodeType rv = SBTNodeType.UNDEFINED_TYPE;
        if( name.indexOf( "CAS" ) != -1 )
        {
            // this is a CAS client
            rv = SBTNodeType.CAS_NODE;
        }
        else
        {
            if( name.endsWith( "DN" ) )
            {
                // this is a DN	client
                rv = SBTNodeType.DN_NODE;
            }
            else
            {
                if( name.endsWith( "Frontend" ) )
                {
                    // this is a FrontEnd client
                    rv = SBTNodeType.FE_NODE;
                }
                else
                {
                    // this is a GC client **OR** a BC client
                    rv = SBTNodeType.OR_NODE;
                }
            }
        }
        return rv;
    }

    public boolean equals(Object o)
    {
        if (! (o instanceof SBTExtentNode) )
        {
            return false;
        }
        SBTExtentNode other = (SBTExtentNode) o;
        return (this.nodeName.equals(other.nodeName)) && (this.nodeType == other.nodeType);
    }
}