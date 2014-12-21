package com.cboe.application.quote;

import com.cboe.infrastructureServices.foundationFramework.utilities.*;
import com.cboe.interfaces.application.QuoteCacheControlService;
import com.cboe.interfaces.application.QuoteCacheControlServiceHome;
import com.cboe.domain.startup.ClientBOHome;

public class QuoteCacheControlServiceHomeImpl extends ClientBOHome implements QuoteCacheControlServiceHome
{
    // Note that if we ever expose this outside we need an interceptor too.
    private QuoteCacheControlServiceImpl service;
    
    public void clientInitialize() throws Exception
    {
        super.clientInitialize();
        create();
    }
    
    public void clientStart() throws Exception
    {
        service.start();
    }
    
    public QuoteCacheControlService create()
    {
        if(service == null)
        {
            if(Log.isDebugOn())
            {
                Log.debug(this, "Creating QuoteCacheControlService.");
            }
    
            service = new QuoteCacheControlServiceImpl();
    
            //Every BObject create MUST have a name...if the object is to be a managed object.
            service.create("QuoteCacheControlService");
            service.initialize();
    
            //Every bo object must be added to the container.
            addToContainer(service);
        }
        return service;
    }

    public QuoteCacheControlService find()
    {
        if(service == null)
        {
            return create();
        }
        
        return service;
    }
}
