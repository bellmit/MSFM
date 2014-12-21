package com.cboe.ffBusinessServices.tradeService;

import com.cboe.ffidl.ffBusinessServices.POA_TradeService_tie;
import com.cboe.ffidl.ffBusinessServices.TradeServiceHelper;
import com.cboe.ffBusinessServices.BusinessServiceHome;
import com.cboe.ffInterfaces.TradeService;
import com.cboe.ffInterfaces.TradeServiceHome;
import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import java.io.*;

public class TradeServiceHomeSimpleImpl
    extends TradeServiceHomeImpl
    implements TradeServiceHome
{
    public synchronized void initialize()
    {
        setExportWithRouteName(true);
        if (serviceImpl == null)
        {
            serviceImpl = new TradeServiceSimpleImpl();
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
