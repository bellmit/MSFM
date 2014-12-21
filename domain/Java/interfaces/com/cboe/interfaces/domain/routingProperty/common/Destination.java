//
// -----------------------------------------------------------------------------------
// Source file: Destination.java
//
// PACKAGE: com.cboe.interfaces.domain.routingProperty.common
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.domain.routingProperty.common;

/**
 * Wrapper for the three attributes that define a Destination: workstation name, workstation type, and action.
 */
public interface Destination extends Comparable<Destination>
{
    /**
     * Returns the destination workstation name
     * @return
     */
    public String getWorkstation();

    public void setWorkstation(String workstation);
    
}