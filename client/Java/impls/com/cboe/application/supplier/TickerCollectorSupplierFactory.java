package com.cboe.application.supplier;

import com.cboe.interfaces.application.*;
/**
 * Creates and returns an instance of the TickerCollectorSupplier on the CAS for
 * the requesting user.
 *
 * @author Keith A. Korecky
 */

public class TickerCollectorSupplierFactory {

    private static TickerCollectorSupplier tickerCollectorSupplier = null;

    /**
     * TickerCollectorSupplierFactory default constructor
     */
    public TickerCollectorSupplierFactory()
    {
        super();
    }

    /**
     * This method returns the singleton instance of the QuoteStatusSupplier or
     * creates it if it has not been instantiated yet.
     */
    public synchronized static TickerCollectorSupplier create( )
    {
        if (tickerCollectorSupplier == null)
        {
            // Configuration service will eventually supply the initial hash table size
            tickerCollectorSupplier = new TickerCollectorSupplier();
        };
        return tickerCollectorSupplier;
    }

    public static TickerCollectorSupplier find( )
    {
        return create( );
    }
}
