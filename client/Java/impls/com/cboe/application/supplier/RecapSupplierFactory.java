package com.cboe.application.supplier;

import com.cboe.interfaces.domain.session.BaseSessionManager;

/**
 * Creates and returns a singleton instance of the <>Supplier on the CAS.
 *
 * @author Keith A. Korecky
 * @version 06/28/1999
 */

public class RecapSupplierFactory
{
    private static UserSupplierHelper userSupplierHelper;

    /**
     * RecapSupplierFactory constructor comment.
     */
    public RecapSupplierFactory()
    {
        super();
    }

    private static UserSupplierHelper getHelper() {
        if ( userSupplierHelper == null ) {
            userSupplierHelper = new UserSupplierHelper();
        }
        return userSupplierHelper;
   }

    /**
     * This method returns the singleton instance of the RecapSupplier or
     * creates it if it has not been instantiated yet.
     */
    public synchronized static RecapSupplier create(BaseSessionManager sessionManager)
    {
        RecapSupplier recapSupplier = (RecapSupplier)getHelper().findSupplier( sessionManager );
        if ( recapSupplier == null )
        {
        // Configuration service will eventually supply the initial hash table size
          recapSupplier = new RecapSupplier(sessionManager);
          getHelper().addSupplier( recapSupplier, sessionManager );
        }
        return recapSupplier;
    }

    public static RecapSupplier find(BaseSessionManager sessionManager)
    {
        return create(sessionManager);
    }

    public synchronized static void remove( BaseSessionManager session )
    {
        getHelper().removeSupplier( session );
    }
}
