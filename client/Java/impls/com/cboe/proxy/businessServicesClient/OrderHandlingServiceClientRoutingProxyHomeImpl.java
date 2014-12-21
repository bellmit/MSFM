package com.cboe.proxy.businessServicesClient;

import com.cboe.proxy.businessServices.OrderHandlingServiceProxy;
import com.cboe.idl.businessServices.OrderHandlingServiceHelper;
import com.cboe.interfaces.businessServices.OrderHandlingServiceHome;
import com.cboe.interfaces.businessServices.OrderHandlingService;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;
import com.cboe.infrastructureServices.traderService.*;
import org.omg.PortableServer.*;
import com.cboe.client.util.ClientObjectResolver;

/**
 * This class is the home for the OrderHandlingServiceRoutingProxy
 * @date January 12 2009
 */
public 	class OrderHandlingServiceClientRoutingProxyHomeImpl
	extends ClientRoutingProxyHomeBase
	implements OrderHandlingServiceHome
{
    private OrderHandlingService myOrderHandlingService;
    
    private static final String MAX_THREAD_POOL_SIZE = "OHSRoutingProxyThreadPoolSize";
    private static final String DEFAULT_MAX_THREAD_POOL_SIZE = "100";

    public OrderHandlingServiceClientRoutingProxyHomeImpl()
    {
    }

    public OrderHandlingService create( )
    {
    	return find();
    }

    public	OrderHandlingService create( String name )
    {
    	return find();
    }
    
    public	OrderHandlingService find()
    {
        if (myOrderHandlingService == null)
        {
            if (!clientIsRemote())
            {
                // running local (within CBOE network), get routing proxy
                myOrderHandlingService = (OrderHandlingService) getProxyInstance();
            }
            else
            {
                // running remote, get proxy to Frontend
                com.cboe.idl.businessServices.OrderHandlingService corbaService = null;
                DirectoryQueryResult[] responses = getTraderService().queryDirectory(getServiceTypeName(), getConstraint());
                if (responses.length == 0)
                {
                    throw new NullPointerException("Could not find OrderHandlingService");
                }
                else if (responses.length > 1)
                {
                    Log.alarm(this, "Found " + responses.length + " OrderHandlingServices - using first service");                    
                }
                try
                {                                        
                    corbaService = (com.cboe.idl.businessServices.OrderHandlingService) ClientObjectResolver.resolveObject(responses[0].getObjectReference(),
                            OrderHandlingServiceHelper.class.getName(), getRoundTripTimeOutValueProperty());
                }
                catch (Exception e)
                {
                    Log.alarm(this,"ObjectResolver.resolveObject with HelperClassName :" + getHelperClassName() + ", Caught exception: " + e.toString());
                    Log.exception(this, e);
                }
                
                OrderHandlingServiceProxy serviceProxy = new OrderHandlingServiceProxy(corbaService);
                serviceProxy.create("OrderHandlingServiceProxy");
                addToContainer(serviceProxy);
                
                myOrderHandlingService = serviceProxy;
            }
        }
        
    	return	myOrderHandlingService;
    }

    /**
     * Create a new instance of routing proxy.
     */
    protected BusinessServiceClientRoutingProxy createRoutingProxy()
    {
        return new OrderHandlingServiceClientRoutingProxy(getMaxThreadPoolSize());
    }

    /**
     * create the tie object.
     */
    protected Servant createServant()
    {
        return new com.cboe.idl.businessServices.POA_OrderHandlingService_tie( find() );
    }

    /**
     * get the service name for exporting.
     */
    protected String getServiceName()
    {
        return com.cboe.idl.businessServices.OrderHandlingServiceHelper.id();
    }
 
    protected String getHelperClassName()
    {
        return OrderHandlingServiceHelper.class.getName();
    }
    
    /**
     * Gets Max Thread Pool Size.
     *
     * @return int
     */
    private int getMaxThreadPoolSize()
    {
    	try
    	{
    		return Integer.parseInt(getProperty( MAX_THREAD_POOL_SIZE , DEFAULT_MAX_THREAD_POOL_SIZE ));   
    	}
    	 catch( NumberFormatException nfe )
         {
    		 Log.exception( this, nfe );
    		 Log.alarm( this, "Failed to find property " + MAX_THREAD_POOL_SIZE + ", using default value: " + DEFAULT_MAX_THREAD_POOL_SIZE);             
             return Integer.parseInt(DEFAULT_MAX_THREAD_POOL_SIZE);
         }    	   
    }
}
