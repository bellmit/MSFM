//
// -----------------------------------------------------------------------------------
// Source file: UserSession.java
//
// PACKAGE: com.cboe.presentation.userSession;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.userSession;

import java.util.Calendar;
import org.omg.CORBA.UserException;

import com.cboe.idl.cmiConstants.LoginSessionTypes;
import com.cboe.exceptions.AuthorizationException;

import com.cboe.util.event.EventChannelListener;

import com.cboe.interfaces.presentation.user.UserStructModel;
import com.cboe.interfaces.presentation.permissionMatrix.UserPermissionMatrix;

/**
 * Defines the contract that a UserSession should fulfill.
 */
public interface UserSession
{
    public static final int MINIMUM_PASSWORD_LENGTH = 3;
    public static final int LOGIN_SESSION_TYPE_PRIMARY = LoginSessionTypes.PRIMARY;
    public static final int LOGIN_SESSION_TYPE_SECONDARY = LoginSessionTypes.SECONDARY;
    public static final int LOGIN_SESSION_TYPE_NOT_SPECIFIED = 999;
    public static final String LOGIN_SESSION_TYPE_PRIMARY_KEY = "Primary";
    public static final String LOGIN_SESSION_TYPE_SECONDARY_KEY = "Secondary";
    public static final String LOGIN_SESSION_TYPE_PROP_KEY = "SessionLoginType";
    public static final String PROPERTIES_SECTION_NAME = "Session";
    public static final String PROPERTIES_TIMER_SECTION_NAME = "Timers";
    public static final String HEARTBEAT_INTERVAL_PROP_KEY = "HeartBeatIntervalMillis";
    public static final int NORMAL_USER_SESSION = 0;
    public static final int FIX_USER_SESSION = 1;

    /**
     * Returns the default login session type for this session.
     * @return Either LOGIN_SESSION_TYPE_PRIMARY, LOGIN_SESSION_TYPE_SECONDARY OR LOGIN_SESSION_TYPE_NOT_SPECIFIED
     */
    public int getDefaultLoginSessionType();

    /**
     * Attempts to login in a user.
     * @param loginInfo to use for login.
     * @return true if logged in successfully, false otherwise.
     * @exception AuthorizationException log in failed. Probably bad login information.
     * @exception UserException another type of fatal failure occurred.
     */
    public boolean login(UserSessionLoginInfo loginInfo) throws AuthorizationException, UserException, Exception;

    /**
     * Attempts to logout a user.
     * @return true if logged out successfully, false otherwise.
     */
    public boolean logout();

    /**
     * Informs the session that it has already been forced to logout.
     */
    public void forcedLogout();

    /**
     * Determines if the user is currently logged in.
     * @return true if logged in, false otherwise.
     */
    public boolean isLoggedIn();

    /**
     * Determines if this user is logged in as a primary login.
     * @return True if logged in as a primary login, false otherwise.
     */
    public boolean isPrimaryLogin();

    /**
     * Determines if this user is logged in as a secondary login.
     * @return True if logged in as a secondary login, false otherwise.
     */
    public boolean isSecondaryLogin();

    /**
     * Gets the time the user logged in.
     * @return Time user logged in. Should return null if not logged in.
     */
    public Calendar getTimeLoggedIn();

    /**
     * Gets the time the user logged out.
     * @return Time user logged out. Should return null if logged in.
     */
    public Calendar getTimeLoggedOut();

    /**
     * Changes the password on the current session.
     * @param oldPassword that user is currently logged in as.
     * @param newPassword to change it to.
     * @return True is change successful, false otherwise.
     * @exception AuthorizationException Change password failed for security reasons.
     * Probably used wrong <code>oldPassword</code> or user not currently logged in.
     * @exception UserException another type of fatal failure occurred.
     * @exception IllegalArgumentException new password was not required length.
     */
    public boolean changePassword(String oldPassword, String newPassword) throws AuthorizationException, UserException, IllegalArgumentException;

    /**
     * Gets the user model this session represents.
     * @return UserModel containing user information.
     */
    public UserStructModel getUserModel();

    /**
     * Gets the permission Matrix for current user.
     * @return UserPermissionMatrix containing user permissions.
     */
    public UserPermissionMatrix getUserPermissionMatrix();

    /**
     * Adds a listener to received events for UserSession changes.
     * @param listener to add
     */
    public void addUserSessionListener(UserSessionListener listener);

    /**
     * Removes a listener from receiving events for UserSession changes.
     * @param listener to remove
     */
    public void removeUserSessionListener(UserSessionListener listener);

    /**
     * Gets the EventChannelListener implementation for this session.
     * @return Implementation of EventChannelListener
     */
    public EventChannelListener getSessionsEventChannelListener();
    
    /**
     * @author fuj
     *
     * decide if it's fix user session or not
     */
    public int getUserSessionType();
    
    public void setUserSessionType(int newUserSessionType);
}