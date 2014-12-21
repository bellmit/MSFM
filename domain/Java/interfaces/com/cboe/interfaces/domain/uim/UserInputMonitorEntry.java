//
// -----------------------------------------------------------------------------------
// Source file: UserInputMonitorEntry.java
//
// PACKAGE: com.cboe.interfaces.domain.uim;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.domain.uim;

public interface UserInputMonitorEntry
{
    String getSessionName();
    int getWindowInterval();

    void setSessionName(String sessionName);
    void setWindowInterval(int windowInterval);
}