package com.cboe.application.supplier;

/**
 * Creates and returns a singleton instance of the UserSessionAdminSupplier on the CAS.
 *
 * @author Keith A. Korecky
 * @version 07/07/1999
 */

import com.cboe.interfaces.application.*;
import com.cboe.interfaces.domain.session.BaseSessionManager;

public class UserSessionAdminSupplierFactory
{
    private static UserSupplierHelper userSupplierHelper;

    private static UserSupplierHelper getHelper() {
        if ( userSupplierHelper == null ) {
            userSupplierHelper = new UserSupplierHelper();
        }
        return userSupplierHelper;
    }

    /**
     * UserSessionAdminSupplierFactory constructor comment.
     */
    public UserSessionAdminSupplierFactory()
    {
        super();
    }

    /**
     * This method returns the instance of the UserSessionAdminSupplier or
     * creates it if it has not been instantiated yet.
     */
    public synchronized static UserSessionAdminSupplier create( BaseSessionManager session )
    {
        UserSessionAdminSupplier userSessionAdminSupplier = (UserSessionAdminSupplier)getHelper().findSupplier( session );
        if ( userSessionAdminSupplier == null )
        {
            // Configuration service will eventually supply the initial hash table size
            userSessionAdminSupplier = new UserSessionAdminSupplier(session);
            getHelper().addSupplier( userSessionAdminSupplier, session );
        }

        return userSessionAdminSupplier;
    }

    public static UserSessionAdminSupplier find( BaseSessionManager session )
    {
        return create( session );
    }

    /**
     * This method removes, from the Hashtable, the instance of the UserSessionAdminSupplier
     * for the userId.
     */
    public synchronized static void remove( SessionManager session )
    {
        getHelper().removeSupplier( session );
    }




}
