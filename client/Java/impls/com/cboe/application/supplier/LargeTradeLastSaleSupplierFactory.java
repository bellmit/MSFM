package com.cboe.application.supplier;

import com.cboe.interfaces.domain.session.BaseSessionManager;

public class LargeTradeLastSaleSupplierFactory {
    private static UserSupplierHelper userSupplierHelper;

    public LargeTradeLastSaleSupplierFactory()
    {
        super();
    }

    private static UserSupplierHelper getHelper() {
        if ( userSupplierHelper == null ) {
            userSupplierHelper = new UserSupplierHelper();
        }
        return userSupplierHelper;
    }
    
    public synchronized static LargeTradeLastSaleSupplier create(BaseSessionManager sessionManager)
    {
    	LargeTradeLastSaleSupplier lastSaleSupplier = (LargeTradeLastSaleSupplier)getHelper().findSupplier( sessionManager );
        if ( lastSaleSupplier == null )
        {
            // Configuration service will eventually supply the initial hash table size
        	lastSaleSupplier = new LargeTradeLastSaleSupplier(sessionManager);
            getHelper().addSupplier( lastSaleSupplier, sessionManager );
        }
        return lastSaleSupplier;
    }

    public static LargeTradeLastSaleSupplier find(BaseSessionManager sessionManager)
    {
        return create(sessionManager);
    }

    public synchronized static void remove( BaseSessionManager session )
    {
        getHelper().removeSupplier( session );
    }

}
