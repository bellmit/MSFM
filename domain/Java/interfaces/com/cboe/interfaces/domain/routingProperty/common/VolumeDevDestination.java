//
//-----------------------------------------------------------------------------------
//Source file: VolumeDevDestination.java
//
//PACKAGE: com.cboe.interfaces.domain.routingProperty.common
//
//-----------------------------------------------------------------------------------
//Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
//-----------------------------------------------------------------------------------
package com.cboe.interfaces.domain.routingProperty.common;

/**
* Wrapper for the three attributes that define a VolumeDevDestination: volume,deviation and destination.
*/
public interface VolumeDevDestination extends Comparable<VolumeDevDestination>
{
    
    public String getWorkstation();

    public void setWorkstation(String workstation);

    
    public String getDeviation();

    public void setDeviation(String deviation);
    

    public int getVolume();

    public void setVolume(int deviation);

}