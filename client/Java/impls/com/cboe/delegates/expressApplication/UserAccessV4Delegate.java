//
// -----------------------------------------------------------------------------------
// Source file: UserAccessV4Delegate.java
//
// PACKAGE: com.cboe.delegates.expressApplication
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.delegates.expressApplication;

import com.cboe.interfaces.expressApplication.UserAccessV4;

public class UserAccessV4Delegate extends com.cboe.idl.cmiV4.POA_UserAccessV4_tie
{
    public UserAccessV4Delegate(UserAccessV4 delegate)
    {
        super(delegate);
    }
}