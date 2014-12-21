package com.cboe.application.supplier;

import java.util.Hashtable;
import com.cboe.interfaces.application.*;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.domain.supplier.BaseSupplier;

/**
 * This is the base class extended by all user-based suppliers.
 * It maintains the code responsible for managing the hash table which
 * ties supplier objects to userIds.
 *
 * @author Keith A. Korecky
 * @version 07/6/1999
 */

public class UserSupplierHelper
{
    private static final int DEFAULT_SIZE = 500;
    /**
    * Collection of supplier objects
    * The key will be the userId and the value will be the Supplier object
    */
    private Hashtable userSuppliers = null;

    /**
    * Default constructors...
    *
    */
    public UserSupplierHelper()
    {
         userSuppliers = new Hashtable(DEFAULT_SIZE);
    }

    /**
    * Finds the UserSupplier based on the userId
    * @param userId the member key information
    * @return UserSupplier the supplier object
    */
    public UserSessionBaseSupplier findSupplier(BaseSessionManager session)
    {
        return (UserSessionBaseSupplier)userSuppliers.get(session);
    }

    /**
    * Adds the Supplier to the collection
    * @param userSupplier the Supplier object
    * @param userId the member key information
    * @return none
    */
    public void addSupplier(UserSessionBaseSupplier userSupplier, BaseSessionManager session)
    {
        userSuppliers.put( session, userSupplier );
    }

    /**
    * Removes the UserSupplier from the collection based on the userId
    * @param userId the member key information
    * @return none
    */
    public void removeSupplier(BaseSessionManager session)
    {
        UserSessionBaseSupplier supplier = findSupplier(session);
        if (supplier != null)
        {
            supplier.stopChannelAdapter();
        }
        userSuppliers.remove(session);
    }
}
