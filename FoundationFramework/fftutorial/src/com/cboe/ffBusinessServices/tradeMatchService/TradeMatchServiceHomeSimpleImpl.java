package com.cboe.ffBusinessServices.tradeMatchService; 

import com.cboe.ffidl.ffBusinessServices.POA_TradeMatchService_tie;
import com.cboe.ffidl.ffBusinessServices.TradeMatchServiceHelper;
import com.cboe.ffInterfaces.TradeMatchService;
import com.cboe.ffInterfaces.TradeMatchServiceHome;
import com.cboe.ffBusinessServices.BusinessServiceHome;
import com.cboe.infrastructureServices.foundationFramework.BOHome;

public class TradeMatchServiceHomeSimpleImpl
    extends BusinessServiceHome
    implements TradeMatchServiceHome
{
    protected TradeMatchServiceSimpleImpl serviceImpl;

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
    }

    public void goSlave()
    {
        create();
    }

    public synchronized void initialize()
    {
        setExportWithRouteName(false);
        if (serviceImpl == null)
        {
            serviceImpl = new TradeMatchServiceSimpleImpl();
        }
    }
}
