package com.cboe.floorApplication.floorSession;

import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.interfaces.expressApplication.MarketQueryV4Home;
import com.cboe.interfaces.floorApplication.FloorSessionManager;
import com.cboe.interfaces.floorApplication.ManualReportingServiceHome;
import com.cboe.interfaces.floorApplication.ManualReportingService;
import com.cboe.interfaces.floorApplication.NBBOService;
import com.cboe.interfaces.floorApplication.MarketQueryV5;
import com.cboe.interfaces.application.MarketQueryHome;
import com.cboe.interfaces.application.OrderManagementService;
import com.cboe.interfaces.application.OrderManagementServiceHome;
import com.cboe.interfaces.application.ParOrderManagementServiceHome;
import com.cboe.interfaces.application.UserSessionLogoutCollector;
import com.cboe.interfaces.application.SessionManager;
import com.cboe.interfaces.businessServices.OrderHandlingService;
import com.cboe.interfaces.application.ActivityServiceHome;

import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.AuthorizationException;

import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

import com.cboe.application.shared.consumer.OrderRoutingProcessor;
import com.cboe.application.shared.consumer.UserSessionLogoutProcessor;
import com.cboe.application.shared.consumer.UserSessionLogoutProcessorFactory;
import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.shared.POANameHelper;
import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.application.supplier.OrderRoutingSupplier;

import com.cboe.util.ExceptionBuilder;
import com.cboe.util.event.EventChannelAdapterFactory;

import com.cboe.domain.logout.LogoutServiceFactory;
import com.cboe.domain.util.ReflectiveStructBuilder;

import com.cboe.idl.floorApplication.FloorSessionManagerOperations;
import com.cboe.idl.floorApplication.LastSaleServiceHelper;
import com.cboe.idl.floorApplication.ManualReportingServiceHelper;
import com.cboe.idl.floorApplication.MarketQueryV5Helper;
import com.cboe.idl.floorApplication.NBBOServiceHelper;
import com.cboe.idl.floorApplication.ProductQueryV2Helper;
import com.cboe.idl.omt.OrderManagementServiceHelper;
import com.cboe.idl.par.ParOrderManagementServiceHelper;
import com.cboe.idl.activity.ActivityServiceHelper;

import com.cboe.idl.user.SessionProfileUserStructV2;
import com.cboe.delegates.application.OrderManagementServiceDelegate;
import com.cboe.delegates.application.ParOrderManagementServiceDelegate;
import com.cboe.delegates.application.ActivityServiceDelegate;
import com.cboe.delegates.floorApplication.LastSaleServiceDelegate;
import com.cboe.delegates.floorApplication.ManualReportingServiceDelegate;
import com.cboe.delegates.floorApplication.MarketQueryV5Delegate;
import com.cboe.delegates.floorApplication.NBBOServiceDelegate;
import com.cboe.interfaces.floorApplication.LastSaleService;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Collection;
import java.util.Iterator;

import org.omg.CORBA.Object;

/**
 * User: mahoney
 * Date: Jul 17, 2007
 */
public class FloorSessionManagerImpl extends BObject implements FloorSessionManager, UserSessionLogoutCollector
{
    private SessionManager sessionManager;
    private UserSessionLogoutProcessor logoutProcessor;
    protected com.cboe.idl.floorApplication.FloorSessionManager floorSessionManager;
    
    // Services
    private HashMap<String, org.omg.CORBA.Object> services;
    protected com.cboe.idl.omt.OrderManagementService omtService;
    protected com.cboe.idl.par.ParOrderManagementService parService;
    protected com.cboe.idl.activity.ActivityService activityService;
    
    protected OrderHandlingService orderHandlingService;
    private OrderRoutingSupplier orderRoutingSupplier = null;
    private OrderRoutingProcessor orderRoutingProcessor = null;

    public FloorSessionManagerImpl()
    {
        super();
        
        
    }

    public synchronized void initialize() throws Exception
    {
        services = new HashMap<String, org.omg.CORBA.Object>();

        // Initialize services so they are ready when needed
        initManualReportingService();
        initLastSaleService();
        initNBBOService();
        initProductQueryV2();
        initMarkeQueryV5();
        omtService = initOrderManagementService();
        parService = initParOrderManagementService();
        activityService = initActivityService();
        
        
    }

