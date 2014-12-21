//
// -----------------------------------------------------------------------------------
// Source file: DateFormatThreadLocal.java
//
// PACKAGE: com.cboe.presentation.common.formatters
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2009 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.formatters;

import java.text.SimpleDateFormat;

/**
 * SimpleDateFormat isn't thread-safe, so this provides an instance per thread,
 * rather than having to synchronize access to a single instance.
 */
public class DateFormatThreadLocal extends ThreadLocal<SimpleDateFormat>
{
    public final String format;

    public DateFormatThreadLocal(String format)
    {
        this.format = format;
    }

    public SimpleDateFormat initialValue()
    {
        return new SimpleDateFormat(format);
    }
}
