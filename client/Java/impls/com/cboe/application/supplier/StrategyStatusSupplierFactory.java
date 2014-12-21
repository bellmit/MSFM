package com.cboe.application.supplier;

/**
 * Creates and returns a singleton instance of the StrategyStatusSupplier on the CAS.
 *
 * @author Derek T. Chambers-Boucher
 * @version 03/24/1999
 */

public class StrategyStatusSupplierFactory
{
    private static StrategyStatusSupplier strategyStatusSupplier = null;

    /**
     * StrategyStatusSupplierFactory constructor comment.
     */
    public StrategyStatusSupplierFactory()
    {
        super();
    }

    /**
     * This method returns the singleton instance of the StrategyStatusSupplier or
     * creates it if it has not been instantiated yet.
     */
    public synchronized static StrategyStatusSupplier create()
    {
        if (strategyStatusSupplier == null)
        {
            // Configuration service will eventually supply the initial hash table size
            strategyStatusSupplier = new StrategyStatusSupplier();
        };
        return strategyStatusSupplier;
    }

    public static StrategyStatusSupplier find()
    {
        return create();
    }
}
