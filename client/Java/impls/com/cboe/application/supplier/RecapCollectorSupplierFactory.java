package com.cboe.application.supplier;

import com.cboe.interfaces.application.*;
/**
 * Creates and returns an instance of the RecapCollectorSupplier on the CAS for
 * the requesting user.
 *
 * @author Keith A. Korecky
 */

public class RecapCollectorSupplierFactory {

    private static RecapCollectorSupplier recapCollectorSupplier = null;

    /**
     * RecapCollectorSupplierFactory default constructor
     */
    public RecapCollectorSupplierFactory()
    {
        super();
    }

    /**
     * This method returns the singleton instance of the QuoteStatusSupplier or
     * creates it if it has not been instantiated yet.
     */
    public synchronized static RecapCollectorSupplier create( )
    {
        if (recapCollectorSupplier == null)
        {
            // Configuration service will eventually supply the initial hash table size
            recapCollectorSupplier = new RecapCollectorSupplier();
        };
        return recapCollectorSupplier;
    }

    public static RecapCollectorSupplier find( )
    {
        return create( );
    }
}
