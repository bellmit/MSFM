/*
 * Created by IntelliJ IDEA.
 * User: huange
 * Date: Oct 11, 2002
 * Time: 2:39:29 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.cboe.application.supplier;

public class NBBOAgentAdminSupplierFactory {
    private static NBBOAgentAdminSupplier nbboAgentAdminSupplier = null;

    /**
     * NBBOAgentAdminSupplierFactory constructor comment.
     */
    public NBBOAgentAdminSupplierFactory()
    {
        super();
    }

    /**
     * This method returns the singleton instance of the NBBOAgentAdminSupplier or
     * creates it if it has not been instantiated yet.
     */
    public synchronized static NBBOAgentAdminSupplier create()
    {
        if (nbboAgentAdminSupplier == null)
        {
            // Configuration service will eventually supply the initial hash table size
            nbboAgentAdminSupplier = new NBBOAgentAdminSupplier();
        };
        return nbboAgentAdminSupplier;
    }

    public static NBBOAgentAdminSupplier find()
    {
        return create();
    }
}
