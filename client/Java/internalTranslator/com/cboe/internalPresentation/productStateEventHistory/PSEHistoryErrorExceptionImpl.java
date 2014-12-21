package com.cboe.internalPresentation.productStateEventHistory;
// -----------------------------------------------------------------------------------
// Source file: PSEHistoryErrorExceptionImpl
//
// PACKAGE: com.cboe.internalPresentation.productStateEventHistory
// 
// Created: Mar 16, 2006 11:48:23 AM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
import com.cboe.interfaces.presentation.common.formatters.ExceptionFormatStrategy;

import com.cboe.presentation.common.formatters.CommonFormatFactory;

public class PSEHistoryErrorExceptionImpl implements PSEventHistoryError
{
    private Exception exception;
    private String message;

    PSEHistoryErrorExceptionImpl(Exception exception)
    {
        this.exception = exception;
    }

    public String getMessage()
    {
        if (message == null)
        {
            message = buildMessage();
        }
        
        return message;  
    }

    public String toString()
    {
        return getMessage();
    }

    private String buildMessage()
    {
        ExceptionFormatStrategy strategy = CommonFormatFactory.getExceptionFormatStrategy();
        
        StringBuilder builder = new StringBuilder();
        builder.append(strategy.format(exception, ExceptionFormatStrategy.DETAIL_MESSAGE));
        
        return builder.toString();
    }
}
