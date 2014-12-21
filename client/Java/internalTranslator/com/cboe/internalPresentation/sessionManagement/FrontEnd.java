//
// -----------------------------------------------------------------------------------
// Source file: FrontEnd.java
//
// PACKAGE: com.cboe.internalPresentation.sessionManagement;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.sessionManagement;

/**
 * FrontEnd represents the "server front end" system component
 */
public class FrontEnd extends SessionManagementComponent
{
    protected String[] connectedCASList = null;
    protected String[] connectedServices = null;

    /**
     * Constructor
     * @param name of FrontEnd
     * @param connectedCASList to this FrontEnd
     * @param connectedServices that this FrontEnd is connected to
     * @param isRunning True if running, false otherwise
     * @param isMaster True if master, false otherwise
     */
    public FrontEnd(String name, String[] connectedCASList, String[] connectedServices, boolean isRunning, boolean isMaster)
    {
        super(name, isRunning, isMaster);

        this.connectedCASList = connectedCASList;
        this.connectedServices = connectedServices;
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
     * Returns a list of CAS names that are connected to this Front End.
     */
    public String[] getConnectedCASList()
    {
        return this.connectedCASList;
    }

    /**
     * Returns a list of service names that this Front End is connected to.
     */
    public String[] getConnectedServices()
    {
        return this.connectedServices;
    }
}
