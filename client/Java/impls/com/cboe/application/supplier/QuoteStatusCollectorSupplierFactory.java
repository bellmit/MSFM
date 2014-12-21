package com.cboe.application.supplier;

import com.cboe.interfaces.application.*;
/**
 * Creates and returns an instance of the QuoteStatusCollectorSupplier on the CAS for
 * the requesting user.
 *
 * @author Keith A. Korecky
 */

public class QuoteStatusCollectorSupplierFactory {

    private static QuoteStatusCollectorSupplier supplier = null;

    /**
     * OrderStatusCollectorSupplierFactory default constructor
     */
    public QuoteStatusCollectorSupplierFactory()
    {
        super();
    }

    /**
     * This method returns the singleton instance of the QuoteStatusSupplier or
     * creates it if it has not been instantiated yet.
     */
    public synchronized static QuoteStatusCollectorSupplier create()
    {
        if (supplier == null)
        {
            // Configuration service will eventually supply the initial hash table size
            supplier = new QuoteStatusCollectorSupplier();
        };
        return supplier;
    }

    public static QuoteStatusCollectorSupplier find()
    {
        return create();
    }
}
