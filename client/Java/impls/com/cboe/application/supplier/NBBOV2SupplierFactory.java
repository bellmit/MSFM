package com.cboe.application.supplier;

import com.cboe.interfaces.domain.session.BaseSessionManager;

/**
 * Creates and returns a singleton instance of the CurrentMarketSupplier on the CAS.
 *
 * @author Derek T. Chambers-Boucher
 * @author Jimmy Wang
 * @version 06/07/2000
 */

public class NBBOV2SupplierFactory
{
    private static UserSupplierHelper userSupplierHelper;

    /**
     * CurrentMarketSupplierFactory constructor comment.
     */
    public NBBOV2SupplierFactory()
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
     * This method returns the singleton instance of the NBBOSupplier or
     * creates it if it has not been instantiated yet.
     */
    public synchronized static NBBOV2Supplier create(BaseSessionManager sessionManager)
    {
        NBBOV2Supplier nbboV2Supplier = (NBBOV2Supplier)getHelper().findSupplier( sessionManager );
        if ( nbboV2Supplier == null )
        {
        // Configuration service will eventually supply the initial hash table size
          nbboV2Supplier = new NBBOV2Supplier(sessionManager);
          getHelper().addSupplier( nbboV2Supplier, sessionManager );
        }
        return nbboV2Supplier;
    }

    public static NBBOV2Supplier find(BaseSessionManager sessionManager)
    {
        return create(sessionManager);
    }

    public synchronized static void remove( BaseSessionManager session )
    {
        getHelper().removeSupplier( session );
    }
}
