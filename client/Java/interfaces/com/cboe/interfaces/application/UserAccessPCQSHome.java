//
// -----------------------------------------------------------------------------------
// Source file: UserAccessPCQSHome.java
//
// PACKAGE: com.cboe.interfaces.application
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.application;

public interface UserAccessPCQSHome
{
    public final static String HOME_NAME = "UserAccessPCQSHome";
    public UserAccessPCQS find();
    public UserAccessPCQS create();
    public String objectToString();
}
