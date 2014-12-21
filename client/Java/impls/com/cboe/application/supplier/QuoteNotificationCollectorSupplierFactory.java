package com.cboe.application.supplier;

import com.cboe.interfaces.application.*;
/**
 * Creates and returns an instance of the QuoteNotificationCollectorSupplier on the CAS for
 * the requesting user.
 *
 * @author William Wei
 */

public class QuoteNotificationCollectorSupplierFactory {

    private static QuoteNotificationCollectorSupplier quoteLockCollectorSupplier = null;

    /**
     * QuoteNotificationCollectorSupplierFactory default constructor
     */
    public QuoteNotificationCollectorSupplierFactory()
    {
        super();
    }

    /**
     * This method returns the singleton instance of the QuoteStatusSupplier or
     * creates it if it has not been instantiated yet.
     */
    public synchronized static QuoteNotificationCollectorSupplier create( )
    {
        if (quoteLockCollectorSupplier == null)
        {
            // Configuration service will eventually supply the initial hash table size
            quoteLockCollectorSupplier = new QuoteNotificationCollectorSupplier();
        };
        return quoteLockCollectorSupplier;
    }

    public static QuoteNotificationCollectorSupplier find( )
    {
        return create( );
    }
}
