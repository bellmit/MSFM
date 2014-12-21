package com.cboe.interfaces.remoteApplication;

/**
 * @author Jing Chen
 */
public interface RemoteCASSessionManagerHome
{
    public final static String HOME_NAME = "RemoteCASSessionManagerHome";
    public RemoteCASSessionManager create(String userSessionIor, String userId, String casOrigin);
    public RemoteCASSessionManager find(String userSessionIor, String userId, String casOrigin);
    public void remove(String userSessionIor);
}
