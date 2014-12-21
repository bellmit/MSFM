package com.cboe.application.supplier;

/**
 * Creates and returns a singleton instance of the ProductStatusSupplier on the CAS.
 *
 * @author Derek T. Chambers-Boucher
 * @version 03/24/1999
 */

public class ProductStatusSupplierFactory
{
    private static ProductStatusSupplier productStatusSupplier = null;

    /**
     * ProductStatusSupplierFactory constructor comment.
     */
    public ProductStatusSupplierFactory()
    {
        super();
    }

    /**
     * This method returns the singleton instance of the ProductStatusSupplier or
     * creates it if it has not been instantiated yet.
     */
    public synchronized static ProductStatusSupplier create()
    {
        if (productStatusSupplier == null)
        {
            // Configuration service will eventually supply the initial hash table size
            productStatusSupplier = new ProductStatusSupplier();
        };
        return productStatusSupplier;
    }

    public static ProductStatusSupplier find()
    {
        return create();
    }
}
