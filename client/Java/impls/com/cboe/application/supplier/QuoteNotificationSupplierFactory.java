package com.cboe.application.supplier;

/**
 * Creates and returns a singleton instance of the QuoteNotificationSupplier on the CAS.
 *
 * @author William Wei
 */

public class QuoteNotificationSupplierFactory
{
    private static QuoteNotificationSupplier quoteLockSupplier = null;

    /**
     * QuoteNotificationSupplierFactory constructor comment.
     */
    public QuoteNotificationSupplierFactory()
    {
        super();
    }

    /**
     * This method returns the singleton instance of the QuoteNotificationSupplier or
     * creates it if it has not been instantiated yet.
     */
    public synchronized static QuoteNotificationSupplier create()
    {
        if (quoteLockSupplier == null)
        {
            // Configuration service will eventually supply the initial hash table size
            quoteLockSupplier = new QuoteNotificationSupplier();
        };
        return quoteLockSupplier;
    }

    public static QuoteNotificationSupplier find()
    {
        return create();
    }
}
