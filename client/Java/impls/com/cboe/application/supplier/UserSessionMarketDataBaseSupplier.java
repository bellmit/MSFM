package com.cboe.application.supplier;

import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.domain.supplier.BaseSupplier;
import com.cboe.util.ThreadPool;
import com.cboe.application.shared.ServicesHelper;

/**
 * UserSessionBaseSupplier extends the BaseSupplier with its own threadpool for
 * each session
 *
 * @author Connie Feng
 * @version 06/13/2000
 */

public abstract class UserSessionMarketDataBaseSupplier extends UserSessionBaseSupplier
{
    protected ThreadPool getThreadPool()
    {
        return ServicesHelper.getUserSessionMarketDataThreadPool(session);
    }
    public UserSessionMarketDataBaseSupplier(BaseSessionManager session)
    {
        super(session);
    }
}
