package com.cboe.internalPresentation.productStateEventHistory;
// -----------------------------------------------------------------------------------
// Source file: PSEHistoryErrorGroupErrorCodeResultImpl
//
// PACKAGE: com.cboe.internalPresentation.productStateEventHistory
// 
// Created: Mar 16, 2006 2:00:47 PM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
import com.cboe.idl.product.GroupErrorCodeResultStruct;

import com.cboe.interfaces.internalPresentation.common.formatters.GroupErrorCodeResultFormatStrategy;

import com.cboe.internalPresentation.common.formatters.FormatFactory;

public class PSEHistoryErrorGroupErrorCodeResultImpl implements PSEventHistoryError
{
    private GroupErrorCodeResultStruct[] results;
    private String message;
    private String sessionName;

    public PSEHistoryErrorGroupErrorCodeResultImpl(GroupErrorCodeResultStruct[] results, String sessionName)
    {
        this.results = results;
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
        for (GroupErrorCodeResultStruct result : results)
        {
            builder.append(formatter.format(result, sessionName,
                                            GroupErrorCodeResultFormatStrategy.GROUP_ERRORCODERESULT_KEYDESCRIPTION));
            
            builder.append('\n');
        }

        return builder.toString();
    }
}
