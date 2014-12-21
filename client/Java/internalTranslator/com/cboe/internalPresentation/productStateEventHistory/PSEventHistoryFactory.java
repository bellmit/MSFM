package com.cboe.internalPresentation.productStateEventHistory;
// -----------------------------------------------------------------------------------
// Source file: PSEventHistoryFactory
//
// PACKAGE: com.cboe.internalPresentation.productStateEventHistory
// 
// Created: Mar 13, 2006 1:17:31 PM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
import com.cboe.idl.session.TradingSessionElementStructV2;
import com.cboe.idl.product.ErrorCodeResultStruct;
import com.cboe.idl.product.GroupErrorCodeResultStruct;

import com.cboe.interfaces.presentation.product.SessionProductClass;
import com.cboe.interfaces.presentation.product.SessionProduct;
import com.cboe.interfaces.internalPresentation.product.GroupModel;
import com.cboe.interfaces.internalPresentation.tradingSession.TradingSessionModel;

import com.cboe.presentation.common.dateTime.DateTimeImpl;

public class PSEventHistoryFactory
{
    private PSEventHistoryFactory() {}

    public static ProductStateEventHistory newPSCEvent(SessionProductClass spc, long time, PSEventHistoryStatus status, String description)
    {
        return new PSEHistorySessionProductClassImpl(spc, new DateTimeImpl(time), status, description);
    }

    public static ProductStateEventHistory newPSCEvent(SessionProduct sp, long time, PSEventHistoryStatus status, String description)
    {
        return new PSEHistorySessionProductImpl(sp, new DateTimeImpl(time), status, description);
    }

    public static ProductStateEventHistory newPSCEvent(TradingSessionElementStructV2 model, long time, PSEventHistoryStatus status, String description)
    {
        return new PSEHistoryTradingSessionElementImpl(model, new DateTimeImpl(time), status, description);
    }

    public static ProductStateEventHistory newPSCEvent(TradingSessionModel model, long time, PSEventHistoryStatus status, String description)
    {
        return new PSEHistoryTradingSessionModelImpl(model, new DateTimeImpl(time), status, description);
    }

    public static ProductStateEventHistory newPSCEvent(GroupModel group, long time, PSEventHistoryStatus status, String description)
    {
        return new PSEHistoryGroupImpl(group, new DateTimeImpl(time), status, description);
    }
    
    public static PSEventHistoryError mewPSCEventError(Exception exception)
    {
        return new PSEHistoryErrorExceptionImpl(exception);
    }

    public static PSEventHistoryError mewPSCEventError(ErrorCodeResultStruct error, String sessionName)
    {
        ErrorCodeResultStruct[] errors = new ErrorCodeResultStruct[1];
        errors[0] = error;
        
        return new PSEHistoryErrorCodeResultImpl(errors, sessionName);
    }

    public static PSEventHistoryError mewPSCEventError(ErrorCodeResultStruct[] errors, String sessionName)
    {
        return new PSEHistoryErrorCodeResultImpl(errors, sessionName);
    }

    public static PSEventHistoryError mewPSCEventError(GroupErrorCodeResultStruct[] errors, String sessionName)
    {
        return new PSEHistoryErrorGroupErrorCodeResultImpl(errors, sessionName);
    }
}
