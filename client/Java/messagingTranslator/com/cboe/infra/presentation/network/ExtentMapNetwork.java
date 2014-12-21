package com.cboe.infra.presentation.network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerMMBusinessProperty;

public class ExtentMapNetwork extends Network
{


    private class SynchronizerThread extends Thread
	{
        boolean completedSynchronizing = false;

        public boolean hasCompletedSynchronizing()
        {
            return completedSynchronizing;
        }

 		public void run() {

 	        emProxy.waitForInitialization();

            synchronized (networkNodes)
            {
                networkNodes.clear(); // just in case
            }
            String[] extentNodes = emProxy.getNodeNames();
            Collection temp = new ArrayList();
            for( int k = 0; k < extentNodes.length; ++k )
            {
                SBTExtentNode xNode = new SBTExtentNode(extentNodes[k]);

                if (GUILoggerHome.find().isDebugOn())
                {
                    GUILoggerHome.find().debug("Adding node " + xNode + " to ExtentMapNetwork",GUILoggerMMBusinessProperty.EXTENT_MAP);
                }
 
                temp.add(xNode);
            }
            if (GUILoggerHome.find().isDebugOn())
            {
                GUILoggerHome.find().debug("ExtentMapNetwork: synchronizer has indicated that ExtentMapProxy has retrieved all of its data",GUILoggerMMBusinessProperty.EXTENT_MAP);
            }
 
			synchronized (ExtentMapNetwork.this)
			{
                completedSynchronizing = true;
                setData( temp );
                ExtentMapNetwork.this.notifyAll();
			}
		}
	};

    ChannelLinkConstructor clc = null;
    Collection channelLinks = null;
    ExtentMapProxy emProxy = ExtentMapProxy.getInstance();
    SynchronizerThread synchronizer = new SynchronizerThread();

    public synchronized Collection getNetworkElements()
    {
	    if (! synchronizer.hasCompletedSynchronizing() )
	    {
            if (GUILoggerHome.find().isDebugOn())
            {
                GUILoggerHome.find().debug("ExtentMapNetwork.sendsTO(String) - waiting",GUILoggerMMBusinessProperty.EXTENT_MAP);
            }
 
		    try { wait(); } catch (InterruptedException ignore) {}
	    }

		return super.getNetworkElements();
    }


    public synchronized SBTNode[] sendsTo( String subjectName )
    {
	    if (! synchronizer.hasCompletedSynchronizing() )
	    {
            if (GUILoggerHome.find().isDebugOn())
            {
                GUILoggerHome.find().debug("ExtentMapNetwork.sendsTO(String) - waiting",GUILoggerMMBusinessProperty.EXTENT_MAP);
            }
		    try { wait(); } catch (InterruptedException ignore) {}
	    }

        Collection participating = new HashSet();
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug("ExtentMapNetwork::sendsTo - Network contains " + networkNodes.size() + " nodes.",GUILoggerMMBusinessProperty.EXTENT_MAP);
        }
 
        Iterator iter = getNetworkElements().iterator();
        while( iter.hasNext() )
        {
            SBTNode node = (SBTNode)iter.next();
            if( node.listensOnSubject( subjectName ) )
            {
                participating.add( node );
            }
        }
        return (SBTNode[])participating.toArray( new SBTExtentNode[0] );
    }


    protected ExtentMapNetwork()
    {
        synchronizer.setName("ExtentMapSynchronizer");
    }

    public void initNetworkElements()
    {
        
        synchronizer.start();
    }
} // end RealTimeNetwork
