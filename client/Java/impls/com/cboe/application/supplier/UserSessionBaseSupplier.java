package com.cboe.application.supplier;

import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.domain.supplier.BaseSupplier;
import com.cboe.domain.instrumentedChannel.supplier.InstrumentedBaseSupplier;
import com.cboe.util.ThreadPool;
import com.cboe.application.shared.ServicesHelper;

/**
 * UserSessionBaseSupplier extends the BaseSupplier with its own threadpool for
 * each session
 *
 * @author Connie Feng
 * @version 06/13/2000
 */

public abstract class UserSessionBaseSupplier extends InstrumentedBaseSupplier
{
    protected BaseSessionManager session;

    protected ThreadPool getThreadPool()
    {
        return ServicesHelper.getUserSessionThreadPool(session);
    }
    /**
     * Constructor.
     *
     * @author Derek T. Chambers-Boucher
     * @author Paul Hoffman
     *
     * @param proxyClassName the fully qualified name of the proxies class.
     * @param name the name of the BaseSupplier.
     * @param channelAdapterSize the explicit channel adapter hashtable size.
     * @param threadPoolSize the explicit thread pool size.
     */
    public UserSessionBaseSupplier(BaseSessionManager session)
    {
        super(false);
        this.session = session; // has to set it before super.initialize to get to the session based thread pool
        threadPool = getThreadPool();
        start();
    }

    public void stopChannelAdapter()
    {
        super.stopChannelAdapter();
        session = null;
    }
}
