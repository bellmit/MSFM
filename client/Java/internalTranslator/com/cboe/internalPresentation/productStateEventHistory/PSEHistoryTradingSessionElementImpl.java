package com.cboe.internalPresentation.productStateEventHistory;

// -----------------------------------------------------------------------------------
// Source file: PSEHistoryTradingSessionElementImpl
//
// PACKAGE: com.cboe.internalPresentation.productStateEventHistory
// 
// Created: Mar 14, 2006 11:20:54 AM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
import com.cboe.idl.session.TradingSessionElementStructV2;

import com.cboe.interfaces.domain.dateTime.DateTime;

public class PSEHistoryTradingSessionElementImpl extends AbstractProductStateEventHistory
{
    private TradingSessionElementStructV2 struct;

    PSEHistoryTradingSessionElementImpl(TradingSessionElementStructV2 model, DateTime dateTime, PSEventHistoryStatus status, String description)
    {
        super(dateTime, status, description);
        this.struct = model;
    }

    public String getSubject()
    {
        return struct.tradingSessionElementStruct.elementName;
    }
}
