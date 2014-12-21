package com.cboe.interfaces.application;

public interface AppServerStatusManager
{
    public String getProcessName();
    public boolean isSystemReady();
    public boolean isBusinessDayStarted();
}
