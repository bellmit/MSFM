package com.cboe.infrastructureServices.systemsManagementService;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.interfaces.adminService.Admin;
import com.cboe.infrastructureServices.interfaces.adminService.AdminHelper;
import com.cboe.infrastructureServices.interfaces.adminService.Command;
import com.cboe.infrastructureServices.interfaces.adminService.CommandHolder;
import com.cboe.infrastructureServices.loggingService.MsgCategory;
import com.cboe.infrastructureServices.loggingService.MsgPriority;
import com.cboe.infrastructureServices.traderService.DirectoryQueryResult;
import com.cboe.infrastructureServices.traderService.TraderServiceBaseImpl;

/**
 * Implementation of the IDL _AdminOperations.
 * This will mask the CORBA nature of the AdminService.
 * Clients will use this implementation and the CORBA call will take place for you.
 * @author Dave Hoag
 * @version 3.2 Changed for increment 4 Infrastructure.
 */
public class AdminServiceClientImpl extends AdminServiceBaseImpl
{
	/**
	 * Define the SmaType that is expected to managed this resource.
	 * 
	 */
	public AdminServiceClientImpl()
	{
		setSmaType("GlobalAdminService.AdminServiceClientImpl");
	}
	Admin adminProxy = null;
	/**
	 * The name of the property in the config service to use to get the target Admin service.
	 */
	static final String TARGET_SERVER = "adminServiceServerName";
	/**
	 * Connect to the server side Admin service.
	 * This overrides the default implementation of registering the AdminService with the trader service.
	 * 
	 * @param configService The source of the properties required for configuration of the admin service.
	 * @return boolean indicating success or failure of configuration. //A carry over of the original implementation
	 */
	public boolean initialize(ConfigurationService configService)
	{
		String thisName = configService.getName();
		try
		{
			String serverName = getProperty(TARGET_SERVER, null );
System.out.println("Target SERVER " + serverName);

			if(serverName == null)
			{
				FoundationFramework.getInstance().getDefaultLogService().log(MsgPriority.medium, MsgCategory.information, "AdminService.initialize","No property for adminServiceServerName, using ServerName of " + thisName);
				serverName = thisName;
			}
			DirectoryQueryResult [] queryResult;
			//String serviceTypeString = AdminHelper.id();
			String serviceTypeString = "IDL:adminService/Admin:1.2"; //Changed due to existing entries in admin service
			queryResult = TraderServiceBaseImpl.getInstance().queryDirectory( serviceTypeString, "ProcessName=="+serverName);
			if ( queryResult.length == 0 )
			{
				FoundationFramework.getInstance().getDefaultLogService().log(MsgPriority.high, MsgCategory.systemAlarm,"AdminService.initialize", "Failed to locate the admin service in the TraderService." );
				return false;
			}
			adminProxy = AdminHelper.narrow(queryResult[0].getObjectReference());
		}
		catch (org.omg.CORBA.SystemException se)
		{
			FoundationFramework.getInstance().getDefaultLogService().log(MsgPriority.high, MsgCategory.systemAlarm, "AdminService.initialize","Failed to initialize the admin service " + getClass().getName() + '.', se);
			return false;
		}
		return true;
	}
	/**
	 * Delegate the request to the server side implementation.
	 * @see com.cboe.infrastructureServices.systemsManagementService.AdminServiceImpl
	 */
	public void defineProperties(com.cboe.infrastructureServices.interfaces.adminService.Property[] nproperties)
		throws com.cboe.infrastructureServices.interfaces.adminService.AdminPackage.MultipleExceptions
	{
			adminProxy.defineProperties(nproperties);
	}
	/**
	 * Delegate the request to the server side implementation.
	 * @see com.cboe.infrastructureServices.systemsManagementService.AdminServiceImpl
	 */
	public void defineProperty(com.cboe.infrastructureServices.interfaces.adminService.Property property)
		throws com.cboe.infrastructureServices.interfaces.adminService.AdminPackage.InvalidPropertyName, com.cboe.infrastructureServices.interfaces.adminService.AdminPackage.InvalidPropertyValue, com.cboe.infrastructureServices.interfaces.adminService.AdminPackage.UnsupportedProperty 
	{
		adminProxy.defineProperty(property);
	}
	/**
	 * Delegate the request to the server side implementation.
	 * @see com.cboe.infrastructureServices.systemsManagementService.AdminServiceImpl
	 */
	public boolean executeCommand(com.cboe.infrastructureServices.interfaces.adminService.CommandHolder command) 
		throws com.cboe.infrastructureServices.interfaces.adminService.AdminPackage.InvalidParameter
	{
		FoundationFramework.getInstance().getDefaultLogService().log(MsgPriority.medium, MsgCategory.debug , "AdminService.executeCommand","Sending command request: " + command.value.name);
		return adminProxy.executeCommand(command);
	}
	/**
	 * Delegate the request to the server side implementation.
	 * @see com.cboe.infrastructureServices.systemsManagementService.AdminServiceImpl
	 */
	public com.cboe.infrastructureServices.interfaces.adminService.Command[] getAllCommands()
	{
		return adminProxy.getAllCommands();
	}
	/**
	 * Delegate the request to the server side implementation.
	 * @see com.cboe.infrastructureServices.systemsManagementService.AdminServiceImpl
	 */
	public com.cboe.infrastructureServices.interfaces.adminService.Command getCommand(String commandName)
		throws com.cboe.infrastructureServices.interfaces.adminService.AdminPackage.UnsupportedCommand
	{
		return adminProxy.getCommand(commandName);
	}
	/**
	 * Delegate the request to the server side implementation.
	 * @see com.cboe.infrastructureServices.systemsManagementService.AdminServiceImpl
	 */
	public com.cboe.infrastructureServices.interfaces.adminService.Property[] getProperties()
	{
		// stub code
		return adminProxy.getProperties();
	}
	/**
	 * Delegate the request to the server side implementation.
	 * @see com.cboe.infrastructureServices.systemsManagementService.AdminServiceImpl
	 */
	public String getPropertyValue(String propertyName)
		throws com.cboe.infrastructureServices.interfaces.adminService.AdminPackage.PropertyNotFound, com.cboe.infrastructureServices.interfaces.adminService.AdminPackage.InvalidPropertyName
	{
		return adminProxy.getPropertyValue(propertyName);
	}

