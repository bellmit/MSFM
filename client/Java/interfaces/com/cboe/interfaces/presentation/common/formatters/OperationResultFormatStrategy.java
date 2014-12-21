// -----------------------------------------------------------------------------------
// Source file: OperationResultFormatStrategy
//
// PACKAGE: com.cboe.interfaces.presentation.common.formatters
// 
// Created: Jan 24, 2005 10:41:36 AM
// -----------------------------------------------------------------------------------
// Copyright (c) 2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

package com.cboe.interfaces.presentation.common.formatters;

import com.cboe.idl.cmiUtil.OperationResultStruct;

public interface OperationResultFormatStrategy extends FormatStrategy
{
    public static final String FULL_OPERATION_RESULT = "Full Operation Result";
    public static final String FULL_OPERATION_RESULT_DESCRIPTION = "Full Operation Result Desription";

    public static final String MESSAGE_ONLY_OPERATION_RESULT = "Message Only";
    public static final String MESSAGE_ONLY_OPERATION_RESULT_DESCRIPTION = "Message Only Desription.";

    /**
     * Defines a method for formatting OperationResultStruct
     * @param result to format
     * @return formatted string
     */
    public String format(OperationResultStruct result);

    /**
     * Defines a method for formatting OperationResultStruct
     * @param result to format
     * @param style of format to use
     * @return formatted string
     */
    public String format(OperationResultStruct result, String style);
}
