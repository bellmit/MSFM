//
//-----------------------------------------------------------------------------------
//Source file: VolumeDevDestinationImpl.java
//
//PACKAGE: com.cboe.domain.routingProperty.common
//
//-----------------------------------------------------------------------------------
//Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
//-----------------------------------------------------------------------------------
package com.cboe.domain.routingProperty.common;

import com.cboe.interfaces.domain.routingProperty.common.VolumeDevDestination;
import com.cboe.interfaces.domain.routingProperty.common.NamedRoutingActionEnum;

import com.cboe.interfaces.domain.routingProperty.common.OrderLocation;

import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

/**
 * Wrapper for the three attributes that define a VolumeDevDestination: volume,deviation and workstation name 
 */
public class VolumeDevDestinationImpl implements VolumeDevDestination
{
    public String workstation;
    public String deviation;
    public int volume;

    private String displayStr = null;

    /**
     * Convenience constructor to assist in creating a Destination from a String[] returned from the
     * PropertyService.
     * 
     * @param workstation - the name of the workstation
     */
    public VolumeDevDestinationImpl(int volume, String deviation, String workstation)
    {
        this.volume = volume;
        this.deviation = deviation;
        this.workstation = workstation;        
    }

    public String getWorkstation()
    {        
        return workstation;
    }

    public void setWorkstation(String workstation)
    {
        this.workstation = workstation;
        displayStr = null;
    }

    public String getDeviation() 
    {
        return deviation;
    }
    
    public void setDeviation(String deviation) 
    {
        this.deviation = deviation;
        displayStr = null;
    }
    
    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
        displayStr = null;
    }
    
    public String toString()
    {
        if (displayStr == null)
        {
            StringBuffer sb = new StringBuffer(20);
            sb.append("[");
            sb.append("Volume=").append(volume);
            sb.append("Deviation=").append(deviation);
            sb.append("Destination=").append(workstation);
            sb.append("]");
            displayStr = sb.toString();
        }
        return displayStr;
    }

    public int compareTo(VolumeDevDestination otherDest)
    {
        return toString().compareTo(otherDest.toString());
    }
}
