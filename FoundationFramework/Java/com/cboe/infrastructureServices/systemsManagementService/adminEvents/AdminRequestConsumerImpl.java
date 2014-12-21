package com.cboe.infrastructureServices.systemsManagementService.adminEvents;

import com.cboe.infrastructureServices.interfaces.adminEvents.POA_AdminRequestEventConsumer;
import com.cboe.infrastructureServices.interfaces.adminEvents.AdminRoutingStruct;
import com.cboe.infrastructureServices.interfaces.adminService.Property;
import com.cboe.infrastructureServices.interfaces.adminService.Command;
import com.cboe.infrastructureServices.systemsManagementService.adminEvents.AdminRequestConsumer;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

/**
 * Created by IntelliJ IDEA.
 * User: Eric J. Fredericks
 * Date: Oct 2, 2003
 * Time: 9:24:48 AM
 * To change this template use Options | File Templates.
 */

public class AdminRequestConsumerImpl extends POA_AdminRequestEventConsumer implements AdminRequestConsumer 
{
    private AdminRequestConsumer delegate;
    
    public AdminRequestConsumerImpl(AdminRequestConsumer delegate)
    {
        this.delegate = delegate;
    }
    
    public void defineProperties(AdminRoutingStruct adminRoutingStruct, Property[] properties)
    {
        try
        {
            delegate.defineProperties(adminRoutingStruct, properties);
        }
        catch(RuntimeException e)
        {
            Log.exception("AdminReqeustConsumerImpl intercepted uncaught RuntimeException", e);
        }
    }

    public void defineProperty(AdminRoutingStruct adminRoutingStruct, Property property)
    {
        try
        {
            delegate.defineProperty(adminRoutingStruct, property);
        }
        catch(RuntimeException e)
        {
            Log.exception("AdminReqeustConsumerImpl intercepted uncaught RuntimeException", e);
        }
    }

    public void executeCommand(AdminRoutingStruct adminRoutingStruct, Command command)
    {
        try
        {
            delegate.executeCommand(adminRoutingStruct, command);
        }
        catch(RuntimeException e)
        {
            Log.exception("AdminReqeustConsumerImpl intercepted uncaught RuntimeException", e);
        }
    }

    public void getAllCommands(AdminRoutingStruct adminRoutingStruct)
    {
        try
        {
            delegate.getAllCommands(adminRoutingStruct);
        }
        catch(RuntimeException e)
        {
            Log.exception("AdminReqeustConsumerImpl intercepted uncaught RuntimeException", e);
        }
    }

    public void getCommand(AdminRoutingStruct adminRoutingStruct, String commandName)
    {
        try
        {
            delegate.getCommand(adminRoutingStruct, commandName);
        }
        catch(RuntimeException e)
        {
            Log.exception("AdminReqeustConsumerImpl intercepted uncaught RuntimeException", e);
        }
    }

    public void getProperties(AdminRoutingStruct adminRoutingStruct)
    {
        try
        {
            delegate.getProperties(adminRoutingStruct);
        }
        catch(RuntimeException e)
        {
            Log.exception("AdminReqeustConsumerImpl intercepted uncaught RuntimeException", e);
        }
    }

    public void getPropertyValue(AdminRoutingStruct adminRoutingStruct, String propertyName)
    {
        try
        {
            delegate.getPropertyValue(adminRoutingStruct, propertyName);
        }
        catch(RuntimeException e)
        {
            Log.exception("AdminReqeustConsumerImpl intercepted uncaught RuntimeException", e);
        }
    }

    public org.omg.CORBA.Object get_typed_consumer()
    {
        return null;
    }

    public void push(org.omg.CORBA.Any any) throws org.omg.CosEventComm.Disconnected
    {
    }

    public void disconnect_push_consumer()
    {
    }
}
