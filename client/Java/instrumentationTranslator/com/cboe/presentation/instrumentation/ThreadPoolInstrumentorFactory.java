//
// ------------------------------------------------------------------------
// FILE: ThreadPoolInstrumentorFactory.java
//
// PACKAGE: com.cboe.presentation.instrumentation
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
//
// ------------------------------------------------------------------------
//

package com.cboe.presentation.instrumentation;

import com.cboe.interfaces.instrumentation.ThreadPoolInstrumentor;
import com.cboe.client.xml.bind.GIThreadInstrumentorType;
import com.cboe.client.xml.bind.GIContextDetailType;

public class ThreadPoolInstrumentorFactory
{
    public static ThreadPoolInstrumentor[] createThreadPoolInstrumentors(String orbName, String clusterName, GIContextDetailType contextDetailType)
    {
        ThreadPoolInstrumentor[] threadInstrumentors = new ThreadPoolInstrumentor[contextDetailType.getThreadInstrumentorsLength()];
        GIThreadInstrumentorType[] instrumentorTypes = contextDetailType.getThreadInstrumentors();
        for (int i = 0; i < instrumentorTypes.length; i++)
        {
            threadInstrumentors[i] = createThreadPoolInstrumentor(orbName, clusterName, contextDetailType, instrumentorTypes[i]);
        }
        return threadInstrumentors;

    }
    public static ThreadPoolInstrumentor createThreadPoolInstrumentor(String orbName, String clusterName, GIContextDetailType contextDetailType, GIThreadInstrumentorType threadInstrumentorType)
    {
        return new ThreadPoolInstrumentorImpl(orbName, clusterName, contextDetailType, threadInstrumentorType);
    }
}