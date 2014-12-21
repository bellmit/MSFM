package com.cboe.infrastructureServices.orbService;

import com.cboe.infrastructureServices.systemsManagementService.ConfigurationService;
import org.omg.PortableServer.POAPackage.WrongPolicy;
/**
 * The basic interface needed to support SBT.
 * @version 2.2
 */
public interface OrbService
{
	public org.omg.CORBA.ORB getOrb();
	public boolean initialize(ConfigurationService configService) ;
	public void processRequest();
	public void connect(org.omg.CORBA.Object obj) throws org.omg.CORBA.SystemException;
	public void disconnect(org.omg.CORBA.Object obj) throws org.omg.CORBA.SystemException;
	public String object_to_string(org.omg.CORBA.Object obj) throws org.omg.CORBA.SystemException;
	public org.omg.CORBA.Object string_to_object(String obj) throws org.omg.CORBA.SystemException;
    /**
     * @param poaName String The name of a poa that is known to this system.
     * @param svt Servant The Servant to connect to the named poa.
     * @param objectId String If the Object reference is to be persistent, provide this parameter. Null is a valid value - requires transient references.
     */
	public org.omg.CORBA.Object connect(String poaName, org.omg.PortableServer.Servant svnt, String objectId) throws NoSuchPOAException, WrongPolicy;
	public org.omg.CORBA.Object connect(String poaName, org.omg.PortableServer.Servant svnt) throws NoSuchPOAException;

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
		org.omg.PortableServer.POAPackage.ServantNotActive;
	public static final String ROOT_POA = "RootPOA";
    /** The user in this case is the 'server' or 'services' that use the framework */
	public static final String USER_POA = "UserPOA";

    /** Called when becoming the master */
    public void goMaster();
	/** Return the poas defined to their initial state */
    public void goSlave();
	/**
	 * Use this method to obtain references to POA instances.
	 *
	 * @param poaName A name of a poa that was defined prior to this method call.
	 * @return The already known POA with the provided name.
	 * @exception NoSuchPOAException Thrown when no poa with the provided name could be found.
	 */
	public org.omg.PortableServer.POA getPOA(String poaName) throws NoSuchPOAException;
	public void disconnect(String poaName, org.omg.PortableServer.Servant svnt)throws NoSuchPOAException;
	public void shutdown();

	/**
	 * Use this method to recreate an unnarrowed stub object that is set for
	 * unordered delivery to the server.  This client-side policy affects
	 * the behvior of the transport only.
	 *
	 * @param obj The previously obtained CORBA object.
	 * @return The new CORBA object.
	 */
	public org.omg.CORBA.Object createUnorderedStub( org.omg.CORBA.Object obj );

	/**
	 * Use this method to recreate an unnarrowed stub object that is set for
	 * a SYNC_SCOPE policy for SYNC_WITH_SERVER.  This client-side policy
	 * affects the behvior of the transport only.
	 *
	 * @param obj The previously obtained CORBA object.
	 * @return The new CORBA object.
	 */
	public org.omg.CORBA.Object createSyncWithServerStub( org.omg.CORBA.Object obj );

	/**
	 * Use this method to recreate an unnarrowed stub object that is set for
	 * a SYNC_SCOPE policy for SYNC_WITH_TARGET.  This client-side policy
	 * affects the behvior of the transport only.
	 *
	 * @param obj The previously obtained CORBA object.
	 * @return The new CORBA object.
	 */
	public org.omg.CORBA.Object createSyncWithTargetStub( org.omg.CORBA.Object obj );
	/**
	 * Return the POA for the given channel from the channelToPOA map.
	 *
	 * @param channelName The channel name.
	 * @return The POA for the given channel.
	 */
	public org.omg.PortableServer.POA getChannelPOA( String channelName );
	
	/**
	 * This method is added to support MIOP channel
	 */
	public void setupMulticastChannelPOA(String multicastChannelName, org.omg.PortableServer.Servant servant);
}
