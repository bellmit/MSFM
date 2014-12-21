package com.cboe.delegates.floorApplication;

import com.cboe.interfaces.floorApplication.ManualReportingService;

/**
 * Author: mahoney
 * Date: Jul 18, 2007
 */
public class ManualReportingServiceDelegate extends com.cboe.idl.floorApplication.POA_ManualReportingService_tie
{
    public ManualReportingServiceDelegate(ManualReportingService delegate)
    {
        super(delegate);
    }
}
