// -----------------------------------------------------------------------------------
// Source file: MessageMonitorEnvironmentManager
//
// PACKAGE: com.cboe.infra.presentation.environment
// 
// Created: Oct 10, 2005 4:03:20 PM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.infra.presentation.environment;

import com.cboe.presentation.environment.AbstractEnvironmentManager;
import com.cboe.presentation.environment.EnvironmentProperties;

public class MessageMonitorEnvironmentManager extends AbstractEnvironmentManager
{
    public EnvironmentProperties createEnvironment(String name)
    {
        return new EnvironmentPropertiesImpl(name);
    }

    public EnvironmentProperties createEnvironment(String name, String prefix, String iorRef)
    {
        EnvironmentProperties rv = new EnvironmentPropertiesImpl(name, prefix, iorRef);
        environments.put( name, rv );
        return rv;
    }
}
