package com.cboe.application.supplier;

import java.util.*;
/**
 * Creates and returns a singleton instance of the ClassStatusSupplier on the CAS.
 *
 * @author Derek T. Chambers-Boucher
 * @version 06/28/1999
 */

public class ClassStatusSupplierFactory
{
    private static ClassStatusSupplier classStatusSupplier = null;

    /**
     * ClassStatusSupplierFactory constructor comment.
     */
    public ClassStatusSupplierFactory()
    {
        super();
    }

    /**
     * This method returns the singleton instance of the ClassStatusSupplier or
     * creates it if it has not been instantiated yet.
     */
    public synchronized static ClassStatusSupplier create()
    {
        if (classStatusSupplier == null)
        {
            // Configuration service will eventually supply the initial hash table size
            classStatusSupplier = new ClassStatusSupplier();
        };
        return classStatusSupplier;
    }

    public static ClassStatusSupplier find()
    {
        return create();
    }
}
