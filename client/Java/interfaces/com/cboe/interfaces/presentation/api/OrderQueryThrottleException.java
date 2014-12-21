//
// -----------------------------------------------------------------------------------
// Source file: OrderQueryThrottleException.java
//
// PACKAGE: com.cboe.interfaces.presentation.api
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.api;

public class OrderQueryThrottleException extends Exception
{
    /**
     * Constructs a new exception with the specified detail message.  The cause is not initialized,
     * and may subsequently be initialized by a call to {@link #initCause}.
     * @param message the detail message. The detail message is saved for later retrieval by the
     * {@link #getMessage()} method.
     */
    public OrderQueryThrottleException(String message)
    {
        super(message);
    }
}
