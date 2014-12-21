package com.cboe.application.supplier;


/**
 * Creates and returns an instance of the RFQSupplier on the CAS for the
 * requesting user.
 *
 * @author Derek T. Chambers-Boucher
 * @version 06/30/1999
 */

public class RFQV2SupplierFactory
{

   private static RFQV2Supplier rfqV2Supplier = null;


    /**
     * RFQSupplierFactory constructor comment.
     */
    public RFQV2SupplierFactory()
    {
        super();
    }



    /**
     * This method returns the singleton instance of the RFQSupplier or
     * creates it if it has not been instantiated yet.
     */
    public synchronized static RFQV2Supplier create()
    {
        if ( rfqV2Supplier == null )
        {
        // Configuration service will eventually supply the initial hash table size
          rfqV2Supplier = new RFQV2Supplier();
        }

        return rfqV2Supplier;
    }

    public static RFQV2Supplier find()
    {
        return create();
    }

}
