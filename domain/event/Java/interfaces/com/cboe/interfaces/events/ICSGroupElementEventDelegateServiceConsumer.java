/*
 * Created on Dec 7, 2005
 * -----------------------------------------------------------------------------------
 * Copyright (c) 2005 The Chicago Board Options Exchange. All Rights Reserved.
 *-----------------------------------------------------------------------------------
 */
package com.cboe.interfaces.events;

import com.cboe.idl.icsGroupElementEvents.ICSGroupElementEventService;

public interface ICSGroupElementEventDelegateServiceConsumer extends ICSGroupElementServiceConsumer
{
    public void setGroupElementEventServiceDelegate(ICSGroupElementEventService service);
}