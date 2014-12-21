//
// ------------------------------------------------------------------------
// FILE: AlarmNotificationExpirationImpl.java
// 
// PACKAGE: com.cboe.presentation.api
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2004 The Chicago Board Options Exchange.  All Rights Reserved.
// ------------------------------------------------------------------------
//

package com.cboe.presentation.api;

import com.cboe.interfaces.instrumentation.alarms.AlarmNotificationExpiration;
import com.cboe.interfaces.presentation.common.memory.MemoryUsage;

/**
 * @author torresl@cboe.com
 */
public class AlarmNotificationExpirationImpl 
        implements AlarmNotificationExpiration
{
    private MemoryUsage memoryUsage;
    private int totalNotificationCount;
    private int expiredNotificationCount;
    public AlarmNotificationExpirationImpl(
            MemoryUsage memoryUsage, 
            int totalNotificationCount,
            int expiredNotificationCount)
    {
        this.memoryUsage = memoryUsage;
        this.totalNotificationCount = totalNotificationCount;
        this.expiredNotificationCount = expiredNotificationCount;
    }

    public MemoryUsage getMemoryUsage()
    {
        return memoryUsage;
    }

    public int getTotalNotificationCount()
    {
        return totalNotificationCount;
    }

    public int getExpiredNotificationCount()
    {
        return expiredNotificationCount;
    }
}
