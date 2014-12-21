package com.cboe.delegates.application;

import com.cboe.interfaces.application.ActivityService;

public class ActivityServiceDelegate extends com.cboe.idl.activity.POA_ActivityService_tie
{
    public ActivityServiceDelegate(ActivityService delegate)
    {
        super(delegate);
    }
}
