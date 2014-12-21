//
// -----------------------------------------------------------------------------------
// Source file: ExceptionFormatStrategy.java
//
// PACKAGE: com.cboe.interfaces.presentation.common.formatters;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.common.formatters;

/**
 * Defines a contract for a class that formats exceptions
 */
public interface ExceptionFormatStrategy extends FormatStrategy
{
    public static final String SIMPLE_MESSAGE = "Simple Message";
    public static final String DETAIL_MESSAGE = "Detail Message";
    public static final String FULL_MESSAGE = "Full Message";
    public static final String STACK_TRACE_MESSAGE = "Stack Trace";

    public static final String SIMPLE_MESSAGE_DESCRIPTION = "Simple Message";
    public static final String DETAIL_MESSAGE_DESCRIPTION = "Detail Message";
    public static final String FULL_MESSAGE_DESCRIPTION = "Simple Message + Detail Message.";
    public static final String STACK_TRACE_MESSAGE_DESCRIPTION = "Stack Trace Only";

    /**
     * Defines a method for formatting Throwable
     * @param throwable to format
     * @return formatted string
     */
    public String format(Throwable throwable);

    /**
     * Defines a method for formatting Throwable
     * @param throwable to format
     * @param styleName to use for formatting
     * @return formatted string
     */
    public String format(Throwable throwable, String styleName);

}