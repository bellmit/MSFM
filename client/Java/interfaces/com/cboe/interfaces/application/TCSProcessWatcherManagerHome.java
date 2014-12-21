package com.cboe.interfaces.application;


public interface TCSProcessWatcherManagerHome
{
    public static final String HOME_NAME = "TCSProcessWatcherManagerHome";

    TCSProcessWatcherManager create() throws Exception;
    TCSProcessWatcherManager find() throws Exception;
    String getHomeName();
}
