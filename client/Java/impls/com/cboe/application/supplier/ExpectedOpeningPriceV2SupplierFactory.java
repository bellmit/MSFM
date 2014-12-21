package com.cboe.application.supplier;

import com.cboe.interfaces.domain.session.BaseSessionManager;

/**
 * Creates and returns a singleton instance of the ExpectedOpeningPriceSupplier on the CAS.
 *
 * @author Keith A. Korecky
 * @version 06/29/1999
 */

public class ExpectedOpeningPriceV2SupplierFactory
{
    private static UserSupplierHelper userSupplierHelper;

    /**
     * ExpectedOpeningPriceSupplierFactory constructor comment.
     */
    public ExpectedOpeningPriceV2SupplierFactory()
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
     * This method returns the singleton instance of the ExpectedOpeningPriceSupplier or
     * creates it if it has not been instantiated yet.
     */
    public synchronized static ExpectedOpeningPriceV2Supplier create(BaseSessionManager sessionManager)
    {
        ExpectedOpeningPriceV2Supplier expectedOpeningPriceV2Supplier = (ExpectedOpeningPriceV2Supplier)getHelper().findSupplier( sessionManager );
        if ( expectedOpeningPriceV2Supplier == null )
        {
        // Configuration service will eventually supply the initial hash table size
          expectedOpeningPriceV2Supplier = new ExpectedOpeningPriceV2Supplier(sessionManager);
          getHelper().addSupplier( expectedOpeningPriceV2Supplier, sessionManager );
        }
        return expectedOpeningPriceV2Supplier;
    }

    public static ExpectedOpeningPriceV2Supplier find(BaseSessionManager sessionManager)
    {
        return create(sessionManager);
    }

    public synchronized static void remove( BaseSessionManager session )
    {
        getHelper().removeSupplier( session );
    }
}
