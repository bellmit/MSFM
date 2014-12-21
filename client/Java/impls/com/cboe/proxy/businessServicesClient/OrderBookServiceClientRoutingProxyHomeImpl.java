package com.cboe.proxy.businessServicesClient;

import com.cboe.idl.businessServices.OrderBookServiceHelper;
import com.cboe.interfaces.businessServices.OrderBookService;
import com.cboe.interfaces.businessServices.OrderBookServiceHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.traderService.*;
import org.omg.PortableServer.*;
import com.cboe.proxy.businessServices.OrderBookServiceProxy;
import com.cboe.client.util.ClientObjectResolver;


/**
 * This class is the home for the OrderBookServiceClientRoutingProxy
 * @date 12/17/2008
 */
public class OrderBookServiceClientRoutingProxyHomeImpl
	         extends ClientRoutingProxyHomeBase implements OrderBookServiceHome
{
    private OrderBookService myOrderBookService;

    public OrderBookServiceClientRoutingProxyHomeImpl()
    {
    }

    public OrderBookService create( )
    {
    	return find();
    }

    public	OrderBookService create( String name )
    {
    	return find();
    }

    public	OrderBookService find()
    {
        if (myOrderBookService == null)
        {
            if (!clientIsRemote())
            {
                // running local (within CBOE network), get routing proxy
                myOrderBookService = (OrderBookService) getProxyInstance();
            }
            else
            { 
                // running remote, get proxy to Frontend
                DirectoryQueryResult[] responses = getTraderService().queryDirectory(getServiceTypeName(), getConstraint());
                if (responses.length == 0)
                {
                    throw new NullPointerException("Could not find OrderBookService");
                }
                else if (responses.length > 1)
                {
                    Log.alarm(this, "Found " + responses.length + " OrderBookServices - using first service");
                }
                com.cboe.idl.businessServices.OrderBookService service = (com.cboe.idl.businessServices.OrderBookService) ClientObjectResolver.resolveObject(responses[0].getObjectReference(),OrderBookServiceHelper.class.getName() );
                
                OrderBookServiceProxy serviceProxy = new OrderBookServiceProxy(service);
                addToContainer(serviceProxy);
                
                myOrderBookService = serviceProxy;             
            }
        }
        
    	return	myOrderBookService;
    }

    /**
     * Create a new instance of routing proxy.
     */
    protected BusinessServiceClientRoutingProxy createRoutingProxy()
    {
        return new OrderBookServiceClientRoutingProxy();
    }

    /**
     * create the tie object.
     */
    protected Servant createServant()
    {
        return new com.cboe.idl.businessServices.POA_OrderBookService_tie( find() );
    }

    /**
     * get the service name for exporting.
     */
    protected String getServiceName()
    {
        return com.cboe.idl.businessServices.OrderBookServiceHelper.id();
    }
    
    protected String getHelperClassName()
    {
        return OrderBookServiceHelper.class.getName();
    }
}
