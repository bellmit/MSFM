/*
 * Created by IntelliJ IDEA.
 * User: huange
 * Date: Sep 30, 2002
 * Time: 12:22:10 PM
 * To change template for new interface use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.cboe.interfaces.application;
import com.cboe.interfaces.application.NBBOAgentSessionManager;

public interface NBBOAgentSessionManagerHome {
    public final static String HOME_NAME = "NBBOAgentSessionManagerHome";
    public NBBOAgentSessionManager create(com.cboe.interfaces.application.SessionManager sessionManager);
    public NBBOAgentSessionManager find(com.cboe.interfaces.application.SessionManager sessionManager);
    public void remove(com.cboe.interfaces.application.SessionManager sessionManager);

}

