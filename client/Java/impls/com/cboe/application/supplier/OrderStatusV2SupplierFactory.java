package com.cboe.application.supplier;

import com.cboe.interfaces.application.*;
import com.cboe.interfaces.domain.session.BaseSessionManager;

/**
 * Creates and returns an instance of the OrderStatusV2Supplier on the CAS for the
 * requesting user.
 *
 * @author Tom Trop
 */
public class OrderStatusV2SupplierFactory
{
    private static UserSupplierHelper userSupplierHelper;

    /**
     *
     */
    private static UserSupplierHelper getHelper() {
        if ( userSupplierHelper == null ) {
            userSupplierHelper = new UserSupplierHelper();
        }
        return userSupplierHelper;
    }

    /**
     *
     */
    public static OrderStatusV2Supplier find( BaseSessionManager session )
    {
        return create( session );
    }

    /**
     * This method returns the instance of the OrderStatusSupplier for the
     * memberKey or creates it if it does not exist yet.
     */
    public synchronized static OrderStatusV2Supplier create( BaseSessionManager session )
    {
        OrderStatusV2Supplier orderStatusSupplier = (OrderStatusV2Supplier)getHelper().findSupplier( session );

        if ( orderStatusSupplier == null )
        {
        // Configuration service will eventually supply the initial hash table size
            orderStatusSupplier = new OrderStatusV2Supplier(session);
            getHelper().addSupplier( orderStatusSupplier, session );
        }

        return orderStatusSupplier;
    }

    /**
     * This method removes, from the Hashtable, the instance of the OrderStatusSupplier
     * for the memberKey.
     */
    public synchronized static void remove( BaseSessionManager session )
    {
        getHelper().removeSupplier( session );
    }
}
