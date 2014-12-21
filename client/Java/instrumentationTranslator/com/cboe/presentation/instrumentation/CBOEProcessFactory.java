//
// -----------------------------------------------------------------------------------
// Source file: CBOEProcessFactory.java
//
// PACKAGE: com.cboe.presentation.instrumentation
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.instrumentation;

import com.cboe.interfaces.instrumentation.CBOEProcessMutable;

public abstract class CBOEProcessFactory
{
    /**
     * Creates an instance of CBOEProcessMutable
     * @param orbName - Corba ORB name String for this ProcessInfo object
     * @return CBOEProcessMutable
     */
    public static CBOEProcessMutable createCBOEProcessMutable(String orbName)
    {
        CBOEProcessMutable processMutable = new ProcessImpl(orbName);
        return processMutable;
    }

}
