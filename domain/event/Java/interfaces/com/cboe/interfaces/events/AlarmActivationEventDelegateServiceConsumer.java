//
// ------------------------------------------------------------------------
// FILE: AlarmActivationEventDelegateServiceConsumer.java
// 
// PACKAGE: com.cboe.interfaces.events
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2004 The Chicago Board Options Exchange.  All Rights Reserved.
// ------------------------------------------------------------------------
//
package com.cboe.interfaces.events;

import com.cboe.idl.alarmEvents.AlarmActivationEventService;

/**
 * Provides an interface of AlarmDefinitionServiceConsumer, but with a settable service delegate.
 */
public interface AlarmActivationEventDelegateServiceConsumer
        extends AlarmActivationServiceConsumer
{
    public void setAlarmActivationEventServiceDelegate(AlarmActivationEventService service);
}
