package com.cboe.internalPresentation.productStateEventHistory;

// -----------------------------------------------------------------------------------
// Source file: PSEHistoryTradingSessionModelImpl
//
// PACKAGE: com.cboe.internalPresentation.productStateEventHistory
// 
// Created: Mar 14, 2006 11:20:40 AM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
import com.cboe.interfaces.internalPresentation.tradingSession.TradingSessionModel;
import com.cboe.interfaces.domain.dateTime.DateTime;

public class PSEHistoryTradingSessionModelImpl extends AbstractProductStateEventHistory
{
    private TradingSessionModel model;

    PSEHistoryTradingSessionModelImpl(TradingSessionModel model, DateTime dateTime, PSEventHistoryStatus status, String description)
    {
        super(dateTime, status, description);
        this.model = model;
    }

    public String getSubject()
    {
        return model.toString();
    }
}
