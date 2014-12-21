// -----------------------------------------------------------------------------------
// Source file: OperationResultFormatter
//
// PACKAGE: com.cboe.presentation.common.formatters
// 
// Created: Jan 24, 2005 10:50:43 AM
// -----------------------------------------------------------------------------------
// Copyright (c) 2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.formatters;

import com.cboe.interfaces.presentation.common.formatters.OperationResultFormatStrategy;
import com.cboe.idl.cmiUtil.OperationResultStruct;

public class OperationResultFormatter extends Formatter implements OperationResultFormatStrategy
{
    public OperationResultFormatter()
    {
        super();

        addStyle(FULL_OPERATION_RESULT, FULL_OPERATION_RESULT_DESCRIPTION);
        addStyle(MESSAGE_ONLY_OPERATION_RESULT, MESSAGE_ONLY_OPERATION_RESULT_DESCRIPTION);

        setDefaultStyle(FULL_OPERATION_RESULT);
    }

    /**
     * Defines a method for formatting OperationResultStruct
     *
     * @param result to format
     * @return formatted string
     */
    public String format(OperationResultStruct result)
    {
        return format(result, getDefaultStyle());
    }

    /**
     * Defines a method for formatting OperationResultStruct
     *
     * @param result to format
     * @param style  of format to use
     * @return formatted string
     */
    public String format(OperationResultStruct result, String style)
    {
        validateStyle(style);
        StringBuffer text = new StringBuffer(80);

        if (style.equalsIgnoreCase(FULL_OPERATION_RESULT))
        {
            text.append(result.errorMessage);
            text.append(" ErrorCode: ");
            text.append(result.errorCode);
        }
        else if (style.equalsIgnoreCase(MESSAGE_ONLY_OPERATION_RESULT))
        {
            text.append(result.errorMessage);
        }

        return text.toString();
    }
}
