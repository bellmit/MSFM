package com.cboe.application.supplier;

import com.cboe.interfaces.application.*;
import com.cboe.interfaces.domain.session.BaseSessionManager;
/**
 * Creates and returns an instance of the QuoteStatusSupplier on the CAS for
 * the requesting user.
 *
 * @author Derek T. Chambers-Boucher
 * @version 06/30/1999
 */

public class QuoteStatusSupplierFactory {

    private static UserSupplierHelper userSupplierHelper;

    private static UserSupplierHelper getHelper() {
        if ( userSupplierHelper == null ) {
            userSupplierHelper = new UserSupplierHelper();
        }
        return userSupplierHelper;
    }

    /**
     * QuoteStatusSupplierFactory default constructor
     */
    public QuoteStatusSupplierFactory()
    {
        super();
    }

    /**
     * This method returns the singleton instance of the QuoteStatusSupplier or
     * creates it if it has not been instantiated yet.
     */
    public synchronized static QuoteStatusSupplier create( BaseSessionManager session )
    {
        QuoteStatusSupplier quoteStatusSupplier = (QuoteStatusSupplier)getHelper().findSupplier( session );
        if ( quoteStatusSupplier == null )
        {
        // Configuration service will eventually supply the initial hash table size
          quoteStatusSupplier = new QuoteStatusSupplier(session);
          getHelper().addSupplier( quoteStatusSupplier, session );
        }

        return quoteStatusSupplier;
    }

    public static QuoteStatusSupplier find( BaseSessionManager session )
    {
        return create( session );
    }

    /**
     * This method removes, from the Hashtable, the instance of the QuoteStatusSupplier
     * for the memberKey.
     */
    public synchronized static void remove( BaseSessionManager session )
    {
        getHelper().removeSupplier( session );
    }



}
