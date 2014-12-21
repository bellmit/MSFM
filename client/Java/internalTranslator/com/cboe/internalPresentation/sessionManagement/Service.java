//
// -----------------------------------------------------------------------------------
// Source file: Service.java
//
// PACKAGE: com.cboe.internalPresentation.sessionManagement;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.sessionManagement;

/**
 * Represents an SMS service
 */
public class Service extends SessionManagementComponent
{
    /**
     * Constructor
     * @param name of service
     * @param isRunning True if running, false otherwise
     * @param isMaster True if master, false otherwise
     */
    public Service(String name, boolean isRunning, boolean isMaster)
    {
        super(name, isRunning, isMaster);
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
}
