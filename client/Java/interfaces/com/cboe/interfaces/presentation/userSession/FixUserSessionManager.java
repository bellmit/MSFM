package com.cboe.interfaces.presentation.userSession;

import com.cboe.idl.cmiV3.UserSessionManagerV3;


/**
 * Created by IntelliJ IDEA.
 * User: Sundares
 * Date: Aug 9, 2004
 * Time: 11:05:21 AM
 * To change this template use Options | File Templates.
 */
public interface FixUserSessionManager extends UserSessionManagerV3{
    public String getSessionId() ;

    public String getRemoteFirmID() ;

    public String getNetAddresses() ;

    public int getPort() ;

    public int getInMsgSeqNum() ;

    public int getOutMsgSeqNum() ;

    public int getConnectState();

}
