//
// ------------------------------------------------------------------------
// FILE: AlarmActivationServiceConsumer.java
// 
// PACKAGE: com.cboe.interfaces.events
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2004 The Chicago Board Options Exchange.  All Rights Reserved.
// ------------------------------------------------------------------------
//
package com.cboe.interfaces.events;

import com.cboe.idl.alarmService.AlarmActivationServiceOperations;

/**
 * Listen for Alarm Activation Service events.
 * ICS will consume these events.
 */
public interface AlarmActivationServiceConsumer
        extends AlarmActivationServiceOperations
{

}
