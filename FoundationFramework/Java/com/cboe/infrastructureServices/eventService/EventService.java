package com.cboe.infrastructureServices.eventService;

import com.cboe.ORBInfra.IOPImpl.IORImpl;
import com.cboe.infrastructureServices.systemsManagementService.*;
import org.omg.PortableServer.Servant;
import java.util.*;
/**
 * The interface to the event service defines the method necessary for a pub sub
 * implementation.
 * 
 * @author Kevin Yaussy
 * @author Dave Hoag
 * @version 2.0
 */
public interface EventService
{
    
	/**	
	 * @deprecated 
	 * But still being use by 14 classes
	 * in /vobs/dte/domain/event/Java/impls/com/cboe/publishers/eventChannel/
	 */
	public org.omg.CORBA.Object getEventChannelSupplierStub( String ecName,
												  String repId );
	
	public void connectTypedEventChannelConsumer( String ecName,
										 String repId,
										 org.omg.PortableServer.Servant srv)
		throws org.omg.CORBA.ORBPackage.InvalidName,
		org.omg.CosNaming.NamingContextPackage.InvalidName, 
		org.omg.CosNaming.NamingContextPackage.CannotProceed,
		org.omg.CosNaming.NamingContextPackage.NotFound,
		org.omg.PortableServer.POAManagerPackage.AdapterInactive,
		org.omg.PortableServer.POAPackage.ServantAlreadyActive,
		org.omg.PortableServer.POAPackage.WrongPolicy,
		org.omg.PortableServer.POAPackage.ServantNotActive,
		org.omg.PortableServer.POAPackage.InvalidPolicy,
		org.omg.PortableServer.POAPackage.AdapterAlreadyExists,
		org.omg.CosEventChannelAdmin.AlreadyConnected,
		org.omg.CosEventChannelAdmin.TypeError,
		org.omg.CosTypedEventChannelAdmin.NoSuchImplementation;
	
	
	/**
	// Use this for subscribing to NotificationChannels only.
	// Filters can be added using the filter creation methods.
	 */
	public void connectTypedNotifyChannelConsumer( String ecName,
										  String repId,
										  org.omg.PortableServer.Servant srv )
		throws org.omg.CORBA.ORBPackage.InvalidName, 
		org.omg.CosNaming.NamingContextPackage.InvalidName, 
		org.omg.CosNaming.NamingContextPackage.CannotProceed,
		org.omg.CosNaming.NamingContextPackage.NotFound,
		org.omg.PortableServer.POAManagerPackage.AdapterInactive,
		org.omg.PortableServer.POAPackage.ServantAlreadyActive,
		org.omg.PortableServer.POAPackage.WrongPolicy,
		org.omg.PortableServer.POAPackage.ServantNotActive,
		org.omg.PortableServer.POAPackage.InvalidPolicy,
		org.omg.PortableServer.POAPackage.AdapterAlreadyExists,
		org.omg.CosEventChannelAdmin.AlreadyConnected,
		org.omg.CosEventChannelAdmin.TypeError,
		org.omg.CosTypedEventChannelAdmin.NoSuchImplementation,
		org.omg.CosNotifyFilter.InvalidConstraint,
		org.omg.CosNotifyFilter.InvalidGrammar,
		org.omg.CosNotifyChannelAdmin.AdminLimitExceeded;

	/**
	 * 
	 */
	public void resumeProxySupplier( String ecName, String repId, org.omg.PortableServer.Servant srv )
		throws org.omg.CORBA.ORBPackage.InvalidName, 
		org.omg.CosNaming.NamingContextPackage.InvalidName, 
		org.omg.CosNaming.NamingContextPackage.CannotProceed,
		org.omg.CosNaming.NamingContextPackage.NotFound,
		org.omg.PortableServer.POAManagerPackage.AdapterInactive,
		org.omg.PortableServer.POAPackage.ServantAlreadyActive,
		org.omg.PortableServer.POAPackage.WrongPolicy,
		org.omg.PortableServer.POAPackage.ServantNotActive,
		org.omg.PortableServer.POAPackage.InvalidPolicy,
		org.omg.PortableServer.POAPackage.AdapterAlreadyExists,
		org.omg.CosEventChannelAdmin.AlreadyConnected,
		org.omg.CosEventChannelAdmin.TypeError,
		org.omg.CosTypedEventChannelAdmin.NoSuchImplementation,
		org.omg.CosNotifyFilter.InvalidConstraint,
		org.omg.CosNotifyFilter.InvalidGrammar,
		org.omg.CosNotifyChannelAdmin.AdminLimitExceeded;
	/**
	 * 
	 */
	public void suspendProxySupplier( String ecName, String repId, org.omg.PortableServer.Servant srv )
		throws org.omg.CORBA.ORBPackage.InvalidName, 
		org.omg.CosNaming.NamingContextPackage.InvalidName, 
		org.omg.CosNaming.NamingContextPackage.CannotProceed,
		org.omg.CosNaming.NamingContextPackage.NotFound,
		org.omg.PortableServer.POAManagerPackage.AdapterInactive,
		org.omg.PortableServer.POAPackage.ServantAlreadyActive,
		org.omg.PortableServer.POAPackage.WrongPolicy,
		org.omg.PortableServer.POAPackage.ServantNotActive,
		org.omg.PortableServer.POAPackage.InvalidPolicy,
		org.omg.PortableServer.POAPackage.AdapterAlreadyExists,
		org.omg.CosEventChannelAdmin.AlreadyConnected,
		org.omg.CosEventChannelAdmin.TypeError,
		org.omg.CosTypedEventChannelAdmin.NoSuchImplementation,
		org.omg.CosNotifyFilter.InvalidConstraint,
		org.omg.CosNotifyFilter.InvalidGrammar,
		org.omg.CosNotifyChannelAdmin.AdminLimitExceeded;

