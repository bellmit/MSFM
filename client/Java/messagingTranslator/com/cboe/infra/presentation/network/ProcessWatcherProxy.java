package com.cboe.infra.presentation.network;
import java.util.*;

import com.cboe.idl.processWatcher.ProcessWatcherRegistration;
import com.cboe.idl.processWatcher.ProcessWatcherRegistrationHelper;
import com.cboe.idl.processWatcher.WatchedProcessStruct;

import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerMMBusinessProperty;
import com.cboe.presentation.common.properties.AppPropertiesFileFactory;

import com.cboe.infrastructureUtility.*;

/**
 * This class serves as the proxy connection to the ProcessWatcher service;
 * albeit it implements only the subset of the ProxyWatcher API needed by our
 * application.
 * @deprecated 11/2003
 */
public class ProcessWatcherProxy
{
    private static ProcessWatcherRegistration pwRegistration = null;
    private static ProcessWatcherProxy instance = new ProcessWatcherProxy();
    private static final String SERVICE_NAME = System.getProperty( "ProcessWatcher.ServiceName", "ProcessWatcherRegistrar" );
    org.omg.CORBA.ORB orb = null;
    private WatchedProcessStruct[] pwProcesses = null;

    public static ProcessWatcherProxy getInstance()
    {
        return instance;
    }

    /**
     * Get the list of processes watched by the PW service
     * Included in this list is information like what machine
     * the process is running on and other useful information.
     * @see com.cboe.processWatcher.WatchedProcess
     * @return
     */
    public WatchedProcessStruct[] getWatchedProcesses()
    {
        return pwProcesses;
    }

    /**
     * Get the PW details for a specific process name.  The processName
     * is typically also the orb name.
     * @param name
     * @return
     */
    public WatchedProcessStruct getWatchedProcess(String name)
    {
        WatchedProcessStruct rv = null;
        for( int idx = 0; idx < pwProcesses.length; idx++ )
        {
            WatchedProcessStruct watchedProcess = pwProcesses[idx];
            if ( watchedProcess.processName.equals(name) )
            {
                rv = watchedProcess;
                if (GUILoggerHome.find().isDebugOn())
                {
                    GUILoggerHome.find().debug("ProcessWatcherProxy.getWatchedProcess(...) - found matching PW entry!",GUILoggerMMBusinessProperty.PROCESS_WATCHER);
                }
 
                break;
            }
            else
            {
                if (GUILoggerHome.find().isDebugOn())
                {
                    GUILoggerHome.find().debug("ProcessWatcherProxy.getWatchedProcess(...) - " + watchedProcess.processName + " does not equal " + name ,GUILoggerMMBusinessProperty.PROCESS_WATCHER);
                }
 
            }
        }
        return rv;
    }

    /**
     * This is a Singleton - don't let outsiders construct instances of this class
     */
    private ProcessWatcherProxy()
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug("Initializing ProcessWatcherProxy..." ,GUILoggerMMBusinessProperty.PROCESS_WATCHER);
        }
 
        org.omg.CORBA.ORB orb = com.cboe.ORBInfra.ORB.Orb.init( new String[0], null );
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug("Orb.init" ,GUILoggerMMBusinessProperty.PROCESS_WATCHER);
        }
 
        try
        {
            CBOETradingBinder binder = new CBOETradingBinder( orb );
            if (GUILoggerHome.find().isDebugOn())
            {
                GUILoggerHome.find().debug("Obtained CBOETradingBinder" ,GUILoggerMMBusinessProperty.PROCESS_WATCHER);
            }
            //org.omg.CORBA.Object[] ops = binder.resolveFromString( SERVICE_NAME );
            org.omg.CORBA.Object[] ops = binder.resolveFromString("ProcessWatcherRegistrar",new String(),new String(), new Hashtable(), new String[0],2);
            if (GUILoggerHome.find().isDebugOn())
            {
                GUILoggerHome.find().debug("Resolved service name " + SERVICE_NAME,GUILoggerMMBusinessProperty.PROCESS_WATCHER);
            }
            if( ops.length > 0 )
            {
                org.omg.CORBA.Object obj = ops[0];
                pwRegistration = ProcessWatcherRegistrationHelper.narrow( obj );
                if (GUILoggerHome.find().isDebugOn())
                {
                    GUILoggerHome.find().debug("Bound ProcessWatcherRegistration" ,GUILoggerMMBusinessProperty.PROCESS_WATCHER);
                }
            }
            else
            {
                GUILoggerHome.find().alarm("Could not bind to " + SERVICE_NAME + ".  Shutting down.");
                orb.shutdown( false );
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
        try
        {
            if (GUILoggerHome.find().isDebugOn())
            {
                GUILoggerHome.find().debug("Getting process list",GUILoggerMMBusinessProperty.PROCESS_WATCHER);
            }
            pwProcesses = pwRegistration.getProcessWatchList();
            if (GUILoggerHome.find().isDebugOn())
            {
                GUILoggerHome.find().debug("There are " + pwProcesses.length + " processes" ,GUILoggerMMBusinessProperty.PROCESS_WATCHER);
            }
            StringBuffer sb = new StringBuffer(200);
            sb.append("\n");
            for( int i = 0;i < pwProcesses.length;++i )
            {
               
                sb.append( "Process " + i + ":\n" );
                sb.append( "\t" + pwProcesses[i].processName );
                sb.append( "\t" + pwProcesses[i].orbName );
                sb.append( "\t" + pwProcesses[i].host );
                sb.append( "\t" + pwProcesses[i].port );
                sb.append( "\t" + pwProcesses[i].poaName + "\n" );
            }
            if (GUILoggerHome.find().isDebugOn())
            {
                GUILoggerHome.find().debug(sb.toString(),GUILoggerMMBusinessProperty.PROCESS_WATCHER);
            }
 
        }
        catch( Exception e )
        {
            e.printStackTrace();
            pwProcesses = new WatchedProcessStruct[0];
        //			logger.log("Shutting down ORB. Exiting...");
        //			((com.cboe.ORBInfra.ORB.Orb)orb).shutdown(true);
        //			System.exit(-1);
        }
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug("Complete.",GUILoggerMMBusinessProperty.PROCESS_WATCHER);
        }
    }

}
