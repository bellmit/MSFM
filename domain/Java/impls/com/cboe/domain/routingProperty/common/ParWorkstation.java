package com.cboe.domain.routingProperty.common;
// -----------------------------------------------------------------------------------
// Source file: ParWorkstation
//
// PACKAGE: com.cboe.domain.routingProperty.common
// 
// Created: Aug 24, 2006 1:20:21 PM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------


public class ParWorkstation
{
    public char workstation;

    public ParWorkstation(char workstation)
    {
        this.workstation = workstation;
    }
    
    public String toString()
    {
        return Character.toString(workstation);
    }
}
