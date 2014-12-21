//
// -----------------------------------------------------------------------------------
// Source file: CASFactory.java
//
// PACKAGE: com.cboe.presentation.casMonitor
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.casMonitor;

import com.cboe.interfaces.casMonitor.CASModel;
import com.cboe.interfaces.casMonitor.CAS;
import com.cboe.interfaces.casMonitor.CASInformation;
import com.cboe.interfaces.presentation.processes.CBOEProcess;

public abstract class CASFactory
{
    public static CASModel createCASModel(CAS cas)
    {
        return new CASImpl(cas);
    }

    public static CASModel createCASModel(CBOEProcess process, CASInformation casInformation)
    {
        return new CASImpl(process, casInformation);
    }

    public static CASModel createCASModel(CBOEProcess process)
    {
        return new CASImpl(process);
    }

    public static CAS createCAS(CAS cas)
    {
        return createCASModel(cas);
    }
}
