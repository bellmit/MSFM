package com.cboe.domain.routingProperty.common;
// -----------------------------------------------------------------------------------
// Source file: BartWorkstation
//
// PACKAGE: com.cboe.domain.routingProperty.common
// 
// Created: Aug 24, 2006 1:19:29 PM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------


public class BoothWorkstation
{
    public int workstationNumber;

    public BoothWorkstation(int workstation)
    {
        this.workstationNumber = workstation;
    }
    
    public String toString()
    {
        return Integer.toString(workstationNumber);
    }
}
