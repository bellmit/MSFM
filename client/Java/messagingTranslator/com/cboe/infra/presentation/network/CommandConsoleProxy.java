//
// -----------------------------------------------------------------------------------
// Source file: CommandConsoleProxy.java
//
// PACKAGE: com.cboe.infra.presentation.network
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.infra.presentation.network;

import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerMMBusinessProperty;

import com.cboe.ORBInfra.IOPImpl.IORImpl;
import com.cboe.ORBInfra.IOPImpl.ProfileNotPresent;
import com.cboe.ORBInfra.TIOP.TIOPProfileImpl;
import com.cboe.CommandConsole.CommandConsole;
import com.cboe.CommandConsole.CommandConsoleHelper;
import com.cboe.LocalTransport.LocalProfile;

/**
 * 
 */
public class CommandConsoleProxy
{
    private static CommandConsoleProxy instance = new CommandConsoleProxy();
    private com.cboe.ORBInfra.ORB.Orb orb;
    private IORImpl ior;

    public static CommandConsoleProxy getInstance()
    {
        return instance;
    }

    public synchronized CommandConsole getCommandConsole(String orbName, String hostName, short portNum)
    	throws ServiceNotAvailableException
    {
        CommandConsole rv = null;
        if (ior != null )
        {
            try
            {
                TIOPProfileImpl tProfile = ( TIOPProfileImpl ) ior.getProfile(TIOPProfileImpl.tag);
                tProfile.setHost(hostName);
                tProfile.setPort(portNum);
                tProfile.setUniqueName(orbName);
                rv = CommandConsoleHelper.narrow(orb.string_to_object(ior.stringify()));
            }
            catch (ProfileNotPresent ex)
            {
                GUILoggerHome.find().exception("CommandConsoleProxy.getCommandConsole(...) - failed to obtain TIOPProfile : " + ex.getMessage(),ex );
                throw new ServiceNotAvailableException("No command console object bound for " + orbName +"@"+hostName+":"+portNum);
            }
            catch (Throwable t)
            {
                GUILoggerHome.find().exception("CommandConsoleProxy.getCommandConsole(...) - failed to obtain TIOPProfile : " + t.getMessage(),t);
                throw new ServiceNotAvailableException("No command console object bound for " + orbName + "@" + hostName + ":" + portNum);
            }
        }
        return rv;
    }

    private CommandConsoleProxy()
    {
        try
        {
            orb = (com.cboe.ORBInfra.ORB.Orb) orb.init( new String[0], null);
            org.omg.CORBA.Object cmdConsole  = orb.resolve_initial_references("CommandConsole");
            ior = (( com.cboe.ORBInfra.ORB.DelegateImpl ) (( org.omg.CORBA.portable.ObjectImpl ) cmdConsole)._get_delegate()).getIOR().copy();
            ior.removeProfile(LocalProfile.tag);
            if (GUILoggerHome.find().isDebugOn())
            {
                GUILoggerHome.find().debug("CommandConsoleProxy.<INIT> - success!",GUILoggerMMBusinessProperty.MESSAGEMON);
            }
 
        }
        catch (Throwable t)
        {
            GUILoggerHome.find().exception("CommandConsoleProxy.<INIT> failed: " + t.getMessage(),t );
        }
    }

}

