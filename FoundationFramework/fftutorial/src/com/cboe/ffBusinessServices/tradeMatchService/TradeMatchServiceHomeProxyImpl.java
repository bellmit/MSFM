package com.cboe.ffBusinessServices.tradeMatchService; 

import com.cboe.domain.util.GenericName;
import com.cboe.ffidl.ffBusinessServices.TradeMatchServiceHelper;
import com.cboe.ffInterfaces.TradeMatchService;
import com.cboe.ffInterfaces.TradeMatchServiceHome;
import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.foundationFramework.utilities.RouteNameHelper;
import com.cboe.infrastructureServices.traderService.DirectoryQueryResult;
import com.cboe.server.util.ObjectResolver;

public class TradeMatchServiceHomeProxyImpl
    extends BOHome
    implements TradeMatchServiceHome
{
    protected TradeMatchServiceProxy service;


    public TradeMatchService create()
    {
        return find();
    }

    public TradeMatchService find()
    {
        if (service == null)
        {
            String serviceTypeName = GenericName.getGenericName(TradeMatchServiceHelper.id(), ':');
            FoundationFramework ff = FoundationFramework.getInstance();
            DirectoryQueryResult[] responses = ff.getTraderService().queryDirectory(serviceTypeName, getConstraint());

            if (responses.length == 0)
            {
                throw new RuntimeException("Could not find TradeMatchService!"); // (indicates a configuration problem)
            }
            else if (responses.length > 1)
            {
                Log.alarm(this, "Found " + responses.length + " exchange services, using first one");
            }

            com.cboe.ffidl.ffBusinessServices.TradeMatchService corbaService = (com.cboe.ffidl.ffBusinessServices.TradeMatchService) ObjectResolver.resolveObject(responses[0].getObjectReference(), TradeMatchServiceHelper.class.getName() );
            service = new TradeMatchServiceProxy(corbaService);
            service.create("TradeMatchServiceProxy");
            addToContainer(service);
        }
        return service;
    }

    protected String getConstraint()
    {
        return "routename == " + RouteNameHelper.getRemoteRouteName();
    }
}
