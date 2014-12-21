package com.cboe.application.supplier;

import com.cboe.interfaces.application.*;

/**
 * Creates and returns an instance of the OrderStatusCollectorSupplier on the CAS for
 * the requesting user.
 *
 * @author Keith A. Korecky
 */

public class OrderStatusCollectorSupplierFactory {

    private static OrderStatusCollectorSupplier supplier = null;

    /**
     * OrderStatusCollectorSupplierFactory default constructor
     */
    public OrderStatusCollectorSupplierFactory()
    {
        super();
    }

    /**
     * This method returns the singleton instance of the QuoteStatusSupplier or
     * creates it if it has not been instantiated yet.
     */
    public synchronized static OrderStatusCollectorSupplier create()
    {
        if (supplier == null)
        {
            // Configuration service will eventually supply the initial hash table size
            supplier = new OrderStatusCollectorSupplier();
        };
        return supplier;
    }

    public static OrderStatusCollectorSupplier find()
    {
        return create();
    }
}
