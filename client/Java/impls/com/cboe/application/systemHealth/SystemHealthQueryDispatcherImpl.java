package com.cboe.application.systemHealth;

import com.cboe.interfaces.application.SystemHealthQueryDispatcher;
import com.cboe.interfaces.application.SystemHealthQueryProcessor;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

public class SystemHealthQueryDispatcherImpl extends BObject implements SystemHealthQueryDispatcher
{
    public String getContextDetail(String xmlInput)
    {
        if (Log.isDebugOn()) {
            Log.debug(this, "received context detail query ->"+xmlInput);
        }
        SystemHealthQueryProcessor processor = SystemHealthQueryProcessorFactory.getContextDetailRequestProcessor(xmlInput);
        return processor.processRequest();
    }

    public String getProductData(String xmlInput)
    {
        if (Log.isDebugOn()) {
            Log.debug(this, "received product data query ->"+xmlInput);
        }
        SystemHealthQueryProcessor processor = SystemHealthQueryProcessorFactory.getProductQueryRequestProcessor(xmlInput);
        return processor.processRequest();
    }

    public String getConfigurationData(String xmlInput)
    {
        if (Log.isDebugOn()) {
            Log.debug(this, "received configuration data query ->"+xmlInput);
        }
        SystemHealthQueryProcessor processor = SystemHealthQueryProcessorFactory.getConfigurationRequestProcessor(xmlInput);
        return processor.processRequest();
    }

}
