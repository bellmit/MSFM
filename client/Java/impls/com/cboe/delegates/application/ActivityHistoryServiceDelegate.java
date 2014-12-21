package com.cboe.delegates.application;

import com.cboe.interfaces.internalBusinessServices.ActivityHistoryService;

public class ActivityHistoryServiceDelegate extends com.cboe.idl.internalBusinessServices.POA_ActivityHistoryService_tie
{
    public ActivityHistoryServiceDelegate(ActivityHistoryService delegate)
    {
        super(delegate);
    }
}
