package com.cboe.proxy.businessServicesClient;

import org.omg.PortableServer.Servant;

import com.cboe.idl.businessServices.LightOrderHandlingServiceHelper;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.businessServices.LightOrderHandlingService;
import com.cboe.interfaces.businessServices.LightOrderHandlingServiceHome;


public 	class LightOrderHandlingServiceClientRoutingProxyHomeImpl
	extends ClientRoutingProxyHomeBase
	implements LightOrderHandlingServiceHome
{
    private LightOrderHandlingService myLightOrderHandlingService;
   
    public LightOrderHandlingServiceClientRoutingProxyHomeImpl()
    {
    }

    public LightOrderHandlingService create( )
    {
    	return find();
    }

    public	LightOrderHandlingService create( String name )
    {
    	return find();
    }

    public	LightOrderHandlingService find()
    {
       
        if (myLightOrderHandlingService == null)
        {
            if (!clientIsRemote())
            {
                // running local (within CBOE network), get routing proxy
                myLightOrderHandlingService = (LightOrderHandlingService) getProxyInstance();
            }
            else
            { 
                // running remote, throw exception - should never happen
                
                    Log.exception(new Exception("Trying to run LightOrderHandlingService remotely.  NOT SUPPORTED!"));
                           
            }
        }
        
        return	myLightOrderHandlingService;
    }

    /**
     * Create a new instance of routing proxy.
     */
    protected BusinessServiceClientRoutingProxy createRoutingProxy()
    {
        return new LightOrderHandlingServiceClientRoutingProxy();
    }

    /**
     * create the tie object.
     */
    protected Servant createServant()
    {
        return new com.cboe.idl.businessServices.POA_LightOrderHandlingService_tie( find() );
    }

    /**
     * get the service name for exporting.
     */
    protected String getServiceName()
    {
        return com.cboe.idl.businessServices.LightOrderHandlingServiceHelper.id();
    }
    
    protected String getHelperClassName()
    {
            return LightOrderHandlingServiceHelper.class.getName();
    }
}


