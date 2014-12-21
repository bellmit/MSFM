package com.cboe.interfaces.application;

import java.util.HashMap;
import java.util.Map;

import com.cboe.interfaces.domain.session.BaseSessionManager;

/**
 * This is the common interface for the User Order Handling Home
 * @author Jing Chen
 */
public interface UserOrderServiceHome
{
    /** Name that will be used for this home.    */
    public final static String HOME_NAME = "UserOrderServiceHome";
    public UserOrderService find(BaseSessionManager session);
    public UserOrderService create(BaseSessionManager session);
    public void remove(BaseSessionManager session);
    public Map getSessionConstraints();
    public HashMap <BaseSessionManager, UserOrderService> getUserOrderServices();
}
