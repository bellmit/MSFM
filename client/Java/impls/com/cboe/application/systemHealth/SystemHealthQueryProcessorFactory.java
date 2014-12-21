package com.cboe.application.systemHealth;

import com.cboe.interfaces.application.SystemHealthQueryProcessor;

public final class SystemHealthQueryProcessorFactory
{
    public static void initialize(boolean allowContextDetailQuery, boolean allowConfigurationDataQuery, boolean allowProductQuery)
    {
        if(allowContextDetailQuery)
        {
            ContextDetailQueryProcessorImpl.initialize();
        }

        if(allowProductQuery)
        {
            ProductQueryProcessorImpl.initialize();
        }

        if(allowConfigurationDataQuery)
        {
            ConfigurationQueryProcessorImpl.initialize();
        }
    }

    public static SystemHealthQueryProcessor getContextDetailRequestProcessor(String xmlInput)
    {
        return new ContextDetailQueryProcessorImpl(xmlInput);
    }

    public static SystemHealthQueryProcessor getProductQueryRequestProcessor(String xmlInput)
    {
        return new ProductQueryProcessorImpl(xmlInput);
    }

    public static SystemHealthQueryProcessor getConfigurationRequestProcessor(String xmlInput)
    {
        return new ConfigurationQueryProcessorImpl(xmlInput);
    }
}
