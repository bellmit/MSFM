package com.cboe.infra.presentation.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.omg.CORBA.ORB;
import org.omg.CORBA.SystemException;

import com.cboe.DistributedFilteringNode.DistributedNodePropertyDefinitions;
import com.cboe.EventService.Admin.AdminPropertyDefinitions;
import com.cboe.EventService.ExtentMap.ExtentMapAdminV6;
import com.cboe.EventService.ExtentMap.ExtentMapAdminV6Helper;
import com.cboe.EventService.ExtentMap.ExtentMapException;
import com.cboe.EventService.ExtentMap.ExtentMapNodeChannelInfo;
import com.cboe.common.log.Logger;
import com.cboe.idl.DistributedFilterNode.DistributedNodeAdmin;
import com.cboe.idl.DistributedFilterNode.DistributedNodeAdminHelper;
import com.cboe.idl.DistributedFilterNode.DistributedNodeQuery;
import com.cboe.idl.DistributedFilterNode.DistributedNodeQueryHelper;
import com.cboe.infrastructureUtility.CBOETradingBinder;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerMMBusinessProperty;

/**
 * ExtentMapProxy - This class encapsulates all access to the Extent Map Service
 * The ExtentMapProxy uses an ExtentMapAdmin  delegate to do all the work.
 */
public class ExtentMapProxy
{
    private static final ExtentMapProxy instance = new ExtentMapProxy();
    private ORB orb;
    private ExtentMapAdminV6 extentMapAdmin = null;

    private String[] csNodeNames = null;
    private String[] dnNodeNames = null;
    private String[] channelNames = null;
    private Map dnInfo = null;
    private Map nodeInfo = null;

    private Hashtable distNodeRefTable = new Hashtable();
    private String[] distNodesNames = null;

    private static final int UNINITIALIZED = 0;
    private static final int ORB_INITIALIZATION_IN_PROCESS = 1;
    private static final int ORB_INITIALIZED = 2;
    private static final int MAP_INITIALIZATION_IN_PROCESS = 3;
    private static final int INITIALIZED = 4;
    private int initializationState = UNINITIALIZED;

    private boolean shouldFetch = true;
    private final Object fetchLock = new Object();
    private final Object updateLock = new Object();

    public class MapFetcher extends Thread {
    	public void destroy() {
            setShouldFetch(false);
    	}

    	public void run() 
    	{
	  		synchronized (ExtentMapProxy.this ) 
	  		{
	  			if ( initializationState < MAP_INITIALIZATION_IN_PROCESS ) 
	  			{
	  				initializationState = MAP_INITIALIZATION_IN_PROCESS;
	        		update();
	        		initializationState = INITIALIZED;
                    if (GUILoggerHome.find().isDebugOn())
                    {
                        GUILoggerHome.find().debug("ExtentMapProxy.MapFetcher:  initialization COMPLETE.",GUILoggerMMBusinessProperty.EXTENT_MAP);
                    }
 
	        		ExtentMapProxy.this.notifyAll();
	        	} else 
	        	{
                    if (GUILoggerHome.find().isDebugOn())
                    {
                        GUILoggerHome.find().debug("ExtentMapProxy.MapFetcher: initializationState = " + initializationState ,GUILoggerMMBusinessProperty.EXTENT_MAP);
                    }
 
		        }
	        }
    	}

