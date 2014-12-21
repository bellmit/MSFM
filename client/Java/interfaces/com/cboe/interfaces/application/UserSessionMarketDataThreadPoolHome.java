package com.cboe.interfaces.application;

import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.util.ThreadPool;


/**
 * This is the common interface for the GlobalThreadPoolHome
 * @author Connie Feng
 */
public interface UserSessionMarketDataThreadPoolHome
{
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "UserSessionMarketDataThreadPoolHome";

    /**
    * Creates an instance of the ThreadPool.
    *
    * @return reference to ThreadPool
    *
    * @author Connie Feng
    */
    public ThreadPool create(BaseSessionManager session);

    /**
     * Finds an instance of the ThreadPool.
     *
     * @return reference to ThreadPool
     *
     * @author Connie Feng
     */
    public ThreadPool find(BaseSessionManager session);


    /**
     * Finds an instance of the ThreadPool. This thread pool will be used for overly market data delivering
     *
     * @return reference to ThreadPool
     *
     * @author Emily Huang
     */
    public ThreadPool findOverlayThreadPool(BaseSessionManager session);

    /**
     *  removes ThreadPool given a specific Session
     *
     *  @param session the session manager to Remove
     *  @return none
     */
    public void remove(BaseSessionManager session);

}
