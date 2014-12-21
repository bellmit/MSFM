//
// -----------------------------------------------------------------------------------
// Source file: FIXUserSession.java
//
// PACKAGE: com.cboe.presentation.fix.userSession;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.fix.userSession;

import org.omg.CORBA.UserException;

import com.cboe.exceptions.AuthorizationException;

import com.cboe.presentation.userSession.UserSession;
import com.cboe.presentation.userSession.UserSessionLoginInfo;
import com.cboe.interfaces.presentation.user.UserStructModel;

/**
 * Defines the contract that a FIXUserSession fulfills.
 */
public interface FIXUserSession extends UserSession
{
    /**
     * Attempts to login in a user.
     * @param cmiLoginInfo to use for login to CMi CAS.
     * @param fixLoginInfo to use for login to FIX CAS.
     * @return true if logged in successfully, false otherwise.
     * @exception AuthorizationException log in failed. Probably bad login information.
     * @exception UserException another type of fatal failure occurred.
     */
    public boolean login(UserSessionLoginInfo cmiLoginInfo, UserSessionLoginInfo fixLoginInfo) throws AuthorizationException, UserException, Exception;

    /**
     * Gets the user model for the FIX session.
     * @return UserModel containing user information.
     */
    public UserStructModel getFIXUserModel();
//    /**
//     * Attempts to logout a user from FIX.
//     * @return true if logged out successfully, false otherwise.
//     */
//    public boolean logoutFIX();
//
//    /**
//     * Informs the session that it has already been forced to logout from the FIX CAS.
//     */
//    public void forcedLogoutFIX();
//
//    /**
//     * Determines if the user is currently logged in to FIX.
//     * @return true if logged in, false otherwise.
//     */
//    public boolean isLoggedInFIX();

}
