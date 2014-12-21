package com.cboe.interfaces.events;

import com.cboe.idl.consumers.CacheUpdateConsumerOperations;
import com.cboe.idl.events.CacheUpdateEventConsumerOperations;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.FatalFoundationFrameworkException;

public interface CacheUpdateChannelHome 
{
    public String HOME_NAME = "CacheUpdateChannelHome";    
    public CacheUpdateEventConsumerOperations getPublisher();
    public boolean isRegistered(CacheUpdateConsumerOperations p_consumer);
    public void removeConsumer(CacheUpdateConsumerOperations p_consumer);
    public void registerConsumer(CacheUpdateConsumerOperations p_consumer) 
        throws FatalFoundationFrameworkException;
    public void registerConsumerWithDeferredConsumption(CacheUpdateConsumerOperations p_consumer) 
        throws FatalFoundationFrameworkException;
    public void startConsumption(CacheUpdateConsumerOperations p_consumer) 
        throws FatalFoundationFrameworkException;

}