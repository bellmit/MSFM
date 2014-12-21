//
// ------------------------------------------------------------------------
// FILE: CASAdminServiceCommand.java
// 
// PACKAGE: com.cboe.presentation.api
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2004 The Chicago Board Options Exchange.  All Rights Reserved.
// ------------------------------------------------------------------------
//
package com.cboe.presentation.api;

public interface CASAdminServiceCommand
{
    String getProcessName();
    String getOrbName();
    String getShortName();
    String getCommandName();
}
