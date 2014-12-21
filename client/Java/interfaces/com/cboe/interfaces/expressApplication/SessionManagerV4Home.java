//
// -----------------------------------------------------------------------------------
// Source file: SessionManagerV4Home.java
//
// PACKAGE: com.cboe.interfaces.expressApplication
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.expressApplication;

import com.cboe.idl.user.SessionProfileUserStructV2;
import com.cboe.idl.cmiCallback.CMIUserSessionAdmin;

import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;

import com.cboe.interfaces.application.SessionManagerHome;

public interface SessionManagerV4Home extends SessionManagerHome
{
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "SessionManagerV4Home";

    /**
     * Creates an instance of the V4 session manager.
     * @return reference to V4 session manager
     */
    public SessionManagerV4 createSessionManagerV4(SessionProfileUserStructV2 validUser, String sessionId, int sessionKey,
                                                        CMIUserSessionAdmin clientListener, short sessionType,
                                                        boolean gmdTextMessaging)
            throws DataValidationException, SystemException;

    /**
     * Removes an instance of the V4 session manager.
     * @param session the V4 session manager to be removed in association with the user
     * @param userId the valid user information
     */
    public void remove(SessionManagerV4 session, String userId);
}