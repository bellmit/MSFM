//
// -----------------------------------------------------------------------------------
// Source file: ManualReportingAPIFactory.java
//
// PACKAGE: com.cboe.presentation.api
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api;

import com.cboe.interfaces.presentation.api.ManualReportingAPI;

public class ManualReportingAPIFactory {
    private static ManualReportingAPI mrAPI;

    private ManualReportingAPIFactory()
    {
    }

    public static synchronized ManualReportingAPI find()
    {
        if(mrAPI == null)
        {
            mrAPI = new ManualReportingAPIImpl();
        }
        return mrAPI;
    }
}
