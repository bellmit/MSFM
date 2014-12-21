package com.cboe.proxy.businessServicesClient;

import com.cboe.client.util.ClientObjectResolver;
import com.cboe.idl.internalBusinessServices.TradeMaintenanceServiceHelper;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.traderService.DirectoryQueryResult;
import com.cboe.interfaces.internalBusinessServices.TradeMaintenanceServiceHome;
import com.cboe.interfaces.internalBusinessServices.TradeMaintenanceService;
import com.cboe.proxy.internalBusinessServices.TradeMaintenanceServiceProxy;
import org.omg.PortableServer.Servant;

/** Home for TradeMaintenanceServiceClientRoutingProxy
 */
public class TradeMaintenanceServiceClientRoutingProxyHomeImpl
	extends ClientRoutingProxyHomeBase
	implements TradeMaintenanceServiceHome
{
    private TradeMaintenanceService myTradeMaintenanceService;

    /**
     * Default Constructor
     */
    public TradeMaintenanceServiceClientRoutingProxyHomeImpl()
    {
    }

    public TradeMaintenanceService create( )
    {
	    return find();
    }

    public TradeMaintenanceService create( String name )
    {
	    return find();
    }

    public TradeMaintenanceService find()
    {
        if (myTradeMaintenanceService == null)
        {
            if (!clientIsRemote())
            {
                // running local (within CBOE network), get routing proxy
                myTradeMaintenanceService = (TradeMaintenanceService) getProxyInstance();
            }
            else
            {
                // running remote, get proxy to Frontend
                DirectoryQueryResult[] responses = getTraderService().queryDirectory(getServiceTypeName(), getConstraint());
                if (responses.length == 0)
                {
                    throw new NullPointerException("Could not find TradeMaintenanceService");
                }
                else if (responses.length > 1)
                {
                    Log.alarm(this, "Found " + responses.length + " TradeMaintenanceServices - using first service");
                }
                com.cboe.idl.internalBusinessServices.TradeMaintenanceService service = (com.cboe.idl.internalBusinessServices.TradeMaintenanceService) ClientObjectResolver.resolveObject(responses[0].getObjectReference(),TradeMaintenanceServiceHelper.class.getName() );

                TradeMaintenanceServiceProxy serviceProxy = new TradeMaintenanceServiceProxy(service);
                addToContainer(serviceProxy);

                myTradeMaintenanceService = serviceProxy;
            }
        }

    	return myTradeMaintenanceService;
    }

    /**
     * Create a new instance of routing proxy.
     */
    protected BusinessServiceClientRoutingProxy createRoutingProxy()
    {
        return new TradeMaintenanceServiceClientRoutingProxy();
    }

    /**
     * create the tie object.
     */
    protected Servant createServant()
    {
        return new com.cboe.idl.internalBusinessServices.POA_TradeMaintenanceService_tie( find() );
    }

    /**
     * get the service name for exporting.
     */
    protected String getServiceName()
    {
        return com.cboe.idl.internalBusinessServices.TradeMaintenanceServiceHelper.id();
    }

    protected String getHelperClassName()
    {
        return TradeMaintenanceServiceHelper.class.getName();
    }
}
