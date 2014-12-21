package com.cboe.application.supplier;

import com.cboe.interfaces.application.*;
import com.cboe.interfaces.domain.session.BaseSessionManager;
/**
 * Creates and returns an instance of the OrderStatusSupplier on the CAS for the
 * requesting user.
 *
 * @author Derek T. Chambers-Boucher
 * @version 06/29/1999
 */

public class OrderStatusSupplierFactory
{


    private static UserSupplierHelper userSupplierHelper;

    private static UserSupplierHelper getHelper() {
        if ( userSupplierHelper == null ) {
            userSupplierHelper = new UserSupplierHelper();
        }
        return userSupplierHelper;
   }

    /**
     * OrderStatusSupplierFactory default constructor
     */
    public OrderStatusSupplierFactory()
    {
        super();
    }


    /**
     * This method returns the instance of the OrderStatusSupplier for the
     * memberKey or creates it if it does not exist yet.
     */
    public synchronized static OrderStatusSupplier create( BaseSessionManager session )
    {
        OrderStatusSupplier orderStatusSupplier = (OrderStatusSupplier)getHelper().findSupplier( session );
        if ( orderStatusSupplier == null )
        {
        // Configuration service will eventually supply the initial hash table size
          orderStatusSupplier = new OrderStatusSupplier(session);
          getHelper().addSupplier( orderStatusSupplier, session );
        }

        return orderStatusSupplier;
    }

    public static OrderStatusSupplier find( BaseSessionManager session )
    {
        return create( session );
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
