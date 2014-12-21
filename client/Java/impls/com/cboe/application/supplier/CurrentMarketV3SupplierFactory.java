package com.cboe.application.supplier;

import com.cboe.interfaces.domain.session.BaseSessionManager;

/**
 * Creates and returns a singleton instance of the CurrentMarketSupplier on the CAS.
 *
 * @author Derek T. Chambers-Boucher
 * @version 06/28/1999
 */

public class CurrentMarketV3SupplierFactory
{
    private static UserSupplierHelper userSupplierHelper;

    /**
     * CurrentMarketSupplierFactory constructor comment.
     */
    public CurrentMarketV3SupplierFactory()
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
    public synchronized static CurrentMarketV3Supplier create(BaseSessionManager sessionManager)
    {
        CurrentMarketV3Supplier currentMarketV3Supplier = (CurrentMarketV3Supplier)getHelper().findSupplier( sessionManager );
        if ( currentMarketV3Supplier == null )
        {
        // Configuration service will eventually supply the initial hash table size
          currentMarketV3Supplier = new CurrentMarketV3Supplier(sessionManager);
          getHelper().addSupplier( currentMarketV3Supplier, sessionManager );
        }
        return currentMarketV3Supplier;
    }

    public static CurrentMarketV3Supplier find(BaseSessionManager sessionManager)
    {
        return create(sessionManager);
    }

    public synchronized static void remove( BaseSessionManager session )
    {
        getHelper().removeSupplier( session );
    }
}
