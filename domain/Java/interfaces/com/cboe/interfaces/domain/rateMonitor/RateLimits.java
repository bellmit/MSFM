//
// -----------------------------------------------------------------------------------
// Source file: RateLimits.java
//
// PACKAGE: com.cboe.interfaces.domain.rateMonitor;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.domain.rateMonitor;

public interface RateLimits
{
    String getSessionName();
    short getRateMonitorType();
    int getWindowSize();
    long getWindowInterval();

    void setSessionName(String sessionName);
    void setRateMonitorType(short rateMonitorType);
    void setWindowSize(int windowSize);
    void setWindowInterval(long windowInterval);
}