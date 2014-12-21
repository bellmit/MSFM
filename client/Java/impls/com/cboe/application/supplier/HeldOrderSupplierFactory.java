/*
 * Created by IntelliJ IDEA.
 * User: huange
 * Date: Oct 4, 2002
 * Time: 4:16:00 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.cboe.application.supplier;

public class HeldOrderSupplierFactory
{
    private static HeldOrderSupplier heldOrderSupplier = null;


    /**
     * HeldOrderSupplierFactory constructor comment.
     */
    public HeldOrderSupplierFactory()
    {
        super();
    }



    /**
     * This method returns the singleton instance of the HeldOrderSupplier or
     * creates it if it has not been instantiated yet.
     */
    public synchronized static HeldOrderSupplier create()
    {
        if ( heldOrderSupplier == null )
        {
        // Configuration service will eventually supply the initial hash table size
          heldOrderSupplier = new HeldOrderSupplier();
        }

        return heldOrderSupplier;
    }

    public static HeldOrderSupplier find()
    {
        return create();
    }

}
