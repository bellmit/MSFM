package com.cboe.interfaces.floorApplication;

import com.cboe.interfaces.application.SessionManager;

/**
 * Created by IntelliJ IDEA.
 * User: mahoney
 * Date: Jul 18, 2007
 * Time: 9:47:34 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ManualReportingServiceHome
{
    /**
     * Name that will be used for this home.
     */
    public final static String HOME_NAME = "ManualReportingServiceHome";

    /**
     * Creates an instance of the ManualReportingService.
     */
    public ManualReportingService create(SessionManager sessionManager);
}
