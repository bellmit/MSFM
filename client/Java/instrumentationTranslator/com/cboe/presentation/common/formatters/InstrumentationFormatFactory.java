//
// ------------------------------------------------------------------------
// FILE: InstrumentationFormatFactory.java
// 
// PACKAGE: com.cboe.presentation.common.formatters
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2005 The Chicago Board Options Exchange.  All Rights Reserved.
// ------------------------------------------------------------------------
//

package com.cboe.presentation.common.formatters;

import com.cboe.interfaces.instrumentation.common.formatters.SeverityFormatStrategy;
import com.cboe.interfaces.instrumentation.common.formatters.NotificationTypesFormatStrategy;
import com.cboe.interfaces.instrumentation.common.formatters.ConditionTypesFormatStrategy;
import com.cboe.interfaces.instrumentation.common.formatters.OperatorFormatStrategy;
import com.cboe.interfaces.instrumentation.common.formatters.WatchdogLimitTypeFormatStrategy;
import com.cboe.interfaces.instrumentation.common.formatters.WatchdogStateFormatStrategy;
import com.cboe.interfaces.instrumentation.common.formatters.UserDataFormatStrategy;

public abstract class InstrumentationFormatFactory
{
    protected static SeverityFormatStrategy severityFormatStrategy;
    protected static NotificationTypesFormatStrategy notificationTypesFormatStrategy;
    protected static ConditionTypesFormatStrategy conditionTypesFormatStrategy;
    protected static OperatorFormatStrategy operatorFormatStrategy;
    protected static WatchdogLimitTypeFormatStrategy watchdogLimitTypeFormatStrategy;
    protected static WatchdogStateFormatStrategy watchdogStateFormatStrategy;
    protected static UserDataFormatStrategy userDataFormatStrategy;

    public static WatchdogLimitTypeFormatStrategy getWatchdogLimitTypeFormatStrategy()
    {
        if(watchdogLimitTypeFormatStrategy == null)
        {
            watchdogLimitTypeFormatStrategy = new WatchdogLimitTypeFormatter();
        }
        return watchdogLimitTypeFormatStrategy;
    }

    public static WatchdogStateFormatStrategy getWatchdogStateFormatStrategy()
    {
        if(watchdogStateFormatStrategy == null)
        {
            watchdogStateFormatStrategy = new WatchdogStateFormatter();
        }
        return watchdogStateFormatStrategy;
    }

    public static SeverityFormatStrategy getSeverityFormatStrategy()
    {
        if(severityFormatStrategy == null)
        {
            severityFormatStrategy = new SeverityFormatter();
        }
        return severityFormatStrategy;
    }

    public static NotificationTypesFormatStrategy getNotificationTypesFormatStrategy()
    {
        if (notificationTypesFormatStrategy == null)
        {
            notificationTypesFormatStrategy = new NotificationTypesFormatter();
        }
        return notificationTypesFormatStrategy;
    }

    public static ConditionTypesFormatStrategy getConditionTypesFormatStrategy()
    {
        if (conditionTypesFormatStrategy == null)
        {
            conditionTypesFormatStrategy = new ConditionTypesFormatter();
        }
        return conditionTypesFormatStrategy;
    }

    public static OperatorFormatStrategy getOperatorFormatStrategy()
    {
        if(operatorFormatStrategy == null)
        {
            operatorFormatStrategy = new OperatorFormatter();
        }
        return operatorFormatStrategy;
    }

    public static UserDataFormatStrategy getUserDataFormatStrategy()
    {
        if (userDataFormatStrategy == null)
        {
            userDataFormatStrategy = new UserDataFormatter();
        }
        return userDataFormatStrategy;
    }

}
