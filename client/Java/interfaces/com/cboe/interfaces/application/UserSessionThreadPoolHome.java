package com.cboe.interfaces.application;

import com.cboe.util.*;
import com.cboe.interfaces.domain.session.BaseSessionManager;


/**
 * This is the common interface for the GlobalThreadPoolHome
 * @author Connie Feng
 */
public interface UserSessionThreadPoolHome
{
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "UserSessionThreadPoolHome";

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
     *  removes ThreadPool given a specific Session
     *
     *  @param session the session manager to Remove
     *  @return none
     */
    public void remove(BaseSessionManager session);

}
