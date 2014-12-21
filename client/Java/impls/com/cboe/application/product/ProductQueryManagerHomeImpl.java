package com.cboe.application.product;

import com.cboe.domain.startup.ClientBOHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.ProductQueryManager;
import com.cboe.interfaces.application.ProductQueryManagerHome;
import com.cboe.interfaces.application.SessionManager;

/**
 * An implementation of CachedProductQueryManagerHome for use in the process that implements
 * the cas product query service.
 *
 * @author Brian Erst
 */
public class ProductQueryManagerHomeImpl extends ClientBOHome implements ProductQueryManagerHome
{
    /**
     * ProductQueryManagerFactory constructor.
     */
    public ProductQueryManagerHomeImpl()
    {
        super();
    }

    /**
    * Creates an instance of the product query manager.
    *
    * @author Connie Feng
    */
    public ProductQueryManager create(SessionManager theSession)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Creating ProductQueryManagerImpl for " + theSession);
        }
        ProductQueryManagerImpl bo = new ProductQueryManagerImpl();
        bo.setSessionManager(theSession);

        bo.create(String.valueOf(bo.hashCode()));

        // Every BObject must be added to the container.
        addToContainer(bo);

        //The addToContainer call MUST occur prior to creation of the interceptor.
        ProductQueryManagerInterceptor boi = null;
        try {
            boi = (ProductQueryManagerInterceptor) this.createInterceptor( bo );
            boi.setSessionManager(theSession);
            if(getInstrumentationEnablementProperty())
            {
                boi.startInstrumentation(getInstrumentationProperty());
            }
        } catch (Exception ex) {
            Log.alarm(this,"Failed to create interceptor");
        }

        return boi;
    }
}
