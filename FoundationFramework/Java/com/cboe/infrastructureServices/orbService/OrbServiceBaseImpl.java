package com.cboe.infrastructureServices.orbService;

import org.omg.PortableServer.POAPackage.WrongPolicy;

import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.foundationFramework.FrameworkComponentImpl;
import com.cboe.infrastructureServices.loggingService.MsgCategory;
import com.cboe.infrastructureServices.loggingService.MsgPriority;
import com.cboe.infrastructureServices.systemsManagementService.ConfigurationService;
/**
 * The abstract implementation of OrbService. Contains the singleton instance of the OrbService.
 * @version 3.1
 */
public abstract class OrbServiceBaseImpl extends FrameworkComponentImpl implements OrbService
{
	static String serviceImplClassName = "com.cboe.infrastructureServices.orbService.OrbServiceNullImpl";

	private static OrbService instance = null;
	org.omg.CORBA.ORB orb = null;

    /** Called when this server is becoming the master. The default impl is to do nothing. */
    public void goMaster(){}
    public void goSlave(){}
	/**
	 */
	OrbServiceBaseImpl()
	{
		setSmaName("ORB");
	}
	/**
	 * Get the singleton instance, create it if necessary.
	 */
	public static OrbService getInstance()
	{
		if (instance == null)
		{
			try
			{
				Class c = Class.forName(serviceImplClassName);
				instance = (OrbService)c.newInstance();
			}
			catch (Exception e)
			{
				FoundationFramework.getInstance().getDefaultLogService().log(MsgPriority.high, MsgCategory.systemAlarm, "OrbServiceBaseImpl.getInstance", "Failed to create the orb service.", e);
			}
		}
		return instance;
	}
	/**
	 * Avoid using this method. Try to use only the exposed interface.
	 */
	public org.omg.CORBA.ORB getOrb()
	{
		return orb;
	}
	/**
	 * The ServiceImplClassName must refer to a class that implements the OrbService interface.
	 * This class must have a public default constructor.
	 * @return String Fully qualified class name of an OrbService.
	 */
	public static String getServiceImplClassName()
	{
		return serviceImplClassName;
	}
	/**
	 * Nothing to configure in this abstract implementation
	 */
	public boolean initialize(ConfigurationService configService) { return true; }
	/**
	   @roseuid 3658E30D023F
	 */
	public void processRequest() { }
	/**
	 * @param str String Fully qualified class name of an OrbService. The class MUST implement the OrbService interface.
	 */
	public static void setServiceImplClassName(String str)
	{
		serviceImplClassName =  str;
	}
	/**
	 * Expose the connect method on the Orb. This should help eliminate calls to getOrb().
	 * @param obj The CORBA object to connect.
	 */
	public void connect(org.omg.CORBA.Object obj) throws org.omg.CORBA.SystemException
	{
		getOrb().connect(obj);
	}
    /**
     * legacy interface.
     */
	public org.omg.CORBA.Object connect(String poaName, org.omg.PortableServer.Servant svnt ) throws NoSuchPOAException
    {
        try
        {
            return connect( poaName, svnt, null);
        }
        catch( WrongPolicy wp)
        {
            FoundationFramework.getInstance().getDefaultLogService().log(MsgPriority.high, MsgCategory.systemAlarm, "OrbServiceBaseImpl.connect( String, Servant)", "Failed to connect to the orb service.", wp);
        }
        return null;
    }
	/**
	 * Expose the connect method on the Orb. This should help eliminate calls to getOrb().
	 * @param obj The CORBA object to connect.
	 */
	public org.omg.CORBA.Object connect(String poaName, org.omg.PortableServer.Servant svnt, String objectId) throws NoSuchPOAException, WrongPolicy
	{
		return null;
	}

