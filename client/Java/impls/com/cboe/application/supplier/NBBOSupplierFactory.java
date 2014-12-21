package com.cboe.application.supplier;

import com.cboe.interfaces.domain.session.BaseSessionManager;

/**
 * Creates and returns a singleton instance of the CurrentMarketSupplier on the CAS.
 *
 * @author Derek T. Chambers-Boucher
 * @author Jimmy Wang
 * @version 06/07/2000
 */

public class NBBOSupplierFactory
{
    private static UserSupplierHelper userSupplierHelper;

    /**
     * CurrentMarketSupplierFactory constructor comment.
     */
    public NBBOSupplierFactory()
    {
        super();
    }

    private static UserSupplierHelper getHelper() {
        if ( userSupplierHelper == null ) {
            userSupplierHelper = new UserSupplierHelper();
        }
        return userSupplierHelper;
   }
    /**
     * This method returns the singleton instance of the NBBOSupplier or
     * creates it if it has not been instantiated yet.
     */
    public synchronized static NBBOSupplier create(BaseSessionManager sessionManager)
    {
        NBBOSupplier nbboSupplier = (NBBOSupplier)getHelper().findSupplier( sessionManager );
        if ( nbboSupplier == null )
        {
        // Configuration service will eventually supply the initial hash table size
          nbboSupplier = new NBBOSupplier(sessionManager);
          getHelper().addSupplier( nbboSupplier, sessionManager );
        }
        return nbboSupplier;
    }

    public static NBBOSupplier find(BaseSessionManager sessionManager)
    {
        return create(sessionManager);
    }

    public synchronized static void remove( BaseSessionManager session )
    {
        getHelper().removeSupplier( session );
    }
}
