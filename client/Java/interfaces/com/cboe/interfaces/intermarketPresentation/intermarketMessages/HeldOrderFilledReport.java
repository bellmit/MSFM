package com.cboe.interfaces.intermarketPresentation.intermarketMessages;

import com.cboe.idl.cmiIntermarketMessages.HeldOrderFilledReportStruct;

import com.cboe.interfaces.presentation.order.FilledReport;

/**
 * Copyright 2000-2002 (C) Chicago Board Options Exchange
 * Package: com.cboe.interfaces.intermarketPresentation.intermarketMessages
 * User: torresl
 * Date: Dec 31, 2002 1:51:26 PM
 */
public interface HeldOrderFilledReport
{
    /**
     * @deprecated
     */
    HeldOrderFilledReportStruct toStruct();

    HeldOrderDetail getHeldOrderDetail();
    FilledReport[] getFilledReports();
}