	/**
	 * Support for event channels.
	 * Channel poa are not placed into the poaTable instance variable. They have their own HashMap of channel names to poa instances.
	 * @param srv org.omg.PortableServer.Servant
	 */
	public org.omg.CORBA.Object getChannelReference( org.omg.PortableServer.Servant srv, String channelName,
										  org.omg.CosNotification.Property[] qos)
		throws org.omg.CORBA.ORBPackage.InvalidName,
		org.omg.PortableServer.POAManagerPackage.AdapterInactive,
		org.omg.PortableServer.POAPackage.ServantAlreadyActive,
		org.omg.PortableServer.POAPackage.WrongPolicy,
		org.omg.PortableServer.POAPackage.InvalidPolicy,
		org.omg.PortableServer.POAPackage.AdapterAlreadyExists,
		org.omg.PortableServer.POAPackage.ServantNotActive
	{
		return null;
	}
	/**
	 * Expose the disconnect method on the Orb. This should help eliminate calls to getOrb().
	 * @param obj The CORBA object to disconnect.
	 */
	public void disconnect(org.omg.CORBA.Object obj) throws org.omg.CORBA.SystemException
	{
		getOrb().disconnect(obj);
	}
	/**
	 * Expose the object_to_string method on the Orb. This should help eliminate calls to getOrb().
	 * @param obj The CORBA object to Stringify.
	 * @return String Stringified IOR
	 */
	public String object_to_string(org.omg.CORBA.Object obj) throws org.omg.CORBA.SystemException
	{
		return getOrb().object_to_string(obj);
	}
	/**
	 * Expose the string_to_object method on the Orb. This should help eliminate calls to getOrb().
	 * @param iorString A stringified IOR
	 * @return CORBA object reference.
	 */
	public org.omg.CORBA.Object string_to_object(String iorString) throws org.omg.CORBA.SystemException
	{
		return getOrb().string_to_object(iorString);
	}
	/**
	 * Use this method to obtain references to POA instances.
	 *
	 * @exception NoSuchPOAException Thrown when no poa with the provided name could be found.
	 */
	public org.omg.PortableServer.POA getPOA(String poaName) throws NoSuchPOAException
	{
		return null;
    }
	/**
	 *
	 * @param obj The Servant to disconnect.
	 */
	public void disconnect(String poaName, org.omg.PortableServer.Servant svnt)throws NoSuchPOAException
	{

	}

	/**
	 * Shutdown the orb.  Leave this to the specific orb service impls.
	 *
	 */
	public void shutdown() {}

	/**
	 * Use this method to recreate an unnarrowed stub object that is set for
	 * unordered delivery to the server.  This client-side policy affects
	 * the behvior of the transport only.
	 *
	 * @param obj The previously obtained CORBA object.
	 * @return The new CORBA object.
	 */
	public org.omg.CORBA.Object createUnorderedStub( org.omg.CORBA.Object obj ) {
		return null;
	}

	/**
	 * Use this method to recreate an unnarrowed stub object that is set for
	 * a SYNC_SCOPE policy for SYNC_WITH_SERVER.  This client-side policy
	 * affects the behvior of the transport only.
	 *
	 * @param obj The previously obtained CORBA object.
	 * @return The new CORBA object.
	 */
	public org.omg.CORBA.Object createSyncWithServerStub( org.omg.CORBA.Object obj ) {
		return null;
	}

	/**
	 * Use this method to recreate an unnarrowed stub object that is set for
	 * a SYNC_SCOPE policy for SYNC_WITH_TARGET.  This client-side policy
	 * affects the behvior of the transport only.
	 *
	 * @param obj The previously obtained CORBA object.
	 * @return The new CORBA object.
	 */
	public org.omg.CORBA.Object createSyncWithTargetStub( org.omg.CORBA.Object obj ) {
		return null;
	}
	/**
	 * Return the POA for the given channel from the channelToPOA map.
	 *
	 * @param channelName The channel name.
	 * @return The POA for the given channel.
	 */
	public org.omg.PortableServer.POA getChannelPOA( String channelName ) {
		return null;
	}
}
