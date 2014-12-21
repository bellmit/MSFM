//
// -----------------------------------------------------------------------------------
// Source file: AcceptTimeDelay.java
//
// PACKAGE: com.cboe.interfaces.consumers.callback
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.consumers.callback;

/**
 * Provides a holder for common definitions for the time delay in callbacks
 */
public interface AcceptTimeDelay
{
    public static final String TIME_DELAY_PROPERTY_SECTION = "Timers";
    public static final String DELAY_ENABLED_PROPERTY_NAME = "ConsumerDelayEnabled";
    public static final String DEFAULT_CONSUMER_DELAY_PROPERTY_NAME = "DefaultConsumerDelayMillis";

    public static final String V4_CONSUMER_DELAY_FREQ_PROPERTY_NAME = "V4ConsumerDelayFrequency";
    public static final String V4_CONSUMER_DELAY_PROPERTY_NAME = "V4ConsumerDelayMillis";
}