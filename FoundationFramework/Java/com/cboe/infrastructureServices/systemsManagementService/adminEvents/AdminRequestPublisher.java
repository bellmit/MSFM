package com.cboe.infrastructureServices.systemsManagementService.adminEvents;

import com.cboe.infrastructureServices.interfaces.adminEvents.AdminRoutingStruct;
import com.cboe.infrastructureServices.interfaces.adminEvents.AdminRequestEventConsumer;
import com.cboe.infrastructureServices.interfaces.adminService.Property;
import com.cboe.infrastructureServices.interfaces.adminService.Command;
import com.cboe.infrastructureServices.systemsManagementService.adminEvents.AdminRequestConsumer;

/**
 * Created by IntelliJ IDEA.
 * User: Eric J. Fredericks
 * Date: Sep 29, 2003
 * Time: 5:18:54 PM
 * To change this template use Options | File Templates.
 */

public class AdminRequestPublisher implements AdminRequestConsumer
{
    private AdminRequestEventConsumer eventChannel;
    private static final boolean WANT_NON_REPUD = ( System.getProperty("LogNonRepud") != null );

    public AdminRequestPublisher(AdminRequestEventConsumer channel)
    {
        eventChannel = channel;
    }
    
    public void defineProperties(AdminRoutingStruct adminRoutingStruct, Property[] properties)
    {
        eventChannel.defineProperties(adminRoutingStruct, properties);
    }

    public void defineProperty(AdminRoutingStruct adminRoutingStruct, Property property)
    {
        eventChannel.defineProperty(adminRoutingStruct, property);
    }

    public void executeCommand(AdminRoutingStruct adminRoutingStruct, Command command)
    {
		StringBuffer b = new StringBuffer();
		for (int i=0; i<command.args.length; i++) {
			b.append(command.args[i].name + ":" + command.args[i].type + ":" + command.args[i].value + command.args[i].description + "\t");
		}

		if ( WANT_NON_REPUD ) {
			com.cboe.securityService.SecurityLogger.getInstance().logNonRepudiationMessage( "ASync_AR non-repudiation message",
				adminRoutingStruct.requestDestination,
				com.cboe.infrastructureServices.interfaces.adminService.AdminHelper.id(),
				command.name,
				command.description,
				b.toString() );
		}

        eventChannel.executeCommand(adminRoutingStruct, command);
    }

    public void getAllCommands(AdminRoutingStruct adminRoutingStruct)
    {
        eventChannel.getAllCommands(adminRoutingStruct);
    }

    public void getCommand(AdminRoutingStruct adminRoutingStruct, String s)
    {
        eventChannel.getCommand(adminRoutingStruct, s);
    }

    public void getProperties(AdminRoutingStruct adminRoutingStruct)
    {
        eventChannel.getProperties(adminRoutingStruct);
    }

    public void getPropertyValue(AdminRoutingStruct adminRoutingStruct, String s)
    {
        eventChannel.getPropertyValue(adminRoutingStruct, s);
    }
}
