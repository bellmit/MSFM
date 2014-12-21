package com.cboe.interfaces.application;

import java.util.Map;

import com.cboe.interfaces.domain.session.BaseSessionManager;

/**
 * This is the common interface for the User Order Handling Home
 * @author Jing Chen
 */
public interface UserQuoteServiceHome
{
    /** Name that will be used for this home.    */
    public final static String HOME_NAME = "UserQuoteServiceHome";
    public UserQuoteService find(BaseSessionManager session);
    public UserQuoteService create(BaseSessionManager session);
    public void remove(BaseSessionManager session);
    public Map getSessionConstraints();
}
