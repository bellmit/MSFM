//
// -----------------------------------------------------------------------------------
// Source file: TimedOutException.java
//
// PACKAGE: com.cboe.interfaces.presentation.api
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.api;

public class TimedOutException extends Exception
{
    private int millisOfTimeElasped;

    public TimedOutException(int millisOfTimeElasped)
    {
        super();
        this.millisOfTimeElasped = millisOfTimeElasped;
    }

    public TimedOutException(String message, int millisOfTimeElasped)
    {
        super(message);
        this.millisOfTimeElasped = millisOfTimeElasped;
    }

    public int getMillisOfTimeElasped()
    {
        return millisOfTimeElasped;
    }
}
