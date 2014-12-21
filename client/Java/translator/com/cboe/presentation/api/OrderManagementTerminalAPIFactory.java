//
// -----------------------------------------------------------------------------------
// Source file: OrderManagementTerminalAPIFactory.java
//
// PACKAGE: com.cboe.presentation.api
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api;

import com.cboe.interfaces.presentation.api.OrderManagementTerminalAPI;

public class OrderManagementTerminalAPIFactory
{
    private static OrderManagementTerminalAPI omtAPI;

    private OrderManagementTerminalAPIFactory()
    {
    }

    public static synchronized OrderManagementTerminalAPI find()
    {
        if(omtAPI == null)
        {
            omtAPI = new OrderManagementTerminalAPIImpl();
        }
        return omtAPI;
    }
}
