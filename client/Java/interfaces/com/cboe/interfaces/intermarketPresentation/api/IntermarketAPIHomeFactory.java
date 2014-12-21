/*
 * Created by IntelliJ IDEA.
 * User: torresl
 * Date: Oct 2, 2002
 * Time: 5:16:15 PM
 */
package com.cboe.interfaces.intermarketPresentation.api;

import com.cboe.idl.cmi.UserSessionManager;

public interface IntermarketAPIHomeFactory
{
    public IntermarketQueryAPI findIntermarketQueryAPI();
    public NBBOAgentAPI findNBBOAgentAPI();
    public IntermarketAPI findIntermarketAPI();
    public boolean isInitialized();
    public void setUserSessionManager(UserSessionManager userSessionManager);
}
