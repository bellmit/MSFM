package com.cboe.proxy.businessServicesClient;

import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.FatalFoundationFrameworkException;
import com.cboe.infrastructureServices.traderService.*;
import com.cboe.infrastructureServices.systemsManagementService.*;
import com.cboe.domain.startup.*;
import com.cboe.interfaces.internalBusinessServices.ProductRoutingService;
import com.cboe.interfaces.internalBusinessServices.ProductRoutingServiceHome;
import com.cboe.interfaces.businessServicesClient.ReplyHandlerManagerClient;
import com.cboe.interfaces.businessServicesClient.ReplyHandlerClient;
import com.cboe.exceptions.*;
import com.cboe.util.*;
import com.cboe.client.util.ClientObjectResolver;
import java.util.*;
import java.lang.reflect.*;

/**
 * BusinessServiceClientRoutingProxy is an abstract class which is designed to be the base
 * for all routing proxies.  The home for which the proxy is created from has to be a class
 * that is a subclass of ProxyBOHome.
 *
 * @date December 17, 2008
**/

public abstract class BusinessServiceClientRoutingProxy
    extends BObject
{
    // Just to identify what we are are processing - class data or product data
    public static int CLASS  = 1;
    public static int PRODUCT = 2;

    public static String RouteName = "routename";
    public static String SessionName = "sessionname";

    // Collection to maintain service reference to route mappings
    protected HashMap<String, org.omg.CORBA.Object> routeMap;
    protected HashMap<String, org.omg.CORBA.Object> uniqueServiceRefByProcess;
    protected ProductRoutingService routingService;
    protected ArrayList<org.omg.CORBA.Object> ahsRouteMap;

    // Generic reply handler manager instance
    protected ReplyHandlerManagerClient replyHandlerManager;
    protected boolean serviceState;

    /**
     * Default constructor
    **/
    public BusinessServiceClientRoutingProxy()
    {
    }

    /**
     * Subclasses should implement the initialize method
     */
    public abstract void initialize();


    /**
     * @return boolean
     */
    public boolean isReady()
    {
        return serviceState;
    }

    /**
     * Overloaded BObject method that initializes the necessary attributes
    **/
    public void create( String name )
    {
        serviceState = false;
        setName( name );
        try
        {
            ProductRoutingServiceHome routingServiceHome = (ProductRoutingServiceHome)(HomeFactory.getInstance().findHome( ProductRoutingServiceHome.HOME_NAME ));
            routingService = routingServiceHome.find();
            if ( routingService == null )
            {
                Log.alarm( this, "ProductRoutingService is null - Operations will not succeed" );
                throw new FatalFoundationFrameworkException( "ProductRoutingService is null - Operations will not succeed");
            }
        }
        catch( CBOELoggableException e )
        {
            Log.alarm( this, "Error locating ProductRoutingServiceHome - Operations will not succeed" );
            // Exception was logged when it was created, we do not need to log it here
            throw new FatalFoundationFrameworkException(e, "Error locating ProductRoutingServiceHome - Operations will not succeed : ");
        }
        routeMap = new HashMap();
        uniqueServiceRefByProcess = new HashMap();
        ahsRouteMap = new ArrayList<org.omg.CORBA.Object>();
        
    }
    protected  org.omg.CORBA.Object getService(int key)throws DataValidationException, SystemException
    {
        return ahsRouteMap.get(key);
    }
    /**
     * Locates a route based on the classKey
     * @param sessionName
     * @param classKey - A unique class identifier
     * @return Object The service object
     **/

    protected org.omg.CORBA.Object getServiceByClass(String sessionName, int classKey )
        throws DataValidationException, SystemException
    {
        org.omg.CORBA.Object service = null;
        try
        {
            HashSet <String> routeNames = routingService.getRouteNamesByClass( classKey );
           
            for ( String routeName : routeNames )
            {
                String key = getRouteMapKey(sessionName, routeName);
                service = routeMap.get(key);
                if (service != null )
                {
                    break;
                }             
            }            
        }
        catch (Exception e)
        {
            Log.exception(this, "Error in getServiceByClass :" + e.getMessage(), e);
        }
        if (service == null) 
        {
            StringBuilder msg = new StringBuilder(60);
            msg.append("No service found for session ").append(sessionName).append(" and class ").append(classKey);
            String message = msg.toString();
            Log.alarm( this, message );
            throw ExceptionBuilder.dataValidationException( message , 0 );
        }
        return service;
    }
    /**
     * This method gets the string for RouteMap Key from the Route Map given the sessionName and Classkey.
     * 
     * @param sessionName
     * @param classKey
     * @return
     * @throws DataValidationException
     * @throws SystemException
     */
    public String getRouteMapKeyByClass(String sessionName, int classKey)
    throws  DataValidationException, SystemException
	{
	    String key = null;
	    org.omg.CORBA.Object service = null;
	    try
	    {
	        HashSet routeNames = routingService.getRouteNamesByClass( classKey );
	        Iterator iterator = routeNames.iterator();
	        while( iterator.hasNext() )
	        {
	            key = getRouteMapKey( sessionName, (String) iterator.next() );
	            service = (org.omg.CORBA.Object) routeMap.get( key );
	            if( service != null )
	            {
	                break;
	            }                    
	        }                
	    }
	    catch (Exception e)
	    {
	        Log.exception( this, "getRouteMapKeyByClass failed to find a service", e );
	    }
	    if (service == null) 
	    {
	        String message = "No service found for session " + sessionName + " and class " + classKey;
	        Log.alarm( this, message );
	        throw ExceptionBuilder.dataValidationException( message , 0 );
	    }
	    return key;            
	}
    
    /**
     * This method gets the string for RouteMap Key from the Route Map given the sessionName and Productkey.
     * 
     * @param sessionName
     * @param classKey
     * @return
     * @throws DataValidationException
     * @throws SystemException
     */
	public String getRouteMapKeyByProduct(String sessionName, int productKey)
	    throws  DataValidationException, SystemException
	{
	    String key = null;
	    org.omg.CORBA.Object service = null;
	    HashSet routeNames = null;
	    try
	    {
	        routeNames = routingService.getRouteNamesByProduct( productKey );                       
	    }
	    catch (Exception e) 
	    {
	        // try to request the product from global service
	        if (Log.isDebugOn()) 
	        {
	            Log.debug( this, "No route found. Call routingService.requestProductBySessionForKey" );
	        }
	        routingService.requestProductBySessionForKey( sessionName, productKey );
	        routeNames = routingService.getRouteNamesByProduct( productKey );
	    }
	    
	    if ( routeNames != null ) 
	    {
	        Iterator iterator = routeNames.iterator();
	        while ( iterator.hasNext() ) 
	        {
	            key = getRouteMapKey( sessionName, (String) iterator.next() );
	            service = (org.omg.CORBA.Object) routeMap.get( key );
	            if ( service != null )
	            {
	                break;
	            }
	        }
	    }
	    
	    if (service == null) 
	    {
	        String message = "No service found for session " + sessionName + " and product " + productKey;
	        Log.alarm( this, message );
	        throw ExceptionBuilder.dataValidationException( message , 0 );
	    }
	    
	    return key;                 
	}
	
	/**
     * This method gets the Corba Object from the RouteMap given the RouteMap Key.
     * 
     * @param sessionName
     * @param classKey
     * @return
     * @throws DataValidationException
     * @throws SystemException
     */
	public org.omg.CORBA.Object getRouteObject(String key)
    throws  DataValidationException, SystemException
	{            
	    org.omg.CORBA.Object service = (org.omg.CORBA.Object) routeMap.get(key);      
	    
	    if (service == null) 
	    {
	        String message = "No service found for session " + key;
	        Log.alarm( this, message );
	        throw ExceptionBuilder.dataValidationException( message , 0 );
	    }
	    return service;            
	}

    /**
     * Locates a route based on the productKey
     * @param sessionName
     * @param productKey - A unique product identifier
     * @return The service object
     **/

    protected org.omg.CORBA.Object getServiceByProduct(String sessionName, int productKey )
            throws DataValidationException, SystemException
    {
        org.omg.CORBA.Object service = null;
        HashSet <String> routeNames = null;
        try
        {
            routeNames = routingService.getRouteNamesByProduct(productKey);
        }
        catch (Exception e)
        {
            Log.exception(this, "No route found. Call routingService.requestProductBySessionForKey :" + 
                    e.getMessage(), e);
            Log.alarm(this, "No route found. Call routingService.requestProductBySessionForKey" );
            // try to request the product from global service
            if (Log.isDebugOn()) 
            {
                Log.debug(this, "No route found. Call routingService.requestProductBySessionForKey");
            }
            routingService.requestProductBySessionForKey(sessionName, productKey);
            routeNames = routingService.getRouteNamesByProduct(productKey);
        }
        if (routeNames != null) 
        {
            for ( String routeName : routeNames )
            {
                String key = getRouteMapKey(sessionName, routeName);
                service = routeMap.get(key);
                if ( service != null )
                {
                    break;
                }
            }
        }
        if (service == null) 
        {
            StringBuilder msg = new StringBuilder(60);
            msg.append("No service found for session ").append(sessionName).append(" and product ").append(productKey);
            String message = msg.toString();
            Log.alarm( this, message );
            throw ExceptionBuilder.dataValidationException(message , 0);
        }
        return service;
    }

    /**
     * Return all the services in a session
     */
    protected ArrayList getServicesBySession(String sessionName)
            throws DataValidationException, SystemException
    {
        if ( routeMap == null )
        {
            if (Log.isDebugOn()) 
            {
                Log.debug( this, "Route collection is invalid" );
            }            
            throw ExceptionBuilder.systemException( "Route collection is invalid", 0 );
        }
        
        Set<String> keys = routeMap.keySet();
        ArrayList services = new ArrayList();
        for ( String key : keys)
        {
            if( key.startsWith(sessionName))
            {
                services.add(routeMap.get(key));
            }
        }
        return services;
    }

    /**
     * Return all the services for a product
     */
    protected ArrayList getServicesByProduct(int productKey)
            throws DataValidationException, SystemException
    {
        if ( routeMap == null )
        {
            if (Log.isDebugOn()) 
            {
                Log.debug( this, "Route collection is invalid" );
            }            
            throw ExceptionBuilder.systemException( "Route collection is invalid", 0 );
        }
        Set<String> keys = routeMap.keySet();
        HashSet<String> routeNames = routingService.getRouteNamesByProduct( productKey );
        ArrayList services = new ArrayList();
        
        for ( String key : keys )
        {
            for ( String routeName : routeNames )
            {
                if ( key.endsWith(routeName) )
                {
                   services.add(routeMap.get(key));
                }
            }            
        }
        if (services.size() < 1)
        {
            StringBuilder msg = new StringBuilder(40);
            msg.append("No service found for product ").append(productKey);
            String message = msg.toString();
            Log.alarm( this, message );
            throw ExceptionBuilder.dataValidationException( message , 0 );
        }        
        return services;
    }

    /**
     * Return all the services for a class
     */
    protected ArrayList getServicesByClass(int classKey)
            throws DataValidationException, SystemException
    {
        if ( routeMap == null )
        {
            if (Log.isDebugOn())
            {
                Log.debug( this, "Route collection is invalid" );
            }
            throw ExceptionBuilder.systemException( "Route collection is invalid", 0 );            
        }
        HashSet<String> routeNames = routingService.getRouteNamesByClass( classKey );
        ArrayList services = new ArrayList();
        
        for ( String key : routeMap.keySet() )
        {
            for ( String routeName : routeNames )
            {
                if (key.endsWith(routeName))
                {
                    services.add(routeMap.get(key));
                }
            }
        }

        if (services.isEmpty())
        {
            StringBuilder msg = new StringBuilder(40);
            msg.append("No service found for class ").append(classKey);
            String message = msg.toString();
            Log.alarm( this, message );
            throw ExceptionBuilder.dataValidationException( message , 0 );
        }

        return services;
    }


    /**
     * Returns the reply handler back to the pool
     * @param handler - the reply handler instance
     */
    public void releaseReplyHandler( ReplyHandlerClient handler )
    {
        if ( replyHandlerManager != null )
        {
            replyHandlerManager.returnReplyHandler( handler );
        }
    }

    /**
     * Obtains a reply handler for the client
     * @return ReplyHandler
     **/
    public ReplyHandlerClient reserveReplyHandler()
    {
        if ( replyHandlerManager == null )
        {
            return null;
        }
        return replyHandlerManager.findReplyHandler();
    }

    /**
     * Groups classes or products by services
     * @param classArray, The array of classes that need to be subgrouped
     * @return HashMap, service to class grouping
     **/
    public HashMap groupByService(String sessionName, int[] classArray, int groupingType  )
        throws DataValidationException, SystemException
    {
        HashMap svcGrouping = new HashMap();
        for (int classKey : classArray)
        {
            org.omg.CORBA.Object svc = null;
            if ( groupingType == BusinessServiceClientRoutingProxy.CLASS )
            {
                svc = getServiceByClass(sessionName, classKey );
            }
            else if ( groupingType == BusinessServiceClientRoutingProxy.PRODUCT )
            {
                svc = getServiceByProduct(sessionName, classKey );
            }
            else
            {
                if (Log.isDebugOn())
                {
                    Log.debug( this, "Invalid grouping type specified:" + groupingType );
                }
            }
            if ( svc != null )
            {
                ArrayList classCollection = (ArrayList)svcGrouping.get( svc );
                if ( classCollection == null )
                {
                    classCollection = new ArrayList();
                    svcGrouping.put( svc, classCollection );
                }
                classCollection.add( classKey );
            }
        }
        return svcGrouping;
    }

    /**
     * Filter the services by filter string defined in the properties file
     *
     * @return  DirectoryQueryResult[]
     * @param   services
     *
     */
    public DirectoryQueryResult[] filterServices(DirectoryQueryResult[] services)
    {
        ArrayList collection = new ArrayList();
        String filter;
        try
        {
            filter = ApplicationPropertyHelper.getProperty("filterString");
        }
        catch (Exception e)
        {
            filter = null;
        }
        if ( filter == null || filter.trim().length() == 0)
        {
            return services;
        }
        for ( int i=0; i < services.length; i++ )
        {
            String routeName = services[i].getProperties().getProperty("routename");
            if ( routeName.startsWith(filter))
            {
                collection.add(services[i]);
            }
        }
        int size = collection.size();
        DirectoryQueryResult[] results = new DirectoryQueryResult[size];
        for ( int i=0; i < size; i++ )
        {
            results[i] = (DirectoryQueryResult) collection.get(i);
        }
        return results;
    }

    /**
     * To determine a destination of a service, sessionName and routeName are needed.
     * The key of the routeMap is constructed by combining the sessionName and routeName
     *
     * @return String which is the key to the route map
     * @param  sessionName
     * @param  routeName
     */
    protected String getRouteMapKey(String sessionName, String routeName)
    {
        StringBuilder key = new StringBuilder(50);
        key.append(sessionName).append(':').append(routeName);
        return key.toString();
    }

    /**
     * Query trader service for a list of services with a specified serviceTypeName
     * and constraints
     *
     * @param serviceTypeName,
     * @param constraints
     */
    protected DirectoryQueryResult[] getBusinessServicesFor(String serviceTypeName, String constraints)
    {
        if (serviceTypeName == null ) 
        {
            return null;
        }
        TraderService traderService = FoundationFramework.getInstance().getTraderService();
        if ( traderService == null )
        {
            return null;
        }
        if (Log.isDebugOn())
        {
            Log.debug( this, "Querying traderservice for available services " + serviceTypeName );
        }
        DirectoryQueryResult services[] = traderService.queryDirectory( serviceTypeName, constraints );
        
        DirectoryQueryResult finalList[] = filterServices(services);
        if ( finalList.length == 0 )
        {
            return null;
        }
        return finalList;
    }

    /**
     * Subclass should implement this method to return the Service Helper class name
     */
    protected abstract String getHelperClassName();

    /**
     * Return the service type name based on the service helper
     * @param internal to overload the method export(), identify this method can be used inside this class only
     * @return String Service type name
     * -MW
     */
     private String getServiceTypeName(boolean internal)
     {
        String name = null;
        try
        {
            String helperClassName = getHelperClassName();
            Class helperClass = Class.forName(helperClassName);
            Method aMethod = helperClass.getMethod("id", null);
            name = (String) aMethod.invoke(null, null);
        } 
        catch ( Exception e) 
        {}
        return name;
    }

    /* Overload the private method, let it look up generic service type name -MW*/
    protected String getServiceTypeName()
    {
         String serviceName = getServiceTypeName(true);
         String genericName = null;
         genericName = com.cboe.domain.util.GenericName.getGenericName( serviceName, ':');
         if (null != genericName )
         {
            return genericName;
         }
         return serviceName;
    }

    /**
     * Delegate the method calling to the actual business service helper
     */
     protected org.omg.CORBA.Object narrow(org.omg.CORBA.Object anObjectReference)
     {

        org.omg.CORBA.Object service = null;
        try
        {
            ClientRoutingBOHome myHome = (ClientRoutingBOHome)this.getBOHome();
            service = ClientObjectResolver.resolveObject(anObjectReference,getHelperClassName(), myHome.getRoundTripTimeOutValueProperty());
        }
        catch (Exception e)
        {
            Log.alarm(this,"ObjectResolver.resolveObject with HelperClassName :" + getHelperClassName() + ", Caught exception: " + e.toString());
            Log.exception(this, e);
        }
        if (null == service)
        {
            Log.alarm(this, "NULL returned by calling ObjectResolver.resolveObject with HelperClassName :" + getHelperClassName());
        }
        return service;
     }
     
     protected org.omg.CORBA.Object createDecoratorForOutboundCalls (org.omg.CORBA.Object anObjectReference)
     {
         return anObjectReference;
     }
}//EOF
