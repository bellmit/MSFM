package com.cboe.interfaces.application;

import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.SystemException;

import java.util.Map;

/**
 * @author Jing Chen
 */
public interface UserSessionQueryHome
{
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "UserSessionQueryHome";
    // get a snapshot of current active sessions.
    public Map getActiveSessions();
    public String[] getLoggedInUserIds() throws CommunicationException, AuthorizationException, SystemException;
}