    	public void update() 
    	{
            if (GUILoggerHome.find().isDebugOn())
            {
                GUILoggerHome.find().debug("ExtentMapProxy::update()",GUILoggerMMBusinessProperty.EXTENT_MAP);
            }
 
               
	    	try {
                if(getShouldFetch())
                {
                    ArrayList list = null;
                    int len = 0;
                    synchronized(updateLock)
                    {
                        csNodeNames = extentMapAdmin.retrieveDNACSNodeNames();
                        len = csNodeNames.length;
                        list = new ArrayList(len);
                        for (int i = 0; i < len; i++)
                        {
                            list.add(csNodeNames[i]);
                        }
                    }

                    for (Iterator iterator = list.iterator(); iterator.hasNext();)
                    {
                        String nodeName = (String) iterator.next();
                        if(!getShouldFetch())
                        {
                            return;
                        }
                        else
                        {
                            // check if csNodeNames has been changed.
                            // this situation could happen if we get a shutdown while still
                            // initializing.
                            // in this case we just want to get out.
                            synchronized(updateLock)
                            {
                                if(len > csNodeNames.length)
                                {
                                    GUILoggerHome.find().alarm("Error getting ExtentMapNodeChannelInfo for Regular Node. ExtentMapNodeChannelInfo array length changed. [" + len +"] ["+ csNodeNames.length+"]");
                                    return;
                                }
                            }
                        }
                        if (GUILoggerHome.find().isDebugOn())
                        {
                            GUILoggerHome.find().debug("ExtentMapProxy.update() getChannelInfo "+nodeName ,GUILoggerMMBusinessProperty.EXTENT_MAP);
                        }
                        if(getShouldFetch())
                        {
                            long start = System.currentTimeMillis();
                            ExtentMapNodeChannelInfo[] info = null;
                            synchronized(updateLock)
                            {
                                if(extentMapAdmin != null)
                                {
                                    try
                                    {
                                        info = extentMapAdmin.retrieveNodeExtentMapInfo( nodeName );
                                    }
                                    catch (Exception e)
                                    {
                                        e.printStackTrace();
                                    }
                                }
                                else
                                {
                                    return;
                                }
                            }
                            long end = System.currentTimeMillis();
                            if (GUILoggerHome.find().isDebugOn())
                            {
                                GUILoggerHome.find().debug("Retrieved ExtentMapNodeChannelInfo for reg node " + nodeName + " in " + ( end - start) + " milliseconds.",GUILoggerMMBusinessProperty.EXTENT_MAP);
                            }

                            nodeInfo.put( nodeName, info );
                        }
                    }
                    if(!getShouldFetch())
                    {
                        return;
                    }
                    list = null;
                    len = 0;
                    synchronized(updateLock)
                    {
                        dnNodeNames = extentMapAdmin.retrieveDNNodeNames();
                        len = dnNodeNames .length;
                        list = new ArrayList(len);
                        for (int i = 0; i < len; i++)
                        {
                            list.add(dnNodeNames[i]);
                        }

                    }
                    for (Iterator iterator = list.iterator(); iterator.hasNext();)
                    {
                        String nodeName = (String) iterator.next();
                        if(!getShouldFetch())
                        {
                            return;
                        }
                        else
                        {
                            // check if dnNodeNames has been changed.
                            // this situation could happen if we get a shutdown while still
                            // initializing. 
                            // in this case we just want to get out.
                            synchronized(updateLock)
                            {
                                if(len > dnNodeNames.length)
                                {
                                    GUILoggerHome.find().alarm("Error getting ExtentMapNodeChannelInfo for DN Node. ExtentMapNodeChannelInfo array length changed. ["+len+"] ["+dnNodeNames.length+"]");
                                    return;
                                }
                            }
                        }
                        if (GUILoggerHome.find().isDebugOn())
                        {
                            GUILoggerHome.find().debug("ExtentMapProxy.update() getDNInfo"+nodeName ,GUILoggerMMBusinessProperty.EXTENT_MAP);
                        }
                        if(getShouldFetch())
                        {
                            long start = System.currentTimeMillis();
                            ExtentMapNodeChannelInfo[] info = null;
                            synchronized(updateLock)
                            {
                                if(extentMapAdmin != null)
                                {
                                    info = extentMapAdmin.retrieveDNExtentMapInfo( nodeName);
                                }
                                else
                                {
                                    return;
                                }
                            }
                            long end = System.currentTimeMillis();
                            if (GUILoggerHome.find().isDebugOn())
                            {
                                GUILoggerHome.find().debug("Retrieved ExtentMapNodeChannelInfo for dn node " + nodeName + " in " + ( end - start) + " milliseconds.",GUILoggerMMBusinessProperty.EXTENT_MAP);
                            }
                            dnInfo.put( nodeName, info );
                        }
                    }
                }
			} catch (ExtentMapException eme) {
                GUILoggerHome.find().exception("ExtentMapProxy.update()",eme);
			}
	    } // end update

    }; // end MapFetcher
    Thread mapFetcher = null;

    /**
     * Singleton accessor method
     */
    public static ExtentMapProxy getInstance()
    {
        return instance;
    }

