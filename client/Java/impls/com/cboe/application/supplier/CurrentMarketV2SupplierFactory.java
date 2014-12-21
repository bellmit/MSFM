package com.cboe.application.supplier;

import com.cboe.interfaces.domain.session.BaseSessionManager;

/**
 * Creates and returns a singleton instance of the CurrentMarketSupplier on the CAS.
 *
 * @author Derek T. Chambers-Boucher
 * @version 06/28/1999
 */

public class CurrentMarketV2SupplierFactory
{
    private static UserSupplierHelper userSupplierHelper;

    /**
     * CurrentMarketSupplierFactory constructor comment.
     */
    public CurrentMarketV2SupplierFactory()
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
    public synchronized static CurrentMarketV2Supplier create(BaseSessionManager sessionManager)
    {
        CurrentMarketV2Supplier currentMarketV2Supplier = (CurrentMarketV2Supplier)getHelper().findSupplier( sessionManager );
        if ( currentMarketV2Supplier == null )
        {
        // Configuration service will eventually supply the initial hash table size
          currentMarketV2Supplier = new CurrentMarketV2Supplier(sessionManager);
          getHelper().addSupplier( currentMarketV2Supplier, sessionManager );
        }
        return currentMarketV2Supplier;
    }

    public static CurrentMarketV2Supplier find(BaseSessionManager sessionManager)
    {
        return create(sessionManager);
    }

    public synchronized static void remove( BaseSessionManager session )
    {
        getHelper().removeSupplier( session );
    }
}
