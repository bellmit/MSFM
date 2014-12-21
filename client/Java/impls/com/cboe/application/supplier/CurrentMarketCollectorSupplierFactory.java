package com.cboe.application.supplier;

import com.cboe.interfaces.application.*;
/**
 * Creates and returns an instance of the CurrentMarketCollectorSupplier on the CAS for
 * the requesting user.
 *
 * @author Keith A. Korecky
 */

public class CurrentMarketCollectorSupplierFactory {

    private static CurrentMarketCollectorSupplier currentMarketCollectorSupplier = null;

    /**
     * CurrentMarketCollectorSupplierFactory default constructor
     */
    public CurrentMarketCollectorSupplierFactory()
    {
        super();
    }

    /**
     * This method returns the singleton instance of the QuoteStatusSupplier or
     * creates it if it has not been instantiated yet.
     */
    public synchronized static CurrentMarketCollectorSupplier create( )
    {
        if (currentMarketCollectorSupplier == null)
        {
            // Configuration service will eventually supply the initial hash table size
            currentMarketCollectorSupplier = new CurrentMarketCollectorSupplier();
        };
        return currentMarketCollectorSupplier;
    }

    public static CurrentMarketCollectorSupplier find( )
    {
        return create( );
    }
}
