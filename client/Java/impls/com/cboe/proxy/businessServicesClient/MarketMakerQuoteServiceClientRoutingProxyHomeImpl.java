package com.cboe.proxy.businessServicesClient;

import com.cboe.proxy.businessServices.MarketMakerQuoteServiceProxy;
import com.cboe.client.util.ClientObjectResolver;
import com.cboe.idl.businessServices.MarketMakerQuoteServiceHelper;
import com.cboe.interfaces.businessServices.MarketMakerQuoteService;
import com.cboe.interfaces.businessServices.MarketMakerQuoteServiceHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.traderService.*;
import org.omg.PortableServer.*;

/**
 * This class is the home for the MarketMakerQuoteServiceClientRoutingProxy
 * @date January 07, 2009
 */
public 	class MarketMakerQuoteServiceClientRoutingProxyHomeImpl
	extends ClientRoutingProxyHomeBase
	implements MarketMakerQuoteServiceHome
{
    private MarketMakerQuoteService myMarketMakerQuoteService;
   
    public MarketMakerQuoteServiceClientRoutingProxyHomeImpl()
    {
    }

    public MarketMakerQuoteService create( )
    {
    	return find();
    }

    public	MarketMakerQuoteService create( String name )
    {
    	return find();
    }

    public	MarketMakerQuoteService find()
    {
       
        if (myMarketMakerQuoteService == null)
        {
            if (!clientIsRemote())
            {
                // running local (within CBOE network), get routing proxy
                myMarketMakerQuoteService = (MarketMakerQuoteService) getProxyInstance();
            }
            else
            { 
                // running remote, get proxy to Frontend
                com.cboe.idl.businessServices.MarketMakerQuoteService corbaService = null;
                
                DirectoryQueryResult[] responses = getTraderService().queryDirectory(getServiceTypeName(), getConstraint());
                if (responses.length == 0)
                {
                    throw new NullPointerException("Could not find MarketMakerQuoteService");
                }
                else if (responses.length > 1)
                {
                    Log.alarm(this, "Found " + responses.length + " MarketMakerQuoteServices - using first service");
                }
                try
                {
                    corbaService = (com.cboe.idl.businessServices.MarketMakerQuoteService) ClientObjectResolver.resolveObject(responses[0].getObjectReference(),
                            com.cboe.idl.businessServices.MarketMakerQuoteServiceHelper.class.getName(), getRoundTripTimeOutValueProperty());
                }
                catch (Exception e)
                {
                    Log.alarm(this,"ClientObjectResolver.resolveObject with HelperClassName :" + getHelperClassName() + ", Caught exception: " + e.toString());
                    Log.exception(this, e);
                }
                
                MarketMakerQuoteServiceProxy serviceProxy = new MarketMakerQuoteServiceProxy(corbaService);
                serviceProxy.create("MarketMakerQuoteServiceProxy");
                addToContainer(serviceProxy);
                
                myMarketMakerQuoteService = serviceProxy;               
            }
        }
        
        return	myMarketMakerQuoteService;
    }

    /**
     * Create a new instance of routing proxy.
     */
    protected BusinessServiceClientRoutingProxy createRoutingProxy()
    {
        return new MarketMakerQuoteServiceClientRoutingProxy();
    }

    /**
     * create the tie object.
     */
    protected Servant createServant()
    {
        return new com.cboe.idl.businessServices.POA_MarketMakerQuoteService_tie( find() );
    }

    /**
     * get the service name for exporting.
     */
    protected String getServiceName()
    {
        return com.cboe.idl.businessServices.MarketMakerQuoteServiceHelper.id();
    }
    
    protected String getHelperClassName()
    {
            return MarketMakerQuoteServiceHelper.class.getName();
    }
}

