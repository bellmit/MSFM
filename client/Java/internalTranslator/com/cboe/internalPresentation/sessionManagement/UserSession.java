//
// -----------------------------------------------------------------------------------
// Source file: UserSession.java
//
// PACKAGE: com.cboe.internalPresentation.sessionManagement;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.sessionManagement;

/**
 * UserSession represents the CAS to User mapping
 */
public class UserSession extends SessionManagementComponent
{
    protected String[] casList = null;
    protected User user = null;

    /**
     * Constructor
     * @param user to map
     * @param array of cas's to map user to
     */
    public UserSession(User user, String[] casList)
    {
        super(user.getName(), false, false);

        this.casList = casList;
        this.user = user;
    }

    /**
     * Determines if this object is equal to another
     * @param otherObject to compare with
     * @return True if equal, false otherwise.
     */
    public boolean equals(Object otherObject)
    {
        boolean isEqual = super.equals(otherObject);

        if(isEqual)
        {
            if(getClass() == otherObject.getClass())
            {
                isEqual = true;
            }
            else
            {
                isEqual = false;
            }
        }

        return isEqual;
    }

    /**
     * Gets an array of cas names to which this session's user is logged into
     * @return array of cas's
     */
    public String[] getCASList()
    {
        return this.casList;
    }

    /**
     * Gets the User associated with this UserSession
     */
    public User getUser()
    {
        return this.user;
    }
}
