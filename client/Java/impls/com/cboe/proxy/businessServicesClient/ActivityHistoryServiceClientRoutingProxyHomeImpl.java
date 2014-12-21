package com.cboe.proxy.businessServicesClient;

import org.omg.PortableServer.*;

import com.cboe.idl.internalBusinessServices.ActivityHistoryServiceHelper;
import com.cboe.interfaces.internalBusinessServices.ActivityHistoryService;
import com.cboe.interfaces.internalBusinessServices.ActivityHistoryServiceHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;



/**
 * This class is the home for the ActivityHistoryServiceClientRoutingProxy
 * @date 12/17/2008
 */
public class ActivityHistoryServiceClientRoutingProxyHomeImpl
	         extends ClientRoutingProxyHomeBase implements ActivityHistoryServiceHome
{
    private ActivityHistoryService myActivityHistoryService;

    public ActivityHistoryServiceClientRoutingProxyHomeImpl()
    {
    }

    public ActivityHistoryService create( )
    {
    	return find();
    }

    public	ActivityHistoryService create( String name )
    {
    	return find();
    }

    public	ActivityHistoryService find()
    {
        if (myActivityHistoryService == null)
        {
            if (!clientIsRemote())
            {              
                // running local (within CBOE network), get routing proxy
                myActivityHistoryService = (ActivityHistoryService) getProxyInstance();                
            }
            else
            { 
                // running remote, throw exception - should never happen              
                Log.exception(new Exception("Trying to run ActivityHistoryService remotely.  NOT SUPPORTED!"));                          
            }                     
        }
    	return	myActivityHistoryService;
    }

    /**
     * Create a new instance of routing proxy.
     */
    protected BusinessServiceClientRoutingProxy createRoutingProxy()
    {
        
        return new ActivityHistoryServiceClientRoutingProxy();
    }

    /**
     * create the tie object.
     */
    protected Servant createServant()
    {
        return new com.cboe.idl.internalBusinessServices.POA_ActivityHistoryService_tie( find() );
    }

    /**
     * get the service name for exporting.
     */
    protected String getServiceName()
    {
        
        return com.cboe.idl.internalBusinessServices.ActivityHistoryServiceHelper.id();
    }
    
    protected String getHelperClassName()
    {
       
        return ActivityHistoryServiceHelper.class.getName();
        
     
    }
}
