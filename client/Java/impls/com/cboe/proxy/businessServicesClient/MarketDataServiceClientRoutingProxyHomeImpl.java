package com.cboe.proxy.businessServicesClient;

import com.cboe.idl.businessServices.MarketDataServiceHelper;
import com.cboe.infrastructureServices.foundationFramework.utilities.RouteNameHelper;
import com.cboe.interfaces.businessServices.MarketDataServiceHome;
import com.cboe.interfaces.businessServices.MarketDataService;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.loggingService.*;
import com.cboe.infrastructureServices.traderService.*;
import com.cboe.proxy.businessServices.MarketDataServiceProxy;
import com.cboe.client.util.ClientObjectResolver;

import org.omg.PortableServer.*;
import com.cboe.infrastructureServices.orbService.*;

import com.cboe.proxy.businessServices.OrderBookServiceProxy;
import com.cboe.util.*;
import java.util.*;

/**
 * This class is the home for the MarketDataServiceRoutingProxy
 * @date 12/31/2008
 * 
 */
public 	class MarketDataServiceClientRoutingProxyHomeImpl
	extends ClientRoutingProxyHomeBase
	implements MarketDataServiceHome
{
    private MarketDataService myMarketDataService;

    public MarketDataServiceClientRoutingProxyHomeImpl()
    {
    }

    public MarketDataService create( )
    {
	    return find();
    }

    public	MarketDataService create( String name )
    {
	    return find();
    }

    public	MarketDataService find()
    {
        if (myMarketDataService == null)
        {
            if (!clientIsRemote())
            {
                // running local (within CBOE network), get routing proxy
                myMarketDataService = (MarketDataService) getProxyInstance();
            }
            else
            {
                // running remote, get proxy to Frontend
                DirectoryQueryResult[] responses = getTraderService().queryDirectory(getServiceTypeName(), getConstraint()); 
                if (responses.length == 0)
                {
                    throw new NullPointerException("Could not find MarketDataService");
                }
                else if (responses.length > 1)
                {
                    Log.alarm(this, "Found " + responses.length + " MarketDataServices - using first service");
                }
               
                com.cboe.idl.businessServices.MarketDataService service = (com.cboe.idl.businessServices.MarketDataService) ClientObjectResolver.resolveObject(responses[0].getObjectReference(), MarketDataServiceHelper.class.getName() );
                MarketDataServiceProxy serviceProxy = new MarketDataServiceProxy(service);
                addToContainer(serviceProxy);
                
                myMarketDataService = serviceProxy;                
            }
            
        }
        
        return myMarketDataService;    		
    }

    /**
     * Create a new instance of routing proxy.
     */
    protected BusinessServiceClientRoutingProxy createRoutingProxy()
    {
        return new MarketDataServiceClientRoutingProxy();
    }

    /**
     * create the tie object.
     */
    protected Servant createServant()
    {
        return new com.cboe.idl.businessServices.POA_MarketDataService_tie( find() );
    }

    /**
     * get the service name for exporting.
     */
    protected String getServiceName()
    {
        return com.cboe.idl.businessServices.MarketDataServiceHelper.id();
    }
    
    protected String getHelperClassName()
    {
            return MarketDataServiceHelper.class.getName();
    }

}
