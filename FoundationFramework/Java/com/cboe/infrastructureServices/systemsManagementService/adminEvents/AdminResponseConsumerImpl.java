package com.cboe.infrastructureServices.systemsManagementService.adminEvents;

import com.cboe.infrastructureServices.interfaces.adminEvents.AdminRoutingStruct;
import com.cboe.infrastructureServices.interfaces.adminEvents.AdminExceptionStruct;
import com.cboe.infrastructureServices.interfaces.adminEvents.POA_AdminResponseEventConsumer;
import com.cboe.infrastructureServices.interfaces.adminService.Command;
import com.cboe.infrastructureServices.interfaces.adminService.Property;
import com.cboe.infrastructureServices.systemsManagementService.adminEvents.AdminResponseConsumer;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

/**
 * Created by IntelliJ IDEA.
 * User: Eric J. Fredericks
 * Date: Oct 2, 2003
 * Time: 5:12:07 PM
 * To change this template use Options | File Templates.
 */

public class AdminResponseConsumerImpl extends POA_AdminResponseEventConsumer implements AdminResponseConsumer
{
    private AdminResponseConsumer delegate;
    
    public AdminResponseConsumerImpl(AdminResponseConsumer delegate)
    {
        this.delegate = delegate;
    }
    
    public void catchException(AdminRoutingStruct adminRoutingStruct, AdminExceptionStruct adminExceptionStruct)
    {
        try
        {
            delegate.catchException(adminRoutingStruct, adminExceptionStruct);
        }
        catch(RuntimeException e)
        {
            Log.exception("AdminReqeustConsumerImpl intercepted uncaught RuntimeException", e);
        }
        
    }

    public void definePropertiesReturn(AdminRoutingStruct adminRoutingStruct)
    {
        try
        {
            delegate.definePropertiesReturn(adminRoutingStruct);
        }
        catch(RuntimeException e)
        {
            Log.exception("AdminReqeustConsumerImpl intercepted uncaught RuntimeException", e);
        }
    }

    public void definePropertyReturn(AdminRoutingStruct adminRoutingStruct)
    {
        try
        {
            delegate.definePropertyReturn(adminRoutingStruct);
        }
        catch(RuntimeException e)
        {
            Log.exception("AdminReqeustConsumerImpl intercepted uncaught RuntimeException", e);
        }
    }

    public void executeCommandReturn(AdminRoutingStruct adminRoutingStruct, boolean b, Command command)
    {
        try
        {
            delegate.executeCommandReturn(adminRoutingStruct, b, command);
        }
        catch(RuntimeException e)
        {
            Log.exception("AdminReqeustConsumerImpl intercepted uncaught RuntimeException", e);
        }
    }

    public void getAllCommandsReturn(AdminRoutingStruct adminRoutingStruct, Command[] commands)
    {
        try
        {
            delegate.getAllCommandsReturn(adminRoutingStruct, commands);
        }
        catch(RuntimeException e)
        {
            Log.exception("AdminReqeustConsumerImpl intercepted uncaught RuntimeException", e);
        }
    }

    public void getCommandReturn(AdminRoutingStruct adminRoutingStruct, Command command)
    {
        try
        {
            delegate.getCommandReturn(adminRoutingStruct, command);
        }
        catch(RuntimeException e)
        {
            Log.exception("AdminReqeustConsumerImpl intercepted uncaught RuntimeException", e);
        }
    }

    public void getPropertiesReturn(AdminRoutingStruct adminRoutingStruct, Property[] properties)
    {
        try
        {
            delegate.getPropertiesReturn(adminRoutingStruct, properties);
        }
        catch(RuntimeException e)
        {
            Log.exception("AdminReqeustConsumerImpl intercepted uncaught RuntimeException", e);
        }
    }

    public void getPropertyValueReturn(AdminRoutingStruct adminRoutingStruct, String s)
    {
        try
        {
            delegate.getPropertyValueReturn(adminRoutingStruct, s);
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
