package com.cboe.infrastructureServices.systemsManagementService.adminEvents;

import com.cboe.infrastructureServices.interfaces.adminEvents.AdminRoutingStruct;
import com.cboe.infrastructureServices.interfaces.adminEvents.AdminExceptionStruct;
import com.cboe.infrastructureServices.interfaces.adminEvents.AdminResponseEventConsumer;
import com.cboe.infrastructureServices.interfaces.adminService.Command;
import com.cboe.infrastructureServices.interfaces.adminService.Property;
import com.cboe.infrastructureServices.systemsManagementService.adminEvents.AdminResponseConsumer;

/**
 * Created by IntelliJ IDEA.
 * User: Eric J. Fredericks
 * Date: Sep 29, 2003
 * Time: 5:27:10 PM
 * To change this template use Options | File Templates.
 */

public class AdminResponsePublisher implements AdminResponseConsumer
{
    private AdminResponseEventConsumer eventChannel;
    
    public AdminResponsePublisher(AdminResponseEventConsumer channel)
    {
        eventChannel = channel;
    }
    
    public void catchException(AdminRoutingStruct adminRoutingStruct, AdminExceptionStruct adminExceptionStruct)
    {
        eventChannel.catchException(adminRoutingStruct, adminExceptionStruct);
    }

    public void definePropertiesReturn(AdminRoutingStruct adminRoutingStruct)
    {
        eventChannel.definePropertiesReturn(adminRoutingStruct);
    }

    public void definePropertyReturn(AdminRoutingStruct adminRoutingStruct)
    {
        eventChannel.definePropertyReturn(adminRoutingStruct);
    }
    
    public void executeCommandReturn(AdminRoutingStruct adminRoutingStruct, boolean result, Command command)
    {
        eventChannel.executeCommandReturn(adminRoutingStruct, result, command);
    }

    public void getAllCommandsReturn(AdminRoutingStruct adminRoutingStruct, Command[] commands)
    {
        eventChannel.getAllCommandsReturn(adminRoutingStruct, commands);
    }

    public void getCommandReturn(AdminRoutingStruct adminRoutingStruct, Command command)
    {
        eventChannel.getCommandReturn(adminRoutingStruct, command);
    }

    public void getPropertiesReturn(AdminRoutingStruct adminRoutingStruct, Property[] properties)
    {
        eventChannel.getPropertiesReturn(adminRoutingStruct, properties);
    }

    public void getPropertyValueReturn(AdminRoutingStruct adminRoutingStruct, String s)
    {
        eventChannel.getPropertyValueReturn(adminRoutingStruct, s);
    }
}
