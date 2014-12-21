package com.cboe.interfaces.application;

public interface StatusMonitorHome {
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "StatusMonitorHome";

    public StatusMonitor create();
}
