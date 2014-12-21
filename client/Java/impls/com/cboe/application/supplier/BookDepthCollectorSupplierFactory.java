package com.cboe.application.supplier;

import com.cboe.interfaces.application.*;
/**
 * Creates and returns an instance of the BookDepthCollectorSupplier on the CAS for
 * the requesting user.
 *
 * @author William Wei
 */

public class BookDepthCollectorSupplierFactory {

    private static BookDepthCollectorSupplier bookDepthCollectorSupplier = null;

    /**
     * BookDepthCollectorSupplierFactory default constructor
     */
    public BookDepthCollectorSupplierFactory()
    {
        super();
    }

    /**
     * This method returns the singleton instance of the QuoteStatusSupplier or
     * creates it if it has not been instantiated yet.
     */
    public synchronized static BookDepthCollectorSupplier create( )
    {
        if (bookDepthCollectorSupplier == null)
        {
            // Configuration service will eventually supply the initial hash table size
            bookDepthCollectorSupplier = new BookDepthCollectorSupplier();
        };
        return bookDepthCollectorSupplier;
    }

    public static BookDepthCollectorSupplier find( )
    {
        return create( );
    }
}
