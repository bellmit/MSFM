package com.cboe.internalPresentation.productStateEventHistory;
// -----------------------------------------------------------------------------------
// Source file: PSEHistoryErrorCodeResultImpl
//
// PACKAGE: com.cboe.internalPresentation.productStateEventHistory
// 
// Created: Mar 16, 2006 1:21:12 PM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
import com.cboe.idl.product.ErrorCodeResultStruct;

import com.cboe.interfaces.internalPresentation.common.formatters.GroupErrorCodeResultFormatStrategy;

import com.cboe.internalPresentation.common.formatters.FormatFactory;

public class PSEHistoryErrorCodeResultImpl implements PSEventHistoryError
{
    private ErrorCodeResultStruct[] errorCodeResult;
    private String message;
    private String sessionName;

    public PSEHistoryErrorCodeResultImpl(ErrorCodeResultStruct[] errorCodeResult, String sessionName)
    {
        this.errorCodeResult = errorCodeResult;
        this.sessionName = sessionName;
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
        GroupErrorCodeResultFormatStrategy formatter = FormatFactory.getGroupErrorCodeResultFormatStrategy();
        StringBuilder builder = new StringBuilder();
        
        for (ErrorCodeResultStruct result : errorCodeResult)
        {
            builder.append(formatter.format(result, sessionName,
                                            GroupErrorCodeResultFormatStrategy.ERRORCODERESULT_KEYDESCRIPTION));
            
            builder.append('\n');
        }

        return builder.toString();
    }
}
