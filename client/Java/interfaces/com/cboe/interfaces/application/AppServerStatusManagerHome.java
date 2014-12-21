package com.cboe.interfaces.application;

public interface AppServerStatusManagerHome
{
    public final static String HOME_NAME = "AppServerStatusManagerHome";
    public AppServerStatusManager create();
    public AppServerStatusManager find();
}
