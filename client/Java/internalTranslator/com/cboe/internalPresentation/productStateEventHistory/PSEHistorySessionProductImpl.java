package com.cboe.internalPresentation.productStateEventHistory;
// -----------------------------------------------------------------------------------
// Source file: PSEHistorySessionProductImpl
//
// PACKAGE: com.cboe.internalPresentation.productStateEventHistory
// 
// Created: Mar 13, 2006 2:33:33 PM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
import com.cboe.interfaces.presentation.product.SessionProduct;
import com.cboe.interfaces.domain.dateTime.DateTime;

public class PSEHistorySessionProductImpl extends AbstractProductStateEventHistory
{
    private SessionProduct sp;

    PSEHistorySessionProductImpl(SessionProduct sp, DateTime dateTime, PSEventHistoryStatus status, String description)
    {
        super(dateTime, status, description);
        this.sp = sp;
    }

    public String getSubject()
    {
        return sp.toString();
    }
}

