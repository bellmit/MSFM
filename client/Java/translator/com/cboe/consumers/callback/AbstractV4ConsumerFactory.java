//
// -----------------------------------------------------------------------------------
// Source file: AbstractV4ConsumerFactory.java
//
// PACKAGE: com.cboe.consumers.callback
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.consumers.callback;

import com.cboe.application.shared.RemoteConnectionCBOEOrb;

public abstract class AbstractV4ConsumerFactory
{
    protected static String getPOAName()
    {
        return RemoteConnectionCBOEOrb.MDX_POA_NAME;
    }
}
