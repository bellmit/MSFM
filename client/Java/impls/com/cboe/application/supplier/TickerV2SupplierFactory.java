package com.cboe.application.supplier;

import com.cboe.interfaces.domain.session.BaseSessionManager;

/**
 * Creates and returns a singleton instance of the TickerSupplier on the CAS.
 *
 * @author Keith A. Korecky
 * @version 06/28/1999
 */

public class TickerV2SupplierFactory
{
    private static UserSupplierHelper userSupplierHelper;

    /**
     * TickerSupplierFactory constructor comment.
     */
    public TickerV2SupplierFactory()
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
     * This method returns the singleton instance of the TickerSupplier or
     * creates it if it has not been instantiated yet.
     */
    public synchronized static TickerV2Supplier create(BaseSessionManager sessionManager)
    {
        TickerV2Supplier tickerV2Supplier = (TickerV2Supplier)getHelper().findSupplier( sessionManager );
        if ( tickerV2Supplier == null )
        {
        // Configuration service will eventually supply the initial hash table size
          tickerV2Supplier = new TickerV2Supplier(sessionManager);
          getHelper().addSupplier( tickerV2Supplier, sessionManager );
        }
        return tickerV2Supplier;
    }

    public static TickerV2Supplier find(BaseSessionManager sessionManager)
    {
        return create(sessionManager);
    }

    public synchronized static void remove( BaseSessionManager session )
    {
        getHelper().removeSupplier( session );
    }
}
