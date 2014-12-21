//
// -----------------------------------------------------------------------------------
// Source file: SAOrderManagementTerminalAPIFactory.java
//
// PACKAGE: com.cboe.internalPresentation.api
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2009 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.api;

import com.cboe.interfaces.presentation.api.OrderManagementTerminalAPI;

public class SAOrderManagementTerminalAPIFactory
{
    private static OrderManagementTerminalAPI omtAPI;

    private SAOrderManagementTerminalAPIFactory()
    {
    }

    public static synchronized OrderManagementTerminalAPI find()
    {
        if(omtAPI == null)
        {
            omtAPI = new SAOrderManagementTerminalAPIImpl();
        }
        return omtAPI;
    }
}
