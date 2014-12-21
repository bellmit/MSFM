package com.cboe.application.supplier;

import com.cboe.interfaces.domain.session.BaseSessionManager;

/**
 * Creates and returns a singleton instance of the TickerSupplier on the CAS.
 *
 * @author Keith A. Korecky
 * @version 06/28/1999
 */

public class TickerSupplierFactory
{
    private static UserSupplierHelper userSupplierHelper;

    /**
     * TickerSupplierFactory constructor comment.
     */
    public TickerSupplierFactory()
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
    public synchronized static TickerSupplier create(BaseSessionManager sessionManager)
    {
        TickerSupplier tickerSupplier = (TickerSupplier)getHelper().findSupplier( sessionManager );
        if ( tickerSupplier == null )
        {
        // Configuration service will eventually supply the initial hash table size
          tickerSupplier = new TickerSupplier(sessionManager);
          getHelper().addSupplier( tickerSupplier, sessionManager );
        }
        return tickerSupplier;
    }

    public static TickerSupplier find(BaseSessionManager sessionManager)
    {
        return create(sessionManager);
    }

    public synchronized static void remove( BaseSessionManager session )
    {
        getHelper().removeSupplier( session );
    }
}
