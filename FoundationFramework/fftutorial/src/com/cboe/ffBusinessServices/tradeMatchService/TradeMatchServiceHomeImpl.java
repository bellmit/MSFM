package com.cboe.ffBusinessServices.tradeMatchService; 

import com.cboe.ffidl.ffBusinessServices.POA_TradeMatchService_tie;
import com.cboe.ffidl.ffBusinessServices.TradeMatchServiceHelper;
import com.cboe.ffInterfaces.TradeMatchService;
import com.cboe.ffInterfaces.TradeMatchServiceHome;
import com.cboe.ffBusinessServices.BusinessServiceHome;
import com.cboe.infrastructureServices.foundationFramework.BOHome;

public class TradeMatchServiceHomeImpl
    extends BusinessServiceHome
    implements TradeMatchServiceHome
{
    protected TradeMatchServiceImpl serviceImpl;

    public TradeMatchService create()
    {
        return find();
    }

    public TradeMatchService find()
    {
        return serviceImpl;
    }

    public void goMaster(boolean failover)
    {
        export(TradeMatchServiceHelper.id());
    }

    public void goSlave()
    {
        create();
        POA_TradeMatchService_tie tie = new POA_TradeMatchService_tie(serviceImpl);
        connectToPOA(tie);
    }

    public synchronized void initialize()
    {
        setExportWithRouteName(true);
        if (serviceImpl == null)
        {
            serviceImpl = new TradeMatchServiceImpl();
            serviceImpl.create("TradeMatcherService");
            addToContainer(serviceImpl);
            serviceImpl.registerCallbacks();
        }
    }

}
