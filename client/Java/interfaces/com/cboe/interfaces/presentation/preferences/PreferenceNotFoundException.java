//
// -----------------------------------------------------------------------------------
// Source file: PreferenceNotFoundException.java
//
// PACKAGE: com.cboe.presentation.preferences
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.preferences;

public class PreferenceNotFoundException extends Exception
{
    public PreferenceNotFoundException()
    {
    }

    public PreferenceNotFoundException(Throwable cause)
    {
        super(cause);
    }

    public PreferenceNotFoundException(String message)
    {
        super(message);
    }

    public PreferenceNotFoundException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
