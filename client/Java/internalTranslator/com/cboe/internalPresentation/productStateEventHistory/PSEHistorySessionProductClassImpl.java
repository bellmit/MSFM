package com.cboe.internalPresentation.productStateEventHistory;
// -----------------------------------------------------------------------------------
// Source file: PSEHistorySessionProductClassImpl
//
// PACKAGE: com.cboe.internalPresentation.productStateEventHistory
// 
// Created: Mar 13, 2006 2:33:46 PM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
import com.cboe.interfaces.presentation.product.SessionProductClass;
import com.cboe.interfaces.domain.dateTime.DateTime;

public class PSEHistorySessionProductClassImpl extends AbstractProductStateEventHistory
{
    private SessionProductClass spc;

    PSEHistorySessionProductClassImpl(SessionProductClass spc, DateTime dateTime, PSEventHistoryStatus status, String description)
    {
        super(dateTime, status, description);
        this.spc = spc;
    }

    public String getSubject()
    {
        return spc.toString();
    }
}
