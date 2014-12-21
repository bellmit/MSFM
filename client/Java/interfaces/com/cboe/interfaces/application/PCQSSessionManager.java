//
// -----------------------------------------------------------------------------------
// Source file: PCQSSessionManager.java
//
// PACKAGE: com.cboe.interfaces.application
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.application;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.SystemException;

public interface PCQSSessionManager extends com.cboe.idl.pcqs.PCQSSessionManagerOperations
{
    public void setRemoteDelegate(com.cboe.idl.pcqs.PCQSSessionManager remoteDelegate)
    		throws SystemException, CommunicationException, AuthorizationException;
}
