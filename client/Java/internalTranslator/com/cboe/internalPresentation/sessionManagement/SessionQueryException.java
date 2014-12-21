//
// -----------------------------------------------------------------------------------
// Source file: SessionQueryException.java
//
// PACKAGE: com.cboe.internalPresentation.sessionManagement;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.sessionManagement;

/**
 * Query Exception
 */
public class SessionQueryException extends Exception
{
       /**
     * Define a new session query exception, and the related exception if one exists.
     * 
     * @param message
     * @param cause
     */
    public SessionQueryException(String message, Throwable cause) {
        super(message, cause);
    }
}
