package com.cboe.proxy.businessServicesClient;

import com.cboe.client.util.ClientObjectResolver;
import com.cboe.idl.businessServices.UserActivityServiceHelper;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.traderService.DirectoryQueryResult;
import com.cboe.interfaces.businessServices.UserActivityService;
import com.cboe.interfaces.businessServices.UserActivityServiceHome;
import com.cboe.proxy.businessServices.UserActivityServiceProxy;
import org.omg.PortableServer.Servant;

/**
 * This class is the home for the UserActivityServiceClientRoutingProxy
 */
public 	class UserActivityServiceClientRoutingProxyHomeImpl
	extends ClientRoutingProxyHomeBase
	implements UserActivityServiceHome
{
    private UserActivityService myUserActivityService;

    public UserActivityServiceClientRoutingProxyHomeImpl()
    {
    }

    public UserActivityService create( )
    {
    	return find();
    }

    public UserActivityService create( String name )
    {
    	return find();
    }

    public UserActivityService find()
    {
        if (myUserActivityService == null)
        {
            if (!clientIsRemote())
            {
                // running local (within CBOE network), get routing proxy
                myUserActivityService = (UserActivityService) getProxyInstance();
            }
            else
            {
                // running remote, get proxy to Frontend
                DirectoryQueryResult[] responses = getTraderService().queryDirectory(getServiceTypeName(), getConstraint());
                if (responses.length == 0)
                {
                    throw new NullPointerException("Could not find UserActivityService");
                }
                else if (responses.length > 1)
                {
                    Log.alarm(this, "Found " + responses.length + " UserActivityServices - using first service");
                }
                com.cboe.idl.businessServices.UserActivityService service = (com.cboe.idl.businessServices.UserActivityService) ClientObjectResolver.resolveObject(responses[0].getObjectReference(),UserActivityServiceHelper.class.getName() );

                UserActivityServiceProxy serviceProxy = new UserActivityServiceProxy(service);
                addToContainer(serviceProxy);

                myUserActivityService = serviceProxy;
            }
        }

    	return myUserActivityService;
    }

    /**
     * Create a new instance of routing proxy.
     */
    protected BusinessServiceClientRoutingProxy createRoutingProxy()
    {
        return new UserActivityServiceClientRoutingProxy();
    }

    /**
     * create the tie object.
     */
    protected Servant createServant()
    {
        return new com.cboe.idl.businessServices.POA_UserActivityService_tie( find() );
    }

    /**
     * get the service name for exporting.
     */
    protected String getServiceName()
    {
        return com.cboe.idl.businessServices.UserActivityServiceHelper.id();
    }

    protected String getHelperClassName()
    {
        return UserActivityServiceHelper.class.getName();
    }
}
