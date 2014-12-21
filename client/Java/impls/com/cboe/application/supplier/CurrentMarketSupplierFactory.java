package com.cboe.application.supplier;

import com.cboe.interfaces.domain.session.BaseSessionManager;

/**
 * Creates and returns a singleton instance of the CurrentMarketSupplier on the CAS.
 *
 * @author Derek T. Chambers-Boucher
 * @version 06/28/1999
 */

public class CurrentMarketSupplierFactory
{
    private static UserSupplierHelper userSupplierHelper;
    /**
     * CurrentMarketSupplierFactory constructor comment.
     */
    public CurrentMarketSupplierFactory()
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
     * This method returns the singleton instance of the CurrentMarketSupplier or
     * creates it if it has not been instantiated yet.
     */
    public synchronized static CurrentMarketSupplier create(BaseSessionManager sessionManager)
    {
        CurrentMarketSupplier currentMarketSupplier = (CurrentMarketSupplier)getHelper().findSupplier( sessionManager );
        if ( currentMarketSupplier == null )
        {
        // Configuration service will eventually supply the initial hash table size
          currentMarketSupplier = new CurrentMarketSupplier(sessionManager);
          getHelper().addSupplier( currentMarketSupplier, sessionManager );
        }
        return currentMarketSupplier;
    }

    public static CurrentMarketSupplier find(BaseSessionManager sessionManager)
    {
        return create(sessionManager);
    }

    public synchronized static void remove( BaseSessionManager session )
    {
        getHelper().removeSupplier( session );
    }
}
