//
// ------------------------------------------------------------------------
// FILE: CASInformationFactory.java
// 
// PACKAGE: com.cboe.presentation.casMonitor
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2004 The Chicago Board Options Exchange.  All Rights Reserved.
// ------------------------------------------------------------------------
//

package com.cboe.presentation.casMonitor;

import com.cboe.interfaces.casMonitor.CASInformation;
import com.cboe.client.xml.bind.GICASConfigurationType;

public class CASInformationFactory
{
    public static CASInformation createCASInformation()
    {
        return new CASInformationImpl();
    }
    public static CASInformation createCASInformation(GICASConfigurationType casConfigurationInformation)
    {
        return new CASInformationImpl(casConfigurationInformation);
    }
}