	public synchronized void waitForInitialization()
	{
		if ( initializationState != INITIALIZED )
		{
            if (GUILoggerHome.find().isDebugOn())
            {
                GUILoggerHome.find().debug("ExtentMapProxy.waitForInitialization: waiting",GUILoggerMMBusinessProperty.EXTENT_MAP);
            }
 
			doWait();
            if (GUILoggerHome.find().isDebugOn())
            {
                GUILoggerHome.find().debug("ExtentMapProxy.waitForInitialization: completed" ,GUILoggerMMBusinessProperty.EXTENT_MAP);
            }
 
		}
	}


    public String[] getChannelNames()
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug("ExtentMapProxy.getChannelNames()" ,GUILoggerMMBusinessProperty.EXTENT_MAP);
        }
 
        waitForInitialization();
        synchronized (this)
        {
            if ( channelNames.length == 0 )
            {
                List channelList = new ArrayList();
                try
                {
                    for( Iterator nodeChIter = nodeInfo.keySet().iterator(); nodeChIter.hasNext(); )
                    {
                        String nodeName = ( String ) nodeChIter.next();
                        ExtentMapNodeChannelInfo[] chInfo = ( ExtentMapNodeChannelInfo[] ) nodeInfo.get(nodeName);
                        for( int m = 0; m < chInfo.length; m++ )
                        {
                            ExtentMapNodeChannelInfo channelInfo = chInfo[m];
                            if( !channelList.contains(channelInfo.eventChannelID) )
                            {
                                channelList.add(channelInfo.eventChannelID);
                            }
                        }
                    }

                    for( Iterator dnChIter = dnInfo.keySet().iterator(); dnChIter.hasNext(); )
                    {
                        String dnName = ( String ) dnChIter.next();
                        ExtentMapNodeChannelInfo[] chInfo = ( ExtentMapNodeChannelInfo[] ) dnInfo.get(dnName);
                        for( int m = 0; m < chInfo.length; m++ )
                        {
                            ExtentMapNodeChannelInfo channelInfo = chInfo[m];
                            if( !channelList.contains(channelInfo.eventChannelID) )
                            {
                                channelList.add(channelInfo.eventChannelID);
                            }

                        }
                    }
                    channelNames = (String[]) channelList.toArray(new String[0]);
                    if (GUILoggerHome.find().isDebugOn())
                    {
                        GUILoggerHome.find().debug("ExtentMapProxy.getChanneNames() - retrieved " + channelNames.length + " channels." ,GUILoggerMMBusinessProperty.EXTENT_MAP);
                    }
 
                }
                catch( Exception e )
                {
                    GUILoggerHome.find().exception(" determining extent map channel names: " + e.getMessage(),e);
                }
            }
        }
        return channelNames;
    }
    /**
     * @return extentMapAdmin.getNodeNames();
     */
    public String[] getNodeNames()
    {
        String[] rv = new String[0];
        synchronized (this ) {
        	if ( initializationState != INITIALIZED ) {
        		doWait();
        	}

	        try
	        {
                synchronized(updateLock)
                {
                    String[] temp = new String[ csNodeNames.length + dnNodeNames.length ];
                    System.arraycopy(csNodeNames,0,temp,0,csNodeNames.length);
                    System.arraycopy(dnNodeNames,0,temp,csNodeNames.length,dnNodeNames.length);
                    rv = temp;
                }
	        } catch (Exception e)
	        {
		        GUILoggerHome.find().exception("Errror in getNodeNames() - " + e.getMessage(),e );
	        }

		}
        return rv;
    }

    /**
     * @return extentMapAdmin.getCSNodeNames();
     */
    public String[] getCSNodeNames()
    {
        String[] rv = new String[0];
    	synchronized ( this ) {
    		if ( initializationState != INITIALIZED ) {
    			doWait();
    		}
            synchronized(updateLock)
            {
    		    rv = csNodeNames;
            }
    	}
        return rv;
    }

    /**
     * This method blocks until the initial update is complete
     * @return extentMapAdmin.getDNNodeNames();
     */
    public String[] getDNNodeNames()
    {
        String[] rv = new String[0];
    	synchronized ( this ) {
    		if (  initializationState != INITIALIZED ) {
    			doWait();
    		}
            synchronized(updateLock)
            {
    		    rv = dnNodeNames;
            }
    	}
        return rv;
    }

    /**
     * If not initalized, initialize the orb {@link #initializeOrb() }, create and start a MapFetcher thread
     * If already initialized, do nothing.  It is the responsibility of shutdown() to reset initalized to false
     */
    public void connect() 
    {
		try 
		{
            if (GUILoggerHome.find().isDebugOn())
            {
                GUILoggerHome.find().debug("ExtentMapProxy: connect()",GUILoggerMMBusinessProperty.EXTENT_MAP);
            }
 
			if ( initializationState == UNINITIALIZED ) 
			{
				initializeOrb();
			}
            if (GUILoggerHome.find().isDebugOn())
            {
                GUILoggerHome.find().debug("ExtentMapProxy: orb initialized",GUILoggerMMBusinessProperty.EXTENT_MAP);
            }
 
			if ( initializationState == ORB_INITIALIZED ) 
			{
    			mapFetcher = new MapFetcher();
    			mapFetcher.start();	// will eventually set initialized = true when done
                if (GUILoggerHome.find().isDebugOn())
                {
                    GUILoggerHome.find().debug("ExtentMapProxy: mapFetcher started",GUILoggerMMBusinessProperty.EXTENT_MAP);
                }
 
    		}
		} catch (IllegalStateException ise ) 
		{
			GUILoggerHome.find().exception("ExtentMapProxy::connect() - initialization failed: " + ise.getMessage(),ise );
		}

    }

    /**
     *
     */
    public void shutdown()
    {
        try
        {
        	if ( mapFetcher != null ) {
                if (GUILoggerHome.find().isDebugOn())
                {
                    GUILoggerHome.find().debug("ExtentMapProxy.shutdown() mapFetcher.destroy" ,GUILoggerMMBusinessProperty.EXTENT_MAP);
                }
        		mapFetcher.destroy();
        	}
            initializeInternalStructures();
            synchronized(updateLock)
            {
                if (GUILoggerHome.find().isDebugOn())
                {
                    GUILoggerHome.find().debug("ExtentMapProxy.shutdown() orb shutting down " ,GUILoggerMMBusinessProperty.EXTENT_MAP);
                }

                if ( orb != null )
                {
                    orb.shutdown( false );
                }
                orb = null;
                extentMapAdmin = null;
                if (GUILoggerHome.find().isDebugOn())
                {
                    GUILoggerHome.find().debug("ExtentMapProxy.shutdown() orb is shutdown" ,GUILoggerMMBusinessProperty.EXTENT_MAP);
                }
            }
            initializationState = UNINITIALIZED;
        }
        catch( Exception e )
        {
            GUILoggerHome.find().exception("Error shutting down ExtentMapProxy",e);
        }
    }

    /**
     * @return extentMapAdmin.retrieveDNInfo();
     */
    public ExtentMapNodeChannelInfo[] retrieveDNInfo( String distributionNodeName )
    {
        ExtentMapNodeChannelInfo[] rv = new ExtentMapNodeChannelInfo[0];
    	synchronized ( this ) {
    		if ( initializationState != INITIALIZED) {
    			doWait();
    		}
            synchronized(updateLock)
            {
                if ( dnInfo.containsKey( distributionNodeName ) ) {
                    rv = (ExtentMapNodeChannelInfo[]) dnInfo.get( distributionNodeName );
                }
            }
    	}
        return rv;
    }

    /**
     * extentMapAdmin.retrieveNodeInfo()
     */
    public ExtentMapNodeChannelInfo[] retrieveNodeInfo( String nodeName )
    {
        ExtentMapNodeChannelInfo[] rv = new ExtentMapNodeChannelInfo[0];
    	synchronized ( this ) {
    		if (  initializationState != INITIALIZED ) {
				doWait();
    		}
            synchronized(updateLock)
            {
                if ( nodeInfo.containsKey( nodeName ) ) {
                    rv = (ExtentMapNodeChannelInfo[]) nodeInfo.get( nodeName );
                }
            }
    	}
        return rv;
    }

    /**
     * Wait until initialization is complete (MapFetcher calls notifyAll() when its work is done).
     */
    private void doWait() 
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug("Not initialized! Waiting...",GUILoggerMMBusinessProperty.EXTENT_MAP);
        }
		try 
		{ 
		    wait(); 
		} catch (InterruptedException ie) 
		{
		    GUILoggerHome.find().exception(ie);
		}

    }


    /**
     * Initialize orb and bind to the ExtentMapAdmin object, which is our delegate
     */
    private void initializeOrb() throws IllegalStateException {

    	if (  initializationState != UNINITIALIZED) {
    		return;
    	}

    	initializationState = ORB_INITIALIZATION_IN_PROCESS;
        String emServiceName = null;
        synchronized(updateLock)
        {
            extentMapAdmin = null;
            try
            {
                orb = com.cboe.ORBInfra.ORB.Orb.init( new String[0], null );
                CBOETradingBinder binder = new CBOETradingBinder( orb );
                emServiceName = System.getProperty("EventService.Admin.ExtentMapServerName", AdminPropertyDefinitions.EXTENT_MAP_V6_ADMIN );

                org.omg.CORBA.Object[] ops = binder.resolveFromString( emServiceName );
                if( ops.length > 0 )
                {
                    org.omg.CORBA.Object obj = ops[0];
                    extentMapAdmin = ExtentMapAdminV6Helper.narrow( obj );
                }
                else
                {
                    orb.shutdown( false );
                    throw new IllegalStateException( "No IORs found for " + emServiceName + " in the Trader."  );
                }
            }
            catch( org.omg.CosTrading.UnknownServiceType ust )
            {
                GUILoggerHome.find().exception("Could not bind to ExtentMapAdminHelp: " + ust.getMessage(),ust );
                orb.shutdown( false );
                throw new IllegalStateException("An exception occurred: " + ust.getMessage() );
            }
            catch( org.omg.CosTrading.IllegalServiceType ist )
            {
                GUILoggerHome.find().exception("Could not bind to ExtentMapAdminHelp: " + ist.getMessage(),ist );
                orb.shutdown( false );
                throw new IllegalStateException("Unable to initialize ORB: " + ist.getMessage() );
            }
            catch( org.omg.CosTrading.IllegalPropertyName ipn )
            {
                GUILoggerHome.find().exception("Could not bind to ExtentMapAdminHelp: " + ipn.getMessage(),ipn );
                orb.shutdown( false );
                throw new IllegalStateException("Unable to initialize ORB: " + ipn.getMessage() );
            }
            catch( org.omg.CORBA.UserException ue )
            {
                GUILoggerHome.find().exception("Could not bind to ExtentMapAdminHelp: " + ue.getMessage(),ue );
                orb.shutdown( false );
                throw new IllegalStateException("Unable to initialize ORB: " + ue.getMessage() );
            }
            catch( SystemException ex )
            {
                GUILoggerHome.find().exception("Could not bind to ExtentMapAdminHelp: " + ex.getMessage(),ex );
                orb.shutdown( false );
                throw new IllegalStateException("No connection to AdminServer");

            }
        }
        initializationState = ORB_INITIALIZED;
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug("Connected to " + emServiceName,GUILoggerMMBusinessProperty.EXTENT_MAP);
        }

   }

	/**
	 * Private to enforce Singleton
	 */
    private ExtentMapProxy()
    {
        initializeInternalStructures();
        setShouldFetch(true);
    }

    private void setShouldFetch(boolean shouldFetch)
    {
        synchronized (fetchLock)
        {
            this.shouldFetch = shouldFetch;
        }
    }

    private boolean getShouldFetch()
    {
        synchronized (fetchLock)
        {
            return this.shouldFetch;
        }
    }

    private void initializeInternalStructures()
    {
        synchronized(updateLock)
        {
            csNodeNames = new String[0];
            dnNodeNames = new String[0];
            channelNames = new String[0];
            dnInfo = new HashMap();
            nodeInfo = new HashMap();
        }
    }

    /**
     * Method initializeDistributionNodeRefs.
     * 
     * added to support DNConsoleGUI (jwalton, 04/05/05)
     */
    public void initializeDistributionNodeRefs()
            throws DistributedNodeUtilException
    {
        String subCompName = this.getClass().getName() 
            + "ExtentMapProxyinitializeDistributionNodeRefs() - ";
        final String EXTENTMAP_SERVER_NAME = AdminPropertyDefinitions.EXTENT_MAP_V6_ADMIN;
        try
        {
            CBOETradingBinder tradingBinder = new CBOETradingBinder(this.orb);

            // Resolve the ExtentMapAdmin Server Reference
            /*Logger.sysNotify(subCompName
                    + "Resolving and Narrowing the ExtentMapAdmin Server <"
                    + EXTENTMAP_SERVER_NAME + "> Reference");
            org.omg.CORBA.Object[] extentMapSvcOffers = tradingBinder
                    .resolveFromString(EXTENTMAP_SERVER_NAME);

            ExtentMapAdminV6 extentMapAdminV6 = null;
            if (extentMapSvcOffers.length > 0)
            {
                org.omg.CORBA.Object obj = extentMapSvcOffers[0];
                extentMapAdminV6 = ExtentMapAdminV6Helper.narrow(obj);
            } else
            {
                throw new DistributedNodeUtilException(subCompName
                        + "No IORs found for <" + EXTENTMAP_SERVER_NAME
                        + "> in the Trader. Check the exports and Try Again!");
            }*/

            String constraint = "";
            String preference = "dummyPref";
            Hashtable policySet = new Hashtable();
            policySet.put("dummyPolicyKey", "dummyPolicyValue");
            String[] properties = new String[]
            { "default_props" };

            //Retrieving DN Query Offers
            org.omg.CosTrading.Offer[] queryOffers = new org.omg.CosTrading.Offer[0];
            queryOffers = tradingBinder.getOffers(
                    DistributedNodePropertyDefinitions.DN_QUERY_SVCTYPE_NAME,
                    constraint, preference, policySet, properties, 0); // 0 -->
                                                                       // All
                                                                       // Query
                                                                       // Offers
            Logger.sysNotify(subCompName + "<" + queryOffers.length
                    + "> DN Query Offers were returned.");

            if (queryOffers.length == 0)
            {
                throw new DistributedNodeUtilException(subCompName
                        + "Zero Query Offers returned from Trader. "
                        + "Check the exports and Try Again!");
            }

            for (int i = 0; i < queryOffers.length; i++)
            {
                //getting the value for the processname.
                // processname property is currently the only property as part
                // of the
                // DN Query Offer to the Trader.

                String dnName = queryOffers[i].properties[0].value
                        .extract_string();
                DistributedNodeQuery dnQueryRef = DistributedNodeQueryHelper
                        .narrow(queryOffers[i].reference);
                DistributedNodeHolder dnHolder = new DistributedNodeHolder(
                        dnName, dnQueryRef, extentMapAdmin);
                if (dnHolder.isActive())
                {
                    distNodeRefTable.put(dnName, dnHolder);
                }

            }

            //Retrieving DN Admin Offers
            org.omg.CosTrading.Offer[] adminOffers = new org.omg.CosTrading.Offer[0];
            adminOffers = tradingBinder.getOffers(
                    DistributedNodePropertyDefinitions.DN_ADMIN_SVCTYPE_NAME,
                    constraint, preference, policySet, properties, 0); // 0 -->
                                                                       // All
                                                                       // Query
                                                                       // Offers

            Logger.sysNotify(subCompName + "<" + adminOffers.length
                    + "> DN Admin Offers were returned.");

            if (adminOffers.length == 0)
            {
                throw new DistributedNodeUtilException(
                        subCompName
                                + "Zero Admin Offers returned from Trader. Check the exports and Try Again!");
            }

            for (int i = 0; i < adminOffers.length; i++)
            {
                //getting the value for the processname.
                // processname property is currently the only property as part
                // of the
                // DN Admin Offer to the Trader.

                String dnName = adminOffers[i].properties[0].value
                        .extract_string();
                DistributedNodeAdmin dnAdminRef = DistributedNodeAdminHelper
                        .narrow(adminOffers[i].reference);
                DistributedNodeHolder dnHolder = (DistributedNodeHolder) distNodeRefTable
                        .get(dnName);
                if (dnHolder != null) // If the Node was set to active while
                                      // Initializing the Query Reference.
                {
                    dnHolder.setAdminRef(dnAdminRef);
                }
            }

            distNodesNames = (String[]) distNodeRefTable.keySet().toArray(
                    new String[0]);

        } catch (Exception e)
        {
            e.printStackTrace();
            DistributedNodeUtilException dnUtilXp = new DistributedNodeUtilException(
                    subCompName
                            + "Exception while retrieving Query and Admin References for the DNs");
            dnUtilXp.initCause(e);
            throw dnUtilXp;
        }
    }

    /**
     * Method getDistrtibutionNode.
     * 
     * @return DistributedNodeHolder
     */
    public DistributedNodeHolder getDistributionNode(String nodeName)
    {
        DistributedNodeHolder distNodeHolder = (DistributedNodeHolder) distNodeRefTable
                .get(nodeName);
        return distNodeHolder;
    }

}