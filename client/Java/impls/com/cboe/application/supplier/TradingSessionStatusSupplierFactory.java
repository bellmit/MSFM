package com.cboe.application.supplier;

/**
 * Creates and returns a singleton instance of the TradingSessionStatusSupplier on the CAS.
 *
 * @author Keith A. Korecky
 * @version 06/28/1999
 */

public class TradingSessionStatusSupplierFactory
{
    private static TradingSessionStatusSupplier tradingSessionStatusSupplier = null;

    /**
     * TradingSessionStatusSupplierFactory constructor comment.
     */
    public TradingSessionStatusSupplierFactory()
    {
        super();
    }

    /**
     * This method returns the singleton instance of the TradingSessionStatusSupplier or
     * creates it if it has not been instantiated yet.
     */
    public synchronized static TradingSessionStatusSupplier create()
    {
        if (tradingSessionStatusSupplier == null)
        {
            // Configuration service will eventually supply the initial hash table size
            tradingSessionStatusSupplier = new TradingSessionStatusSupplier();
        };
        return tradingSessionStatusSupplier;
    }

    public static TradingSessionStatusSupplier find()
    {
        return create();
    }
}
