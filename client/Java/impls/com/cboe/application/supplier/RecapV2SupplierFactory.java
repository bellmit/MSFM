package com.cboe.application.supplier;

import com.cboe.interfaces.domain.session.BaseSessionManager;

/**
 * Creates and returns a singleton instance of the <>Supplier on the CAS.
 *
 * @author Keith A. Korecky
 * @version 06/28/1999
 */

public class RecapV2SupplierFactory
{
    private static UserSupplierHelper userSupplierHelper;

    /**
     * RecapSupplierFactory constructor comment.
     */
    public RecapV2SupplierFactory()
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
    public synchronized static RecapV2Supplier create(BaseSessionManager sessionManager)
    {
        RecapV2Supplier recapV2Supplier = (RecapV2Supplier)getHelper().findSupplier( sessionManager );
        if ( recapV2Supplier == null )
        {
        // Configuration service will eventually supply the initial hash table size
          recapV2Supplier = new RecapV2Supplier(sessionManager);
          getHelper().addSupplier( recapV2Supplier, sessionManager );
        }
        return recapV2Supplier;
    }

    public static RecapV2Supplier find(BaseSessionManager sessionManager)
    {
        return create(sessionManager);
    }

    public synchronized static void remove( BaseSessionManager session )
    {
        getHelper().removeSupplier( session );
    }
}
