//
// ------------------------------------------------------------------------
// FILE: MethodInstrumentorFactory.java
//
// PACKAGE: com.cboe.presentation.instrumentation
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
//
// ------------------------------------------------------------------------
//

package com.cboe.presentation.instrumentation;

import com.cboe.interfaces.instrumentation.MethodInstrumentor;
import com.cboe.client.xml.bind.GIMethodInstrumentorType;
import com.cboe.client.xml.bind.GIContextDetailType;

public class MethodInstrumentorFactory
{
    public static MethodInstrumentor[] createMethodInstrumentors(String orbName, String clusterName,
                                                                 GIContextDetailType contextDetailType)
    {
        MethodInstrumentor[] methodInstrumentors = new MethodInstrumentor[contextDetailType.getMethodInstrumentorsLength()];
        GIMethodInstrumentorType[] instrumentorTypes = contextDetailType.getMethodInstrumentors();
        for (int i = 0; i < instrumentorTypes.length; i++)
        {
            methodInstrumentors[i] = createMethodInstrumentor(orbName, clusterName,
                                                              contextDetailType, instrumentorTypes[i]);
        }
        return methodInstrumentors;
    }
    public static MethodInstrumentor createMethodInstrumentor(String orbName, String clusterName,
                                                              GIContextDetailType contextDetailType,
                                                              GIMethodInstrumentorType methodInstrumentorType)
    {
        return new MethodInstrumentorImpl(orbName, clusterName,
                                          contextDetailType, methodInstrumentorType);
    }
}