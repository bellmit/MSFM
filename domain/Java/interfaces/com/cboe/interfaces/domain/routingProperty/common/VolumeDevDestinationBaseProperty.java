//
//-----------------------------------------------------------------------------------
//Source file: VolumeDevDestinationBaseProperty.java
//
//PACKAGE: com.cboe.interfaces.domain.routingProperty.common
//
//-----------------------------------------------------------------------------------
//Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
//-----------------------------------------------------------------------------------
package com.cboe.interfaces.domain.routingProperty.common;

import com.cboe.interfaces.domain.routingProperty.BaseProperty;

public interface VolumeDevDestinationBaseProperty extends BaseProperty
{
    VolumeDevDestination getVolumeDevDestination();

    void setVolumeDevDestination(VolumeDevDestination value);

    public String getWorkstation();

    public void setWorkstation(String workstation);

    public String getDeviation();

    public void setDeviation(String deviation);

    public int getVolume();

    public void setVolume(int deviation);
}
