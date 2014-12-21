//
// -----------------------------------------------------------------------------------
// Source file: PCQSSessionManagerHome.java
//
// PACKAGE: com.cboe.interfaces.application
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.application;

public interface PCQSSessionManagerHome
{
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "PCQSSessionManagerHome";
    public PCQSSessionManager create(SessionManager sessionManager);
}