	// Use this method to disconnect the consumer object from the channel.
	public void disconnectNotificationConsumer( org.omg.PortableServer.Servant srv );

	// Use this for publishing to EventChannels and NotificationChannels.
	// Only typed IDL defined methods may be pushed on the returned object.
	public org.omg.CORBA.Object getTypedEventChannelSupplierStub( 
													 String ecName,
													 String repId )
		throws org.omg.CORBA.ORBPackage.InvalidName,
		org.omg.CosNaming.NamingContextPackage.InvalidName, 
		org.omg.CosNaming.NamingContextPackage.CannotProceed,
		org.omg.CosNaming.NamingContextPackage.NotFound,
		org.omg.CosEventChannelAdmin.AlreadyConnected,
		org.omg.CosEventChannelAdmin.TypeError,
		org.omg.CosTypedEventChannelAdmin.NoSuchImplementation,
		org.omg.CosTypedEventChannelAdmin.InterfaceNotSupported;

	public boolean initialize(ConfigurationService configService) ;

    /**
       Creates a new Inclusion Filter.
       @roseuid 36FBB1C80271
     */
    public ConsumerFilter createNewInclusionFilter(Servant servant, String repId, String methodName, String constraintStr, String ecName)
		throws org.omg.CosNaming.NamingContextPackage.InvalidName,
		org.omg.CosNaming.NamingContextPackage.CannotProceed,
		org.omg.CosNaming.NamingContextPackage.NotFound,
		org.omg.CosTypedEventChannelAdmin.NoSuchImplementation,
		org.omg.CosNotifyChannelAdmin.AdminLimitExceeded;

    
    /**
       Returns an enumeration of all current Inclusion Filters.
       @roseuid 36FBB1F100AB
     */
    public Enumeration getAllInclusionFilters(Servant servant);
    
    /**
       Removes the given ConsumerFilter.
       @roseuid 36FBB245005D
     */
    public void removeFilter(ConsumerFilter filter);
    
    /**
       Removes the ConsumerFilters given in the Vector using the equals method.
       @roseuid 36FBB245005D
     */
    public void removeFilters(Vector filters);
    
    /**
       Removes all current Filters.
       @roseuid 36FBB25900CB
     */
    public void removeAllFilters(Servant servant);
    
    /**
       Applies the given filter to the ProxySupplier.
       @roseuid 36FBB26000AB
     */
    public void applyFilter(ConsumerFilter filter)
		throws org.omg.CosNotifyFilter.InvalidConstraint;

    /**
       Applies the filters given in the Vector to the ProxySupplier.
       @roseuid 36FBB26000AB
     */
    public void applyFilters(Vector filters)
		throws org.omg.CosNotifyFilter.InvalidConstraint;

    
    /**
       Creates a new Exclusion Filter.
       @roseuid 36FBB5600177
     */
    public ConsumerFilter createNewExclusionFilter(Servant servant, String repId, String methodName, String constraintStr, String ecName)
		throws org.omg.CosNaming.NamingContextPackage.InvalidName,
		org.omg.CosNaming.NamingContextPackage.CannotProceed,
		org.omg.CosNaming.NamingContextPackage.NotFound,
		org.omg.CosTypedEventChannelAdmin.NoSuchImplementation,
		org.omg.CosNotifyChannelAdmin.AdminLimitExceeded;

    /**
       Returns an enumeration of all current Exclusion Filters.
       @roseuid 36FBB64800CB
     */
    public Enumeration getAllExclusionFilters(Servant servant);
    
    /**
       Returns an enumeration of all filters.
       @roseuid 36FC02C700BB
     */
    public Enumeration getAllFilters(Servant servant);

	/**
	 * Creates a buffering area to hold events.
	 *
	 */
	public int createBuffer();

	/**
	 * Tells the event transport to hold the next event in the buffering
	 * area associated with bufferId.
	 */
	public void startBuffer( int bufferId );

	/**
	 * Tells the event transport to send all buffered events associated
	 * with bufferId.
	 */
	public void commitBuffer( int bufferId );

	/**
	 * Tells the event transport to throw away all buffered events associated
	 * with bufferId.
	 */
	public void abortBuffer( int bufferId );

	/**
	 * Does the initial, required, setup of the event service.
	 */
	public void setupEventService();

	/**
	 * Does the initial, required, startup of the event service.
	 */
	public void startupEventService();
	
	/**
	 * API to set the Flag which will control the message publishing
	 * Parameter: IORImpl ior: ior of the channel object
	 *            String channelName: name of the channel
	 * Return   : true  the flag is set to the requested channel
	 *            false the flag is not set to the requested channel 
	 *            because the channel is not existed or the wrong 
	 *            sub-class is called
	 */
	public boolean setPublishingFlag(IORImpl ior, boolean bFlag);
	public boolean setPublishingFlag(String sChannelName, boolean bFlag);
	
	/**
	 * The context to a GMD message so that the application can manually acknowledge 
	 * receipt.
	 * @return GMDMessageControl A class that will provide control to the user.
	 */
	public GMDMessageControl getGMDMessageControl();
	
	
	/**
	 * Return a CORBA stub object, which application can use to publish messages onto
	 * a multicast channel
	 */
	public org.omg.CORBA.Object getMulticastChannel(String multicastChannelName);
	
	/**
	 * Connect the consumer to a multicast channel
	 */
	public void connectMulticastConsumer(String multicastChannelName, org.omg.PortableServer.Servant consumer);

}
