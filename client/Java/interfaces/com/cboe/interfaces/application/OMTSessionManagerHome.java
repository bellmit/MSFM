package com.cboe.interfaces.application;

/**
 * Created by IntelliJ IDEA.
 * User: mahoney
 * Date: May 1, 2007
 * Time: 4:17:20 PM
 * To change this template use File | Settings | File Templates.
 */
public interface OMTSessionManagerHome
{
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "OMTSessionManagerHome";
    public OMTSessionManager create(SessionManager sessionManager);
}
