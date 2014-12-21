package com.cboe.cfix.interfaces;

/**
 * FixSessionListenerIF.java
 *
 * @author Dmitry Volpyansky
 *
 */

public interface FixSessionListenerIF
{
    public static final int SESSION_CONNECTED              = 0;
    public static final int SESSION_LOGGED_OUT_BY_SENDER   = 1 << 1;
    public static final int SESSION_DISCONNECTED_BY_SENDER = 1 << 2;
    public static final int SESSION_LOGGED_OUT_BY_TARGET   = 1 << 3;
    public static final int SESSION_DISCONNECTED_BY_TARGET = 1 << 4;

    public void sessionStarting(FixSessionIF fixSession);
    public void sessionTargetLoggedIn(FixSessionIF fixSession);
    public void sessionTerminating(FixSessionIF fixSession, int sessionEndingFlags);
    public void sessionEnded(FixSessionIF fixSession, int sessionEndingFlags);
}
