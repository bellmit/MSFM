//
// -----------------------------------------------------------------------------------
// Source file: UserAccessPCQSDelegate.java
//
// PACKAGE: com.cboe.delegates.application
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.delegates.application;

import com.cboe.interfaces.application.UserAccessPCQS;

public class UserAccessPCQSDelegate extends com.cboe.idl.pcqs.POA_UserAccessPCQS_tie
{
    public UserAccessPCQSDelegate(UserAccessPCQS delegate)
    {
        super(delegate);
    }
}
