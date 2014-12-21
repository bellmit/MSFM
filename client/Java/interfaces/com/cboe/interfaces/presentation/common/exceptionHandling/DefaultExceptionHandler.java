//
// -----------------------------------------------------------------------------------
// Source file: DefaultExceptionHandler.java
//
// PACKAGE: com.cboe.interfaces.presentation.common.exceptionHandling;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.common.exceptionHandling;

import org.omg.CORBA.UserException;

/*
 * Defines the contract for an exception handler
 */
public interface DefaultExceptionHandler
{
    public static final String TOTAL_FAILURE = "An exception occured while logging or displaying a message box to the user.";
    public static final String UNCAUGHT_EXCEPTION = "This application encountered an uncaught exception.";

    /**
     * Processes a standard throwable
     * @param throwable Throwable object to handle
     */
    public void process(Throwable throwable);

    /**
     * Processes a standard throwable
     * @param throwable Throwable object to handle
     * @param userMessage optional message
     */
    public void process(Throwable throwable, String userMessage);

    /**
     * Processes a standard UserException
     * @param userException to handle
     */
    public void process(UserException userException);

    /**
     * Processes a standard UserException
     * @param userException to handle
     * @param userMessage optional message
     */
    public void process(UserException userException, String userMessage);
}