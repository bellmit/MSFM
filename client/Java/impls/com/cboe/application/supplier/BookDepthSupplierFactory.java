package com.cboe.application.supplier;

import com.cboe.interfaces.domain.session.BaseSessionManager;

/**
 * Creates and returns a singleton instance of the BookDepthSupplier on the CAS.
 *
 * @author William Wei
 */

public class BookDepthSupplierFactory
{
    private static UserSupplierHelper userSupplierHelper;

    /**
     * BookDepthSupplierFactory constructor comment.
     */
    public BookDepthSupplierFactory()
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
     * This method returns the singleton instance of the BookDepthSupplier or
     * creates it if it has not been instantiated yet.
     */
    public synchronized static BookDepthSupplier create(BaseSessionManager sessionManager)
    {
        BookDepthSupplier bookDepthSupplier = (BookDepthSupplier)getHelper().findSupplier( sessionManager );
        if ( bookDepthSupplier == null )
        {
        // Configuration service will eventually supply the initial hash table size
          bookDepthSupplier = new BookDepthSupplier(sessionManager);
          getHelper().addSupplier( bookDepthSupplier, sessionManager );
        }
        return bookDepthSupplier;
    }


    public static BookDepthSupplier find(BaseSessionManager sessionManager)
    {
        return create(sessionManager);
    }

    public synchronized static void remove( BaseSessionManager session )
    {
        getHelper().removeSupplier( session );
    }
}
