/*
 * Created on Dec 7, 2005
 * -----------------------------------------------------------------------------------
 * Copyright (c) 2005 The Chicago Board Options Exchange. All Rights Reserved.
 *-----------------------------------------------------------------------------------
 */
package com.cboe.interfaces.events;

import com.cboe.idl.clusterInfoEvents.OrbNameAliasEventService;

public interface OrbNameAliasEventDelegateServiceConsumer extends OrbNameAliasServiceConsumer
{
    public void setOrbNameAliasEventServiceDelegate(OrbNameAliasEventService service);
}
