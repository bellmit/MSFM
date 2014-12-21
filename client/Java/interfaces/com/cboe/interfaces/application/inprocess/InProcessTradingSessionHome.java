package com.cboe.interfaces.application.inprocess;

/**
 * @author Jing Chen
 */
public interface InProcessTradingSessionHome
{
    public final static String HOME_NAME = "InProcessTradingSessionHome";
    public InProcessTradingSession create(InProcessSessionManager sessionManager, UserSessionAdminConsumer consumer);
}
