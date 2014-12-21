//
//
// -----------------------------------------------------------------------------------
// Source file: LogicalNameAFactory
//
// PACKAGE: com.cboe.presentation.logicalName;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2009 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.orbNameAlias;

import com.cboe.idl.clusterInfo.LogicalOrbNameStruct;

import com.cboe.interfaces.presentation.processes.LogicalNameModel;


public class LogicalNameFactory 
{
    /**
     *  Create a new LogicalName object with all the information.  This is not
     *  put into the cache.
     */
    public static LogicalNameModel createLogicalName(String logicalName, String orbName)
    {
        return new LogicalNameImpl(logicalName, orbName);
    }

    /**
     *  Create a new LogicalName object with all just the logical name.  This is not
     *  put into the cache.
     */
    public static LogicalNameModel createBlankLogicalName(String logicalName)
    {
        return new LogicalNameImpl(logicalName);
    }

    /**
     * Create a new LogicalName object with all the information.  This is not put into the cache.
     */
    public static LogicalNameModel createLogicalName(LogicalOrbNameStruct struct)
    {
        return new LogicalNameImpl(struct.logicalName, struct.orbName);
    }
}