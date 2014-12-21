package com.cboe.application.productConfiguration;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.SessionManager;
import com.cboe.interfaces.application.ProductConfigurationQueryService;
import com.cboe.interfaces.application.ProductConfigurationQueryServiceHome;
import com.cboe.domain.startup.ClientBOHome;

public class ProductConfigurationQueryServiceHomeImpl extends ClientBOHome implements ProductConfigurationQueryServiceHome
{
    public ProductConfigurationQueryServiceHomeImpl()
    {
        super();
    }

    /**
    * Creates an instance of ProductConfigurationQueryService for the current session.
    */
    public ProductConfigurationQueryService create(SessionManager theSession)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Creating ProductConfigurationQueryServiceHomeImpl for " + theSession);
        }
        ProductConfigurationQueryServiceImpl bo = new ProductConfigurationQueryServiceImpl(theSession);

        // Every BObject must be added to the container.
        addToContainer(bo);

        // Every BObject create MUST have a name...if the object is to be a managed object.
        bo.create(String.valueOf(bo.hashCode()));

        //The addToContainer call MUST occur prior to creation of the interceptor.
        ProductConfigurationQueryServiceInterceptor boi = null;
        try
        {
            boi = (ProductConfigurationQueryServiceInterceptor) this.createInterceptor( bo );
            boi.setSessionManager(theSession);
            if(getInstrumentationEnablementProperty())
            {
                boi.startInstrumentation(getInstrumentationProperty());
            }
        }
        catch(Exception ex)
        {
            Log.exception(this, ex);
        }
        
        return boi;
    }

}
