//
// -----------------------------------------------------------------------------------
// Source file: TranslatorInitializer.java
//
// PACKAGE: com.cboe.presentation.api;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api;

import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;
import com.cboe.presentation.common.logging.GUILoggerHome;

/**
 * Takes care of initializing parts of the Translator after everything else is done in HomeBuilder
 */
public class TranslatorInitializer
{
    private TranslatorInitializer()
    {}

    public static void initialize()
    {
        InstrumentationTranslatorImpl api = (InstrumentationTranslatorImpl) InstrumentationTranslatorFactory.find();

        try
        {
            api.initializeInstrumentorMonitor();
            api.initializeProcessCache();
            api.initializeProductQueryService();
            api.initializeAlarmsCache();
            api.initializeSubscriptionRecoveryListener();
            api.initializeLogicalOrbNameCache();
        }
        catch(Exception e)
        {
            GUILoggerHome.find().alarm("InstrumentationTranslatorImpl could not be initialized.");
            DefaultExceptionHandlerHome.find().process(e, "InstrumentationTranslatorImpl could not be initialized.");
        }
    }
}
