package com.cboe.infrastructureServices.eventService;

import com.cboe.ORBInfra.IOPImpl.IORImpl;
import com.cboe.infrastructureServices.foundationFramework.FrameworkComponentImpl;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.FatalFoundationFrameworkException;
import com.cboe.infrastructureServices.systemsManagementService.ConfigurationService;

/**
 * 
 * @author Kevin Yaussy
 * @author Dave Hoag
 * @version 2.0
 */
public abstract class EventServiceBaseImpl extends FrameworkComponentImpl implements EventService{
   static String serviceImplClassName = "com.cboe.infrastructureServices.eventService.EventServiceImpl";
   private static EventService instance;
	/**
	 * The context to a GMD message so that the application can manually acknowledge 
	 * receipt.
	 * @return null An empty implementation.
	 */
	public GMDMessageControl getGMDMessageControl()
	{
		return null;
	}
	/**
	 * Empty implementation 
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
		org.omg.CosNotifyChannelAdmin.AdminLimitExceeded
	{
	}
	/**
	 *  Empty implementation
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
		org.omg.CosNotifyChannelAdmin.AdminLimitExceeded
	{
	}




public static EventService getInstance() {
    if (instance == null) {
        try {
            Class c = Class.forName(getServiceImplClassName());
                instance = (EventService)c.newInstance();
        } catch (Exception e ){ 
		    throw new FatalFoundationFrameworkException(e,"Failed to getInstance of EventService");
	}
    }
    return instance;
}

public boolean initialize( ConfigurationService configService) {
    return true;
}

public static String getServiceImplClassName() {
    return serviceImplClassName;
}

public static void setServiceImplClassName(String aName) {
     serviceImplClassName = aName;
}

	public int createBuffer() {
		return 0;
	}

	public void startBuffer( int bufferId ) {}

	public void commitBuffer( int bufferId ) {}

	public void abortBuffer( int bufferId ) {}

	public void setupEventService() {}
	public void startupEventService() {}
	
	public boolean setPublishingFlag(IORImpl ior, boolean bFlag) {return false;}
	public boolean setPublishingFlag(String sChannelName, boolean bFlag) {return false;}
	
}
