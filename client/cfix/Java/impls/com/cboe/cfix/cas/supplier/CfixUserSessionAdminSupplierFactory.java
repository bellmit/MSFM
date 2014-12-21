/**
 * @author Jing Chen
 */
package com.cboe.cfix.cas.supplier;

import com.cboe.application.supplier.*;
import com.cboe.interfaces.domain.session.*;

public class CfixUserSessionAdminSupplierFactory {
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
    public CfixUserSessionAdminSupplierFactory()
    {
        super();
    }

    /**
     * This method returns the instance of the UserSessionAdminSupplier or
     * creates it if it has not been instantiated yet.
     */
    public synchronized static CfixUserSessionAdminSupplier create( BaseSessionManager session )
    {
        CfixUserSessionAdminSupplier userSessionAdminSupplier = (CfixUserSessionAdminSupplier)getHelper().findSupplier( session );
        if ( userSessionAdminSupplier == null )
        {
            // Configuration service will eventually supply the initial hash table size
            userSessionAdminSupplier = new CfixUserSessionAdminSupplier(session);
            getHelper().addSupplier( userSessionAdminSupplier, session );
        }

        return userSessionAdminSupplier;
    }

    public static CfixUserSessionAdminSupplier find( BaseSessionManager session )
    {
        return create( session );
    }

    /**
     * This method removes, from the Hashtable, the instance of the UserSessionAdminSupplier
     * for the userId.
     */
    public synchronized static void remove( BaseSessionManager session )
    {
        getHelper().removeSupplier( session );
    }

}
