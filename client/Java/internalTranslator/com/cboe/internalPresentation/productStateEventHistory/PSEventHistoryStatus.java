package com.cboe.internalPresentation.productStateEventHistory;
// -----------------------------------------------------------------------------------
// Source file: PSEventHistoryStatus
//
// PACKAGE: com.cboe.internalPresentation.productStateEventHistory
// 
// Created: Mar 13, 2006 1:18:53 PM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

public enum PSEventHistoryStatus
{
    COMPLETED("Completed"),
    INCOMPLETE("Completed With Errors"),
    FAILED("Failed"),
    FIRED("Fired");
    
    private final String description;

    PSEventHistoryStatus(String description)
    {
        this.description = description;
    }
    
    public String toString()
    {
        return description;
    }
}
