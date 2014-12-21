package com.cboe.infrastructureServices.systemsManagementService;

import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.foundationFramework.FrameworkComponentImpl;
import com.cboe.infrastructureServices.interfaces.adminService.POA_Admin_tie;
import com.cboe.infrastructureServices.interfaces.adminService.AdminPackage.InvalidPropertyName;
import com.cboe.infrastructureServices.loggingService.MsgCategory;
import com.cboe.infrastructureServices.loggingService.MsgPriority;
import com.cboe.infrastructureServices.traderService.TraderService;
import com.cboe.ORBInfra.PortableServer.POA_i;

/**
 * Abstract implementation of the IDL _AdminOperations.
 * Acts the holder of the AdminService singleton instance.
 * 
 * @version 3.3
 */
public abstract class AdminServiceBaseImpl extends FrameworkComponentImpl implements AdminService
{
	private static AdminService instance;
	/** The default implmentation */
	protected static String serviceImplClassName = "com.cboe.infrastructureServices.systemsManagementService.AdminServiceImpl";
	private POA_Admin_tie adminImpl;
	/**
	 */
	public AdminServiceBaseImpl()
	{
		setSmaName(FoundationFramework.getInstance().getName());
	}
	/**
	 * Null implementation
	 */
	public void defineProperties(com.cboe.infrastructureServices.interfaces.adminService.Property[] nproperties)
		throws com.cboe.infrastructureServices.interfaces.adminService.AdminPackage.MultipleExceptions 
	{
	}
	/**
	 * Null implementation
	 */
	public void defineProperty(com.cboe.infrastructureServices.interfaces.adminService.Property property)
		throws com.cboe.infrastructureServices.interfaces.adminService.AdminPackage.InvalidPropertyName, com.cboe.infrastructureServices.interfaces.adminService.AdminPackage.InvalidPropertyValue, com.cboe.infrastructureServices.interfaces.adminService.AdminPackage.UnsupportedProperty
	{
	}
	/**
	 * Null implementation
	 */
	public boolean executeCommand(com.cboe.infrastructureServices.interfaces.adminService.CommandHolder command) 
		throws com.cboe.infrastructureServices.interfaces.adminService.AdminPackage.InvalidParameter {
		return true;
	}
	/**
	 * Null implementation
	 */
	public com.cboe.infrastructureServices.interfaces.adminService.Command[] getAllCommands()
	{
		return new com.cboe.infrastructureServices.interfaces.adminService.Command[0];
	}
	/**
	 * Null implementation
	 */
	public com.cboe.infrastructureServices.interfaces.adminService.Command getCommand(String commandName)
		throws com.cboe.infrastructureServices.interfaces.adminService.AdminPackage.UnsupportedCommand
	{
		return null;
	}
	/**
	 */
	public static AdminService getInstance()
	{
		if (instance == null)
		{
			try
			{
				Class c = Class.forName(getServiceImplClassName());
				instance = (AdminService)c.newInstance();
			}
			catch (Exception e)
			{
				FoundationFramework.getInstance().getDefaultLogService().log(MsgPriority.high, MsgCategory.systemAlarm, "AdminService.getInstance", "Failed to create the admin service " + getServiceImplClassName() + '.', e);
			}
		}
		return instance;
	}
	//Must be implemented by actual AdminService implementations.
	public com.cboe.infrastructureServices.interfaces.adminService.Property[] getProperties()
	{
		// stub code
		return null;
	}
	/**
	 * Default implementation uses the System.properties
	 */
	public String getPropertyValue(String propertyName)
		throws com.cboe.infrastructureServices.interfaces.adminService.AdminPackage.PropertyNotFound, com.cboe.infrastructureServices.interfaces.adminService.AdminPackage.InvalidPropertyName 
	{
		// TBI
		String value = null;
		value = (String) System.getProperties().get( propertyName );
		if (value == null) 
		{
			throw new InvalidPropertyName(propertyName);
		}
		return value;
	}
	/**
	 * The name of a class that implements the AdminService interface and has a public default constructor.
	 */
	static String getServiceImplClassName()
	{
		return serviceImplClassName;
	}
	/**
	 * Export the admin service to the trader service. This allows the process to be configured
	 * via the CommandCallbackService.
	 *
	 * @param configService The source of property information for configuration of the admin service.
	 */
	public boolean initialize(ConfigurationService configService)
	{
		boolean retVal = false;
		try {
			String processName = FoundationFramework.getInstance().getName();
			processName = getProperty("adminServerName", processName);

			org.omg.CORBA.Object obj = FoundationFramework.getInstance().getOrbService().getOrb().resolve_initial_references("RootPOA");
			POA_i rootPOA = (POA_i)org.omg.PortableServer.POAHelper.narrow(obj);

			org.omg.CORBA.Policy[] policies = new org.omg.CORBA.Policy[3];
			policies[0] = rootPOA.create_lifespan_policy(org.omg.PortableServer.LifespanPolicyValue.PERSISTENT);
			policies[1] = rootPOA.create_id_assignment_policy(org.omg.PortableServer.IdAssignmentPolicyValue.USER_ID);
			policies[2] = rootPOA.create_thread_model_policy(com.cboe.ORBInfra.PortableServer.ThreadModelPolicyValue.THREAD_PER_REQUEST);

			org.omg.PortableServer.POA adminPoa = rootPOA.create_POA("AdminServicePOA", null, policies);

			adminImpl = new POA_Admin_tie(getInstance());
			adminPoa.activate_object_with_id( new String("AdminServiceObject").getBytes(), adminImpl );
			adminPoa.the_POAManager().activate();

			FoundationFramework.getInstance().getDefaultLogService().log(MsgPriority.low, MsgCategory.information, "AdminServiceBaseImpl.initialize", "Activated AdminService POA");

			String serviceTypeString = "IDL:adminService/Admin:1.2";
			TraderService traderSvc = FoundationFramework.getInstance().getTraderService();
			traderSvc.withdraw( serviceTypeString, "ProcessName==" + processName);

			java.util.Properties props = new java.util.Properties();
			props.put("routeName", "PCSAdminService");
			props.put("ProcessName", processName);

			// allow initialize to return true even if Trader Service export fails
			// but put out a high systemNotification log message
			String offerID = traderSvc.export(adminImpl, adminPoa, serviceTypeString, props);
			if ( offerID != null ) {
				FoundationFramework.getInstance().getDefaultLogService().log(MsgPriority.low, MsgCategory.information, "AdminServiceBaseImpl.initialize", "Exported AdminService reference, ProcessName = " + processName + ", Offer ID " + offerID);
			}
			else {
				FoundationFramework.getInstance().getDefaultLogService().log(MsgPriority.high, MsgCategory.systemNotification, "AdminService.initialize", "AdminService export to TraderService failed, offerID is NULL -- FF will continue, however.");
			}

			retVal = true; // all is good
		}
		catch(org.omg.CORBA.SystemException se) {
			FoundationFramework.getInstance().getDefaultLogService().log(MsgPriority.high, MsgCategory.systemAlarm, "AdminService.initialize","Failed to initialize the admin service, CORBA SystemException " + getClass().getName() + '.', se);
		}
		catch(Exception e) {
			FoundationFramework.getInstance().getDefaultLogService().log(MsgPriority.high, MsgCategory.systemAlarm, "AdminService.initialize","Failed to initialize the admin service, Exception " + getClass().getName() + '.', e);
		}

		return retVal;
	}

	/**
	 * The name of a class that implements the AdminService interface and has a public default constructor.
	 * @author Dave Hoag
	 * @param newValue java.lang.String
	 */
	public static void setServiceImplClassName(String newValue)
	{
		serviceImplClassName = newValue;
	}
}
