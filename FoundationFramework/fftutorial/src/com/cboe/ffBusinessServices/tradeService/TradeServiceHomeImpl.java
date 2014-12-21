package com.cboe.ffBusinessServices.tradeService;

import com.cboe.ffidl.ffBusinessServices.POA_TradeService_tie;
import com.cboe.ffidl.ffBusinessServices.TradeServiceHelper;
import com.cboe.ffBusinessServices.BusinessServiceHome;
import com.cboe.ffInterfaces.TradeService;
import com.cboe.ffInterfaces.TradeServiceHome;
import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import java.io.*;

public class TradeServiceHomeImpl
    extends BusinessServiceHome
    implements TradeServiceHome
{
    protected TradeServiceImpl serviceImpl;
    protected TradeService     service; // (may be serviceImpl or may be an interceptor for serviceImpl)

    public TradeService create()
    {
        return find();
    }

    public TradeService find()
    {
        return service;
    }

    public void goMaster(boolean failover)
    {
        export(TradeServiceHelper.id());
    }

    public void goSlave()
    {
        create();
        POA_TradeService_tie tie = new POA_TradeService_tie(service);
        connectToPOA(tie);
    }

    public synchronized void initialize()
    {
        setExportWithRouteName(true);
        if (serviceImpl == null)
        {
            serviceImpl = new TradeServiceImpl();
            addToContainer(serviceImpl);
            serviceImpl.create("TradeService");
            try
            {
                // (Attempts to create interceptor defined in HomeDescriptor (via XML))
                //
                service = (TradeService)initializeObject(serviceImpl);
            }
            catch (RuntimeException ex)
            {
                throw ex;
            }
            catch (Exception ex)
            {
                Log.exception(this, "Failed to create interceptor", ex);
                service = serviceImpl;
            }
        }
    }
}