    public void setSessionManager(SessionManager theSession)
    {
        sessionManager = theSession;
        logoutProcessor = UserSessionLogoutProcessorFactory.create(this);
        EventChannelAdapterFactory.find().addChannelListener(this, logoutProcessor, sessionManager);
        LogoutServiceFactory.find().addLogoutListener(sessionManager, this);
    }

    public void acceptUserSessionLogout()
    {
        String PAR_ACRONYM = "PARWS";
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling acceptUserSessionLogout for " + sessionManager);
        }
     // PAR workstation will need extra cleanup.
        try
        {
            SessionProfileUserStructV2 sessionProfileUser = ServicesHelper.getUserService().getSessionProfileUserInformationV2(sessionManager.getUserId());
            if(sessionProfileUser.userInfo.userAcronym.acronym == PAR_ACRONYM)
            {
                
                orderHandlingService = null;
                orderRoutingSupplier.removeListenerGroup(this);
                orderRoutingSupplier = null;
                orderRoutingProcessor = null;
            }
        }
        catch(Exception e)
        {
            Log.exception(this, e);
        }
        unregisterRemoteObjects();
        unregisterParRemoteObjects();
        // Do any individual service clean up needed for logout
        EventChannelAdapterFactory.find().removeListenerGroup(this);
        EventChannelAdapterFactory.find().removeChannel(this);
        LogoutServiceFactory.find().logoutComplete(sessionManager, this);

