//
// -----------------------------------------------------------------------------------
// Source file: ActionThreader.java
//
// PACKAGE: com.cboe.interfaces.presentation.common.threading;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.common.threading;

import com.cboe.interfaces.presentation.threading.APIWorker;

public interface ActionThreader
{
    public void launchAction(APIWorker worker);
}