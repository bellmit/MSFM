package com.cboe.application.supplier;


/**
 * Creates and returns an instance of the RFQSupplier on the CAS for the
 * requesting user.
 *
 * @author Derek T. Chambers-Boucher
 * @version 06/30/1999
 */

public class RFQSupplierFactory
{

   private static RFQSupplier rfqSupplier = null;


    /**
     * RFQSupplierFactory constructor comment.
     */
    public RFQSupplierFactory()
    {
        super();
    }



    /**
     * This method returns the singleton instance of the RFQSupplier or
     * creates it if it has not been instantiated yet.
     */
    public synchronized static RFQSupplier create()
    {
        if ( rfqSupplier == null )
        {
        // Configuration service will eventually supply the initial hash table size
          rfqSupplier = new RFQSupplier();
        }

        return rfqSupplier;
    }

    public static RFQSupplier find()
    {
        return create();
    }

}
