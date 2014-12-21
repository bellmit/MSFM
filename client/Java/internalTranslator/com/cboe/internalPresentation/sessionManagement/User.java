//
// -----------------------------------------------------------------------------------
// Source file: User.java
//
// PACKAGE: com.cboe.internalPresentation.sessionManagement;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.sessionManagement;

/**
 * User represents a user of the system
 */
public class User extends SessionManagementComponent
{
    protected boolean isLoggedIn = false;

    /**
     * Constructor
     * @param userName user id of user
     * @param isLoggedIn true if logged in, false if logged out
     */
    public User(String userName, boolean isLoggedIn)
    {
        super(userName, false, false);
        this.isLoggedIn = isLoggedIn;
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
     * Determines whether or not the user represented with this User is logged in.
     * @return True if logged in, false of logged out.
     */
    public boolean isLoggedIn()
    {
        return this.isLoggedIn;
    }
}