    public static class UnitTest extends TestCase
    {
        boolean verbose = System.getProperty("verbose", "false").equalsIgnoreCase("true") ;
	    
	    public UnitTest(String name) {
			super(name);
		}
	    
        public static void main(String [] args)
        {
	    	TestSuite suite = new TestSuite(UnitTest.class);
	        TestRunner.run(suite);
        }
        //Method invoked by the unit test in the AdminServiceImpl
        public void testClient() throws Exception
        {
            System.getProperties().put("ORB.PortNum","4443");
            System.getProperties().put("ORB.OrbName","adminClientTest");
            System.getProperties().put("ORB.InitRefURL","initrefs.ior");

            java.util.Properties props = new java.util.Properties();
            props.put("n2.adminServiceImpl", "com.cboe.infrastructureServices.systemsManagementService.AdminServiceClientImpl");
            props.put("n2.traderServiceImpl", "com.cboe.infrastructureServices.traderService.TraderServiceImpl");
            props.put("n2.adminServiceServerName", "n");
            props.put("n2.orbServiceImpl", "com.cboe.infrastructureServices.orbService.OrbServicePOAImpl");
            if(!verbose)
                props.put("n2.logServiceImpl", "com.cboe.infrastructureServices.loggingService.LogServiceNullImpl");
            props.put("n2.instrumentationServiceImpl", "com.cboe.infrastructureServices.instrumentationService.InstrumentationServiceNullImpl");
            if( ! verbose)
                props.put("test.logServiceImpl", "com.cboe.infrastructureServices.loggingService.LogServiceNullImpl");
            props.put("n2.isMaster", "true");
            props.put("n2.autoStart", "true");
            System.out.println("FF init");
            FoundationFramework.initializeForTest("n2", null, props);
            System.out.println("COMPLETE: FF init");
            try
            {
                System.out.println("Getting admin service");
                AdminService service = FoundationFramework.getInstance().getAdminService();
                System.out.println("Getting command from service " + service);
                Command command = service.getCommand("adminServiceClientTest");
                System.out.println("Sending command " + command);
                service.executeCommand(new CommandHolder(command));
            }
            catch(Throwable t)
            {
                System.out.println(t.toString());
                t.printStackTrace();
                System.exit(1); 
            }
            System.out.println("All done");
            System.exit(0);
        }
    }
}
