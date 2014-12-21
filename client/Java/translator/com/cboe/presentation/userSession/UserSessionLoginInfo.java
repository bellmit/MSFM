//
// -----------------------------------------------------------------------------------
// Source file: UserSessionLogonInfo.java
//
// PACKAGE: com.cboe.presentation.userSession;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.userSession;

import com.cboe.idl.cmi.Version;
import com.cboe.idl.cmiConstants.LoginSessionModes;
import com.cboe.idl.cmiConstants.LoginSessionTypes;
import com.cboe.idl.cmiUser.UserLogonStruct;

/**
 * Contains the data required to login to a session.
 */
public class UserSessionLoginInfo
{
    private String userId = null;
    private String password = null;
    private String version = null;
    private char loginMode;
    private int sessionLoginType;

    /**
     * default constructor
     */
    public UserSessionLoginInfo()
    {
        super();
    }

    /**
     * Constructor
     */
    public UserSessionLoginInfo(String userId, String password, String version, char loginMode, int sessionLoginType)
    {
        this();
        setUserId(userId);
        setPassword(password);
        setVersion(version);
        setUserLoginMode(loginMode);
        setSessionLoginType(sessionLoginType);
    }

    /**
     * Gets the user id
     * @return user id
     */
    public String getUserId()
    {
        return userId;
    }

    /**
     * Gets the password
     * @return password
     */
    public String getPassword()
    {
        return password;
    }

    /**
     * Gets the version to use
     * @return login version
     */
    public String getVersion()
    {
        return version;
    }

    /**
     * Gets the user login mode
     * @return user login mode
     */
    public char getUserLoginMode()
    {
        return loginMode;
    }

    /**
     * Gets the session login type
     * @return session login type
     */
    public int getSessionLoginType()
    {
        return sessionLoginType;
    }

    /**
     * Builds a UserLogonStruct from contained information.
     * @return a UserLogonStruct
     */
    public UserLogonStruct toUserLogonStruct()
    {
        UserLogonStruct loginStruct = new UserLogonStruct(getUserId(), getPassword(), getVersion(), getUserLoginMode());

        return loginStruct;
    }

    /**
     * Sets the user id
     * @param user id to set.
     */
    private void setUserId(String userId)
    {
        this.userId = userId;
    }

    /**
     * Sets the password
     * @param password to set.
     */
    public void setPassword(String password)
    {
        this.password = password;
    }

    /**
     * Sets the app version to use
     * @param version to set.
     */
    protected void setVersion(String version)
    {
        if(version != null && version.length() > 0)
        {
            this.version = version;
        }
        else
        {
            throw new IllegalArgumentException("Version for UserSessionLoginInfo invalid.");
        }
    }

    /**
     * Sets the login mode to use
     * @param login mode
     */
    private void setUserLoginMode(char loginMode)
    {
        if(loginMode == LoginSessionModes.NETWORK_TEST ||
            loginMode == LoginSessionModes.PRODUCTION ||
            loginMode == LoginSessionModes.STAND_ALONE_TEST)
        {
            this.loginMode = loginMode;
        }
        else
        {
            throw new IllegalArgumentException("Login Mode for UserSessionLoginInfo invalid.");
        }
    }

    /**
     * Sets the session type to use
     * @param session type
     */
    private void setSessionLoginType(int sessionType)
    {
        if(sessionType == LoginSessionTypes.PRIMARY ||
            sessionType == LoginSessionTypes.SECONDARY)
        {
            this.sessionLoginType = sessionType;
        }
        else
        {
            throw new IllegalArgumentException("Session Type for UserSessionLoginInfo invalid.");
        }
    }
}