// -----------------------------------------------------------------------------------
// Source file: CancelReasonFormatStrategy.java
//
// PACKAGE: com.cboe.interfaces.presentation.common.formatters;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.common.formatters;


/**
 * Defines a contract for a class that formats CancelReasons
 */
public interface CancelReasonFormatStrategy extends FormatStrategy
{
    public static final String FULL_CANCEL_REASON = "Full Cancel Reason";

    public static final String FULL_CANCEL_REASON_DESCRIPTION = "Full Cancel Reason Name";

    /**
     * Defines a method for formatting Cancel Reason using default style
     * @param short cancelReason to format
     * @return formatted string
     */
    public String format(short cancelReason);

    /**
     * Defines a method for formatting Cancel Reason
     * @param short cancelReason to format
     * @param format style to use
     * @return formatted string
     */
    public String format(short cancelReason, String style);

}
