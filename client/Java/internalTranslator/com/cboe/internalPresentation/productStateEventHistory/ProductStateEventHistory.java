package com.cboe.internalPresentation.productStateEventHistory;
// -----------------------------------------------------------------------------------
// Source file: ProductStateEventHistory
//
// PACKAGE: com.cboe.internalPresentation.productStateEventHistory
// 
// Created: Mar 13, 2006 1:14:06 PM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
import com.cboe.interfaces.domain.dateTime.DateTime;

public interface ProductStateEventHistory
{
    public PSEventHistoryStatus getStatus();

    public DateTime getDateTime();

    public String getDesription();

    public String getSubject();
    
    public PSEventHistoryError getError();
    public void setError(PSEventHistoryError error);
}
