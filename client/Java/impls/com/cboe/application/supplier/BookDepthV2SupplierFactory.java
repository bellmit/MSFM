package com.cboe.application.supplier;

import com.cboe.interfaces.domain.session.BaseSessionManager;

/**
 * Creates and returns a singleton instance of the BookDepthSupplier on the CAS.
 *
 * @author William Wei
 */

public class BookDepthV2SupplierFactory
{
    private static UserSupplierHelper userSupplierHelper;
    /**
     * BookDepthSupplierFactory constructor comment.
     */
    public BookDepthV2SupplierFactory()
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
    public synchronized static BookDepthV2Supplier create(BaseSessionManager sessionManager)
    {
        BookDepthV2Supplier bookDepthV2Supplier = (BookDepthV2Supplier)getHelper().findSupplier( sessionManager );
        if ( bookDepthV2Supplier == null )
        {
        // Configuration service will eventually supply the initial hash table size
          bookDepthV2Supplier = new BookDepthV2Supplier(sessionManager);
          getHelper().addSupplier( bookDepthV2Supplier, sessionManager );
        }
        return bookDepthV2Supplier;
    }

    public static BookDepthV2Supplier find(BaseSessionManager sessionManager)
    {
        return create(sessionManager);
    }

    public synchronized static void remove( BaseSessionManager session )
    {
        getHelper().removeSupplier( session );
    }
}
