//
// -----------------------------------------------------------------------------------
// Source file: OrderRoutingSupplierFactory.java
//
// PACKAGE: com.cboe.application.supplier
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.application.supplier;

import com.cboe.interfaces.domain.session.BaseSessionManager;

/**
 * Creates and returns an instance of the OrderRoutingSupplier on the CAS for the
 * requesting user.
 */
@SuppressWarnings({"UtilityClassWithoutPrivateConstructor"})
public class OrderRoutingSupplierFactory
{
    private static UserSupplierHelper userSupplierHelper;

    private static synchronized UserSupplierHelper getHelper()
    {
        if(userSupplierHelper == null)
        {
            userSupplierHelper = new UserSupplierHelper();
        }
        return userSupplierHelper;
    }

    /**
     * This method returns the instance of the OrderRoutingSupplier for the
     * memberKey or creates it if it does not exist yet.
     */
    public static synchronized OrderRoutingSupplier create(BaseSessionManager session)
    {
        OrderRoutingSupplier orderRoutingSupplier =
                (OrderRoutingSupplier) getHelper().findSupplier(session);

        if(orderRoutingSupplier == null)
        {
            orderRoutingSupplier = new OrderRoutingSupplier(session);
            getHelper().addSupplier(orderRoutingSupplier, session);
        }

        return orderRoutingSupplier;
    }

    public static OrderRoutingSupplier find(BaseSessionManager session)
    {
        return create(session);
    }

    /**
     * This method removes, from the Hashtable, the instance of the OrderRoutingSupplier for the
     * memberKey.
     */
    public static synchronized void remove(BaseSessionManager session)
    {
        getHelper().removeSupplier(session);
    }
}
