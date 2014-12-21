package com.cboe.proxy.businessServicesClient;

import com.cboe.domain.startup.ClientRoutingBOHome;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.BOInterceptor;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.foundationFramework.POAHelper;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.foundationFramework.utilities.RouteNameHelper;
import com.cboe.infrastructureServices.orbService.NoSuchPOAException;
import com.cboe.infrastructureServices.traderService.TraderService;
import org.omg.PortableServer.Servant;

import java.lang.reflect.Method;

/**
 * This class is designed as the common base of all Client routing proxy homes. The main
 * responsibility of routing proxy home is
 * 1.   Initializing the home with the routing proxy
 * 2.   Starting the proxy
 *
 * @Date 12/18/2008
 */
public abstract class ClientRoutingProxyHomeBase
    extends ClientRoutingBOHome
{
    private BusinessServiceClientRoutingProxy routingProxy;
    private Servant theTie;
    protected org.omg.CORBA.Object callbackObject;
    protected boolean initialized = false;

    /**
     * Default Constructor
     */
    public ClientRoutingProxyHomeBase() 
    {
    }

    /**
     * Initialize the home by creating a routing proxy
    **/
    public void clientInitialize()
    {
        Log.information( this, "Initializing service");

        if ( routingProxy == null )
        {
            routingProxy =  createRoutingProxy();
            addToContainer( routingProxy );
            String className = routingProxy.getClass().getName();
            if (className.lastIndexOf(".") > 0) 
            {
                className = className.substring(className.lastIndexOf(".")+1, className.length());
            }                
            routingProxy.create( className ); 
        }
        Log.information( this, "Initialization complete");
    }
    
    public BOInterceptor createInterceptor(BObject obj) throws ClassNotFoundException, IllegalAccessException, InstantiationException, ClassCastException
    {
        // ClientRoutingProxy is called via a code path that already has an interceptor.
        // We do not need to create an additional one.
        return null;
    }

    public void clientStart()
    {
    }

    /**
     * Initialize the routing proxy
     */
    private boolean initializeRoutingProxy()
    {
        if ( routingProxy  == null )
        {
            Log.alarm( this, "Cannot start - Not initialized" );
            return false;
        }
        Log.information( this, "Starting service");
        if (Log.isDebugOn())
        {
            Log.debug( this, "Sending initialization request to RoutingProxy" );
        }
        routingProxy.initialize();
        if ( ! routingProxy.isReady() )
        {
            Log.alarm( this, "Startup failure");
            return false;
        }
        return true;
    }

    public void goMaster(boolean failover)
    {
        if (!clientIsRemote())
        {
            initialized = initializeRoutingProxy();
            if ( ! initialized ) {
                Log.alarm(this, "Cannot go master, service failed to start");
                return;
            }
            super.goMaster(failover);

            Log.information(this, "Connecting to the ORB.");
            theTie = createServant();
            try
            {
                callbackObject = POAHelper.connect(theTie, this);
            }
            catch (NoSuchPOAException e)
            {
                Log.alarm(this, "Unable to connect routing proxy service to POA: " + theTie.getClass());
            }
            // ClientRoutingProxy is a utility within the client, not a service for other programs
            Log.information(this, "goMaster will not export the service");
        }
    }

    public void goSlave()
    {
        if (clientIsRemote())
        {
            super.goSlave();
        }
    }

    protected Object getProxyInstance()
    {
        return routingProxy;
    }

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
        {
            Log.exception(this, e);
        }
        if (Log.isDebugOn())
        {
            Log.debug(this, "getServiceTypeName:" + name );
        }

        return name;
    }

    /**
     * Overload the private method, let it look up generic service type name
     * instead of interface id, can be called by all the sub-classes
     * @return String
     */
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
     * gets the foundation framework instance 
     * and then uses it to get the TraderService
     * @return TraderService
     */
    protected TraderService getTraderService()
    {
        return FoundationFramework.getInstance().getTraderService();
    }
    
    /**
     * gets the remote route name needed to locate the V2oFrontEnd 
     * and builds the constraint.
     * @return String
     */
    protected String getConstraint()
    {
        String rrn = RouteNameHelper.getRemoteRouteName();
        StringBuilder constraint = new StringBuilder(rrn.length()+13);
        constraint.append("routename == ").append(rrn);
        return constraint.toString();
    }

    /**
     * Create a new instance of routing proxy. Subclasses should implement this method
     */
    protected abstract BusinessServiceClientRoutingProxy createRoutingProxy();

    /**
     * create the tie object. Subclasses should implement this method
     */
    protected abstract Servant createServant();

    /**
     * get the service name for exporting. Subclass should implement this method
     */
    protected abstract String getServiceName();
    
    protected abstract String getHelperClassName();

}
