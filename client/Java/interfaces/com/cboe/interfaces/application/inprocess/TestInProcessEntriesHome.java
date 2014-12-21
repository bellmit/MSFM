package com.cboe.interfaces.application.inprocess;

import com.cboe.interfaces.application.StatusMonitor;

/**
 * @author Jing Chen
 */
public interface TestInProcessEntriesHome
{
        /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "TestInProcessEntriesHome";

    public TestInProcessEntries create();
}
