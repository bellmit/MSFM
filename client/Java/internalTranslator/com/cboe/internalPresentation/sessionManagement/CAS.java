//
// -----------------------------------------------------------------------------------
// Source file: CAS.java
//
// PACKAGE: com.cboe.internalPresentation.sessionManagement;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.sessionManagement;

/**
 * CAS represents the "Client Application Server" system component
 */
public class CAS extends SessionManagementComponent
{
    protected User[] connectedUsers = null;
    protected String[] connectedFrontEnds = null;

    /**
     * CAS constructor.
     * @param name of CAS
     * @param connectedUsers to this CAS
     * @param connectedFrontEnds that this CAS is connected to
     * @param isRunning True if running, false otherwise
     * @param isMaster True if master, false otherwise
     */
    public CAS(String name, User[] connectedUsers, String[] connectedFrontEnds, boolean isRunning, boolean isMaster)
    {
        super(name, isRunning, isMaster);

        this.connectedUsers = connectedUsers;
        this.connectedFrontEnds = connectedFrontEnds;
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
     * Gets a list of users who are connected to this CAS
     */
    public User[] getConnectedUsers()
    {
        return this.connectedUsers;
    }

    /**
     * Gets a list of front ends to which this CAS is connected
     */
    public String[] getConnectedFrontEnds()
    {
        return this.connectedFrontEnds;
    }
}
