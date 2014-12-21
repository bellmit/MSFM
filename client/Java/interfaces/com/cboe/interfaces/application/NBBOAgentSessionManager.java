/*
 * Created by IntelliJ IDEA.
 * User: huange
 * Date: Sep 30, 2002
 * Time: 12:04:40 PM
 * To change template for new interface use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.cboe.interfaces.application;

import com.cboe.exceptions.SystemException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.AuthorizationException;

public interface NBBOAgentSessionManager
                 extends com.cboe.idl.cmiIntermarket.NBBOAgentSessionManagerOperations
{
    public void setRemoteDelegate(Object remoteDelegate)
        throws SystemException, CommunicationException, AuthorizationException;

}
