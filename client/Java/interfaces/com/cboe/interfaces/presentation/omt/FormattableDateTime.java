//
// -----------------------------------------------------------------------------------
// Source file: FormattableDateTime.java
//
// PACKAGE: com.cboe.interfaces.presentation.omt;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2008 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.omt;

import com.cboe.interfaces.domain.dateTime.DateTime;


/**
 * An interface that extends DateTime to allow specific formatting for display purposes
 */

public interface FormattableDateTime extends DateTime
{
    public String toString(String displayFormat);
}
