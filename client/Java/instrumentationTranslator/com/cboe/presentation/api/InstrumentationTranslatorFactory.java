//
// -----------------------------------------------------------------------------------
// Source file: InstrumentationTranslatorFactory.java
//
// PACKAGE: com.cboe.presentation.api
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api;

import com.cboe.interfaces.instrumentation.api.InstrumentationMonitorAPI;
import com.cboe.presentation.groups.ICSGroupServiceAPI;

import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;
import com.cboe.presentation.common.logging.GUILoggerHome;

public abstract class InstrumentationTranslatorFactory
{
    private static InstrumentationMonitorAPI api = null;

    public static InstrumentationMonitorAPI create()
    {
        if(api == null)
        {
            try
            {
                InstrumentationTranslatorImpl impl = new InstrumentationTranslatorImpl();
                impl.initializeServices();
                api = impl;
            }
            catch( Exception e )
            {
                GUILoggerHome.find().alarm("InstrumentationTranslatorImpl could not be created.");
                DefaultExceptionHandlerHome.find().process(e, "InstrumentationTranslatorImpl could not be created.");
            }
        }
        return api;
    }

    public static InstrumentationMonitorAPI find()
    {
        if(api == null)
        {
            throw new IllegalStateException("Create has not been called yet for: InstrumentationTranslatorFactory");
        }
        return api;
    }
    public static ICSGroupServiceAPI findGroupServiceAPI()
    {
        if(api == null)
        {
            throw new IllegalStateException("Create has not been called yet for: InstrumentationTranslatorFactory");
        }
        return (ICSGroupServiceAPI)api;
    }
}
