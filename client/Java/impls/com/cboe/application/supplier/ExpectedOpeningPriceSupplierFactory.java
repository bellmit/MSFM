package com.cboe.application.supplier;

import com.cboe.interfaces.domain.session.BaseSessionManager;

/**
 * Creates and returns a singleton instance of the ExpectedOpeningPriceSupplier on the CAS.
 *
 * @author Keith A. Korecky
 * @version 06/29/1999
 */

public class ExpectedOpeningPriceSupplierFactory
{
    private static UserSupplierHelper userSupplierHelper;

    /**
     * ExpectedOpeningPriceSupplierFactory constructor comment.
     */
    public ExpectedOpeningPriceSupplierFactory()
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
    public synchronized static ExpectedOpeningPriceSupplier create(BaseSessionManager sessionManager)
    {
        ExpectedOpeningPriceSupplier expectedOpeningPriceSupplier = (ExpectedOpeningPriceSupplier)getHelper().findSupplier( sessionManager );
        if ( expectedOpeningPriceSupplier == null )
        {
        // Configuration service will eventually supply the initial hash table size
          expectedOpeningPriceSupplier = new ExpectedOpeningPriceSupplier(sessionManager);
          getHelper().addSupplier( expectedOpeningPriceSupplier, sessionManager );
        }
        return expectedOpeningPriceSupplier;
    }

    public static ExpectedOpeningPriceSupplier find(BaseSessionManager sessionManager)
    {
        return create(sessionManager);
    }

    public synchronized static void remove( BaseSessionManager session )
    {
        getHelper().removeSupplier( session );
    }
}
