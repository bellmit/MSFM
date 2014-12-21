/**
 * Copyright 2000-2002 (C) Chicago Board Options Exchange
 * Package: com.cboe.intermarketPresentation.intermarketMessages
 * User: torresl
 * Date: Jan 2, 2003 10:27:23 AM
 */
package com.cboe.intermarketPresentation.intermarketMessages;

import com.cboe.interfaces.intermarketPresentation.intermarketMessages.HeldOrderFilledReport;
import com.cboe.idl.cmiIntermarketMessages.HeldOrderFilledReportStruct;

public class HeldOrderFilledReportFactory
{
    public static HeldOrderFilledReport createHeldOrderFilledReport(HeldOrderFilledReportStruct heldOrderFilledReportStruct)
    {
        return new HeldOrderFilledReportImpl(heldOrderFilledReportStruct);
    }
}