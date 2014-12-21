package com.cboe.application.supplier;

/** Maintain a singleton AuctionSupplier on the CAS.
 */
public class AuctionSupplierFactory
{
    private static AuctionSupplier auctionSupplier;

    // Nobody creates objects of this factory type.
    private AuctionSupplierFactory()
    { }

    /** Return the instance of the AuctionSupplier for the
     * userSession or create it if it does not exist yet.
     * @return AuctionSupplier for this CAS.
     */
    public synchronized static AuctionSupplier create()
    {
        if (auctionSupplier == null)
        {
            // Configuration service will eventually supply the initial hash table size
            auctionSupplier = new AuctionSupplier();
        }

        return auctionSupplier;
    }

    public static AuctionSupplier find()
    {
        return create();
    }

}