        unregisterFloorSessionManager();
        logoutProcessor.setParent(null);
        logoutProcessor = null;
        sessionManager = null;
    }

    protected void unregisterRemoteObjects()
    {
        Collection<org.omg.CORBA.Object> theServices = services.values();
        Iterator<org.omg.CORBA.Object> it = theServices.iterator();
        while(it.hasNext())
        {
            try
            {
                org.omg.CORBA.Object remoteObject = it.next();
                if(remoteObject != null)
                {
                    RemoteConnectionFactory.find().unregister_object(remoteObject);
                }
            }
            catch(Exception e)
            {
                Log.exception(this, e);
            }
            if (Log.isDebugOn())
            {
                Log.debug(this, "calling unregisterRemoteObjects");
            }
        }
        services.clear();
        
    }
    protected void unregisterParRemoteObjects()
    {
        //unregisterPARService()
        
        try
        {
            if (parService != null) {
                RemoteConnectionFactory.find().unregister_object(parService);
                parService = null;
            }
        } catch( Exception e ) {
            Log.exception( this, e );
        }
        
        //unregisterOMTService()
        
        try
        {
            if (omtService != null) {
                RemoteConnectionFactory.find().unregister_object(omtService);
                omtService = null;
            }
        } catch( Exception e ) {
            Log.exception( this, e );
        }
        
        
        try
        {
            if (activityService != null) {
                RemoteConnectionFactory.find().unregister_object(activityService);
                activityService = null;
            }
        } catch( Exception e ) {
            Log.exception( this, e );
        }
        
    }

    public org.omg.CORBA.Object getService(String serviceId)
            throws com.cboe.exceptions.SystemException, CommunicationException, AuthorizationException, NotFoundException
    {
        org.omg.CORBA.Object service = services.get(serviceId);
        if(null == service)
        {
            if(ManualReportingServiceHelper.id().equals(serviceId))
            {
                service = getManualReportingService();
            } 
            else if(LastSaleServiceHelper.id().equals(serviceId)) 
            {
            	service = getLastSaleService();
            }
            else if(NBBOServiceHelper.id().equals(serviceId))
            {
            	service = getNBBOService();
            }
            else if(ProductQueryV2Helper.id().equals(serviceId)) {
            	service = getProductQueryV2();
            }
            else if(MarketQueryV5Helper.id().equals(serviceId)) {
            	service = getMarketQueryV5();
            }
            
        }
        return service;
    }
    
    public com.cboe.idl.omt.OrderManagementService getOrderManagementService()
    throws SystemException, CommunicationException, AuthorizationException, NotFoundException
    {
        if(omtService == null)
        {
                omtService = initOrderManagementService();
        }
        return omtService;
    }
    
    public com.cboe.idl.par.ParOrderManagementService getParOrderManagementService()
    throws SystemException, CommunicationException, AuthorizationException, NotFoundException
    {
        if(parService == null)
        {
                parService = initParOrderManagementService();
        }
        return parService;
    }
    
    public com.cboe.idl.activity.ActivityService getActivityService()
    throws SystemException, CommunicationException, AuthorizationException, NotFoundException
    {
        if(activityService == null)
        {
            activityService = initActivityService();
        }
        return activityService;
    }
    

    /**
     * Initializes the ManualReportingService and adds it to the services map.
     */
    private org.omg.CORBA.Object initManualReportingService()
            throws SystemException, CommunicationException, AuthorizationException
    {
        ManualReportingServiceHome home = ServicesHelper.getManualReportingServiceHome();
        String poaName = POANameHelper.getPOAName((BOHome) home);
        ManualReportingService service = home.create(sessionManager);

        ManualReportingServiceDelegate delegate = new ManualReportingServiceDelegate(service);
        org.omg.CORBA.Object obj = (org.omg.CORBA.Object) RemoteConnectionFactory.find().register_object(delegate, poaName);
        //manualReportingCorba = ManualReportingServiceHelper.narrow(obj);

        String key = ManualReportingServiceHelper.id();
        services.put(key, obj);
        return obj;
    }
    
    private org.omg.CORBA.Object initLastSaleService()
    	throws SystemException, CommunicationException, AuthorizationException
    {
    	MarketQueryHome home = ServicesHelper.getMarketQueryHome();
        String poaName = POANameHelper.getPOAName((BOHome) home);
        // MarketQueryHome has two interfaces -
        //     MarketQueryV3 createMarketQuery(SessionManager sessionManager)
        // and LastSaleService createLargeTradeLastSale(SessionManager sessionManager)
        // MarketQueryV3 instance should be already created before LastSaleService
        LastSaleService lastSale = home.createLargeTradeLastSale(sessionManager);
        LastSaleServiceDelegate delegate = new LastSaleServiceDelegate(lastSale);
        org.omg.CORBA.Object obj = (org.omg.CORBA.Object) 
        	RemoteConnectionFactory.find().register_object(delegate, poaName);
        
        // keyed on Helper.id() 
        String key = LastSaleServiceHelper.id();
        services.put(key, obj);
        return obj;
    }
    
    private org.omg.CORBA.Object initNBBOService()
		throws SystemException, CommunicationException, AuthorizationException
	{
		MarketQueryV4Home home = ServicesHelper.getMarketQueryV4Home();
	    String poaName = POANameHelper.getPOAName((BOHome) home);
	    NBBOService nbbo = home.createNBBOService(sessionManager);
	    NBBOServiceDelegate delegate = new NBBOServiceDelegate(nbbo);
	    org.omg.CORBA.Object obj = (org.omg.CORBA.Object) 
	    	RemoteConnectionFactory.find().register_object(delegate, poaName);
	    
	    // keyed on Helper.id() 
	    String key = NBBOServiceHelper.id();
	    services.put(key, obj);
	    return obj;
	}

    private org.omg.CORBA.Object initProductQueryV2() 
    	throws SystemException, CommunicationException, AuthorizationException {
    	// keyed on Helper.id() 
	    String key = ProductQueryV2Helper.id();
	    org.omg.CORBA.Object obj = (org.omg.CORBA.Object) sessionManager.getProductQuery();
	    services.put(key, obj);
	    return obj;
	}
    
    private org.omg.CORBA.Object initMarkeQueryV5()
		throws SystemException, CommunicationException, AuthorizationException
	{
		MarketQueryV4Home home = ServicesHelper.getMarketQueryV4Home();
	    String poaName = POANameHelper.getPOAName((BOHome) home);
	    MarketQueryV5  marketQuery = home.createMarketQueryV5(sessionManager);
	    MarketQueryV5Delegate delegate = new MarketQueryV5Delegate(marketQuery);
	    org.omg.CORBA.Object obj = (org.omg.CORBA.Object) 
	    	RemoteConnectionFactory.find().register_object(delegate, poaName);
	    
	    // keyed on Helper.id() 
	    String key = MarketQueryV5Helper.id();
	    services.put(key, obj);
	    return obj;
	}
    
    protected com.cboe.idl.omt.OrderManagementService initOrderManagementService()
    throws SystemException, CommunicationException, AuthorizationException
    {
        OrderManagementServiceHome home = ServicesHelper.getOrderManagementServiceHome();
        String poaName = POANameHelper.getPOAName((BOHome) home);
        OrderManagementService omt = home.create(sessionManager);
        
        OrderManagementServiceDelegate delegate = new OrderManagementServiceDelegate(omt);
        org.omg.CORBA.Object obj = (org.omg.CORBA.Object) RemoteConnectionFactory.find().register_object(delegate, poaName);
        omtService = OrderManagementServiceHelper.narrow(obj);
        
        return omtService;
    }
    
    protected com.cboe.idl.par.ParOrderManagementService initParOrderManagementService()
    throws SystemException, CommunicationException, AuthorizationException
    {
        ParOrderManagementServiceHome home = ServicesHelper.getParOrderManagementServiceHome();
        String poaName = POANameHelper.getPOAName((BOHome) home);
        com.cboe.interfaces.application.ParOrderManagementService par = home.create(sessionManager);
        
        ParOrderManagementServiceDelegate delegate = new ParOrderManagementServiceDelegate(par);
        org.omg.CORBA.Object obj = (org.omg.CORBA.Object) RemoteConnectionFactory.find().register_object(delegate, poaName);
        parService = ParOrderManagementServiceHelper.narrow(obj);
        
        return parService;
    }
    
    protected com.cboe.idl.activity.ActivityService initActivityService()
    throws SystemException, CommunicationException, AuthorizationException
    {
        ActivityServiceHome home = ServicesHelper.getActivityServiceHome();
        String poaName = POANameHelper.getPOAName((BOHome) home);
        com.cboe.interfaces.application.ActivityService activity = home.create(sessionManager);
        
        ActivityServiceDelegate delegate = new ActivityServiceDelegate(activity);
        org.omg.CORBA.Object obj = (org.omg.CORBA.Object) RemoteConnectionFactory.find().register_object(delegate, poaName);
        activityService = ActivityServiceHelper.narrow(obj);
        
        return activityService;
    }
   

    private synchronized org.omg.CORBA.Object getManualReportingService()
            throws SystemException, CommunicationException, AuthorizationException
    {
        org.omg.CORBA.Object manualReportingCorba = services.get(ManualReportingServiceHelper.id());
        if(null == manualReportingCorba)
        {
            manualReportingCorba = initManualReportingService();
        }
        return manualReportingCorba;
    }
    
    private synchronized org.omg.CORBA.Object getLastSaleService()
    		throws SystemException, CommunicationException, AuthorizationException
    {
    	org.omg.CORBA.Object lastSaleCorba = services.get(LastSaleServiceHelper.id());
    	if(null == lastSaleCorba)
    	{
    		lastSaleCorba = initManualReportingService();
    	}
    	return lastSaleCorba;
    }
    
    private synchronized org.omg.CORBA.Object getNBBOService()
			throws SystemException, CommunicationException, AuthorizationException
	{
		org.omg.CORBA.Object nbboCorba = services.get(NBBOServiceHelper.id());
		if(null == nbboCorba)
		{
			nbboCorba = initNBBOService();
		}
		return nbboCorba;
	}
    
    private Object getProductQueryV2() 
    	throws SystemException, CommunicationException, AuthorizationException {
    	org.omg.CORBA.Object rptQueryCorba = services.get(ProductQueryV2Helper.id());
		if(null == rptQueryCorba)
		{
			rptQueryCorba = initProductQueryV2();
		}
		return rptQueryCorba;
	}
    
    private Object getMarketQueryV5() 
		throws SystemException, CommunicationException, AuthorizationException {
    	org.omg.CORBA.Object marketQueryCorba = services.get(MarketQueryV5Helper.id());
		if(null == marketQueryCorba)
		{
			marketQueryCorba = initMarkeQueryV5();
		}
		return marketQueryCorba;
    }
    
    
    
    
    public void setRemoteDelegate(com.cboe.idl.floorApplication.FloorSessionManager remoteDelegate)
		throws SystemException, CommunicationException, AuthorizationException
	{
    	floorSessionManager = remoteDelegate;
	}
    
    protected void unregisterFloorSessionManager()
    {
        try {
            if (floorSessionManager != null) {
                RemoteConnectionFactory.find().unregister_object(floorSessionManager);
                floorSessionManager = null;
            }
        } catch( Exception e ) {
            Log.exception( this, e );
        }
	}
    
   
   
    public BaseSessionManager getSessionManager()
    {
        return sessionManager;
       
    }


}
