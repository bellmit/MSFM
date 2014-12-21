//
// -----------------------------------------------------------------------------------
// Source file: Time.java
//
// PACKAGE: com.cboe.interfaces.presentation.par
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2011 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.par;

public interface Time
{
    /**
     * return number of hours (24 hour clock)
     */
    int getHours();

    /**
     * return number of minutes
     */
    int getMinutes();

    /**
     * Return a String representing this Time as it should be stored as a TimePreference value
     * Format: "HHMM"
     * @return
     */
    String toPrefString();
}
