//
// -----------------------------------------------------------------------------------
// Source file: PCQSSessionManagerDelegate.java
//
// PACKAGE: com.cboe.delegates.application
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.delegates.application;

import com.cboe.interfaces.application.PCQSSessionManager;

public class PCQSSessionManagerDelegate extends com.cboe.idl.pcqs.POA_PCQSSessionManager_tie
{
    public PCQSSessionManagerDelegate(PCQSSessionManager delegate)
    {
        super(delegate);
    }
}
