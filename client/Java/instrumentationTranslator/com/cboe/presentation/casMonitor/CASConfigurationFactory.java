//
// ------------------------------------------------------------------------
// FILE: CASConfigurationFactory.java
// 
// PACKAGE: com.cboe.presentation.casMonitor
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
// 
// ------------------------------------------------------------------------
//

package com.cboe.presentation.casMonitor;

import com.cboe.interfaces.casMonitor.CASConfiguration;
import com.cboe.client.xml.bind.GIConfigurationResponseType;

public class CASConfigurationFactory
{
    public static CASConfiguration createCASConfiguration(GIConfigurationResponseType configurationResponseType)
    {
        return new CASConfigurationImpl(configurationResponseType);
    }
}