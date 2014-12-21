//
// -----------------------------------------------------------------------------------
// Source file: Destination.java
//
// PACKAGE: com.cboe.domain.routingProperty.common
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.routingProperty.common;

import com.cboe.interfaces.domain.routingProperty.common.Destination;
import com.cboe.interfaces.domain.routingProperty.common.NamedRoutingActionEnum;

import com.cboe.interfaces.domain.routingProperty.common.OrderLocation;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

/**
 * Wrapper for the three attributes that define a Destination: workstation name, workstation type, and action.
 */
public class DestinationImpl implements Destination
{
    public String workstation;

    /**
     * Convenience constructor to assist in creating a Destination from a String[] returned from the PropertyService.
     * @param workstation - the name of the workstation 
     */
    public DestinationImpl(String workstation)
    {
        setWorkstation(workstation);
    }
    

    public String getWorkstation()
    {
        return workstation;
    }
    

    public void setWorkstation(String workstation)
    {
        this.workstation = workstation;
    }
    

    public String toString()
    {
        StringBuffer sb = new StringBuffer(20);
        sb.append("[");
        sb.append("Name=").append(workstation);
        sb.append("]");
        return sb.toString();
    }

    public int compareTo(Destination otherDest)
    {
        return toString().compareTo(otherDest.toString());
    }
}
