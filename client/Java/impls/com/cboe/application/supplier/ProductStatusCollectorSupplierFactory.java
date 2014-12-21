package com.cboe.application.supplier;

import com.cboe.interfaces.application.*;
/**
 * Creates and returns an instance of the QuoteStatusSupplier on the CAS for
 * the requesting user.
 *
 * @author Derek T. Chambers-Boucher
 * @version 06/30/1999
 */

public class ProductStatusCollectorSupplierFactory {

    private static ProductStatusCollectorSupplier productStatusCollectorSupplier = null;

    /**
     * TradingSessionCacheSupplierFactory default constructor
     */
    public ProductStatusCollectorSupplierFactory()
    {
        super();
    }

    /**
     * This method returns the singleton instance of the QuoteStatusSupplier or
     * creates it if it has not been instantiated yet.
     */
    public synchronized static ProductStatusCollectorSupplier create( )
    {
        if (productStatusCollectorSupplier == null)
        {
            // Configuration service will eventually supply the initial hash table size
            productStatusCollectorSupplier = new ProductStatusCollectorSupplier();
        };
        return productStatusCollectorSupplier;
    }

    public static ProductStatusCollectorSupplier find( )
    {
        return create( );
    }
}
