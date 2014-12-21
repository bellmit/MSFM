// -----------------------------------------------------------------------------------
// Source file: InstrumentorFactory.java
//
// PACKAGE: com.cboe.presentation.instrumentation;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.instrumentation;

import com.cboe.interfaces.instrumentation.*;

import com.cboe.client.xml.bind.GIContextDetailType;
import com.cboe.client.xml.bind.GIQueueInstrumentorType;


public abstract class InstrumentorFactory
{
    /**
     * Creates an instance of MethodInstrumentor from an infra MethodInstrumentor.
     * @param methodInstrumentor infra MethodInstrumentor
     * @return MethodInstrumentor
     */
    public static MethodInstrumentor createMethodInstrumentor(String orbName, String clusterName,
                                                              com.cboe.instrumentationService.instrumentors.MethodInstrumentor methodInstrumentor)
    {
        return new MethodInstrumentorImpl(orbName, clusterName, methodInstrumentor);
    }

    /**
     * Creates an instance of CalculatedMethodInstrumentor from an infra MethodInstrumentor and CalculatedMethodInstrumentor.
     * @param methodInstrumentor infra MethodInstrumentor to wrap
     * @param calculatedMethodInstrumentor infra CalculatedMethodInstrumentor to wrap
     * @return CalculatedMethodInstrumentor
     */
    public static CalculatedMethodInstrumentor createMethodInstrumentor(String orbName, String clusterName,
                                                              com.cboe.instrumentationService.instrumentors.MethodInstrumentor methodInstrumentor,
                                                              com.cboe.instrumentationService.instrumentors.CalculatedMethodInstrumentor calculatedMethodInstrumentor)
    {
        return new MethodInstrumentorImpl(orbName, clusterName, methodInstrumentor, calculatedMethodInstrumentor);
    }

    /**
     * Creates an instance of QueueInstrumentor from an infra QueueInstrumentor.
     * @param queueInstrumentor instance of infra QueueInstrumentor
     * @return QueueInstrumentor
     */
    public static QueueInstrumentor createQueueInstrumentor(String orbName, String clusterName,
                                                            com.cboe.instrumentationService.instrumentors.QueueInstrumentor queueInstrumentor)
    {
        return new QueueInstrumentorImpl(orbName, clusterName, queueInstrumentor);
    }

    /**
     * Creates an instance of CalculatedQueueInstrumentor from an infra QueueInstrumentor and CalculatedQueueInstrumentor.
     * @param queueInstrumentor instance of infra QueueInstrumentor
     * @param calculatedQueueInstrumentor instance of infra CalculatedQueueInstrumentor
     * @return CalculatedQueueInstrumentor
     */
    public static CalculatedQueueInstrumentor createQueueInstrumentor(String orbName, String clusterName,
                                                            com.cboe.instrumentationService.instrumentors.QueueInstrumentor queueInstrumentor,
                                                            com.cboe.instrumentationService.instrumentors.CalculatedQueueInstrumentor calculatedQueueInstrumentor)
    {
        return new QueueInstrumentorImpl(orbName, clusterName, queueInstrumentor, calculatedQueueInstrumentor);
    }

    public static QueueInstrumentor[] createQueueInstrumentors(String orbName, String clusterName,
                                                               GIContextDetailType contextDetailType)
    {
        QueueInstrumentor[] queueInstrumentors = new QueueInstrumentor[contextDetailType.getQueueInstrumentorsLength()];
        GIQueueInstrumentorType[] instrumentorTypes = contextDetailType.getQueueInstrumentors();
        for( int i = 0; i < instrumentorTypes.length; i++ )
        {
            queueInstrumentors[i] = createQueueInstrumentor(orbName, clusterName,
                                                            contextDetailType, instrumentorTypes[i]);
        }
        return queueInstrumentors;
    }

    public static QueueInstrumentor createQueueInstrumentor(String orbName, String clusterName,
                                                            GIContextDetailType contextDetailType,
                                                            GIQueueInstrumentorType queueInstrumentorType)
    {
        return new QueueInstrumentorImpl(orbName, clusterName,
                                         contextDetailType, queueInstrumentorType);
    }

    /**
     * Creates an instance of HeapInstrumentor from an infra HeapInstrumentor.
     * @param heapInstrumentor to wrap in instance of HeapInstrumentor
     * @return HeapInstrumentor
     */
    public static HeapInstrumentor createHeapInstrumentor(String orbName, String clusterName,
                                                          com.cboe.instrumentationService.instrumentors.HeapInstrumentor heapInstrumentor)
    {
        return new HeapInstrumentorImpl(orbName, clusterName, heapInstrumentor);
    }

    /**
     * Creates an instance of CalculatedHeapInstrumentor from an infra HeapInstrumentor.
     * @param heapInstrumentor instance of infra HeapInstrumentor to wrap
     * @param calculatedHeapInstrumentor instance of infra CalculatedHeapInstrumentor to wrap
     * @return CalculatedHeapInstrumentor
     */
    public static CalculatedHeapInstrumentor createHeapInstrumentor(String orbName, String clusterName,
                                                          com.cboe.instrumentationService.instrumentors.HeapInstrumentor heapInstrumentor,
                                                          com.cboe.instrumentationService.instrumentors.CalculatedHeapInstrumentor calculatedHeapInstrumentor)
    {
        return new HeapInstrumentorImpl(orbName, clusterName, heapInstrumentor, calculatedHeapInstrumentor);
    }

    /**
     * Creates an instance of NetworkConnectionInstrumentor from an infra NetworkConnectionInstrumentor.
     * @param networkConnectionInstrumentor instance of infra NetworkConnectionInstrumentor
     * @return NetworkConnectionInstrumentor
     */
    public static NetworkConnectionInstrumentor createNetworkConnectionInstrumentor(String orbName, String clusterName,
                                                                                    com.cboe.instrumentationService.instrumentors.NetworkConnectionInstrumentor networkConnectionInstrumentor)
    {
        return new NetworkConnectionInstrumentorImpl(orbName, clusterName, networkConnectionInstrumentor);
    }

    /**
     * Creates an instance of CalculatedNetworkConnectionInstrumentor from an infra NetworkConnectionInstrumentor and CalculatedNetworkConnectionInstrumentor.
     * @param networkConnectionInstrumentor instance of infra NetworkConnectionInstrumentor
     * @param calculatedNetworkConnectionInstrumentor instance of infra CalculatedNetworkConnectionInstrumentor
     * @return CalculatedNetworkConnectionInstrumentor
     */
    public static CalculatedNetworkConnectionInstrumentor createNetworkConnectionInstrumentor(String orbName, String clusterName,
                                                                                    com.cboe.instrumentationService.instrumentors.NetworkConnectionInstrumentor networkConnectionInstrumentor,
                                                                                    com.cboe.instrumentationService.instrumentors.CalculatedNetworkConnectionInstrumentor calculatedNetworkConnectionInstrumentor)
    {
        return new NetworkConnectionInstrumentorImpl(orbName, clusterName, networkConnectionInstrumentor, calculatedNetworkConnectionInstrumentor);
    }

    /**
     * Creates an instance of ThreadPoolInstrumentor from an infra ThreadPoolInstrumentor.
     * @param threadPoolInstrumentor instance of infra ThreadPoolInstrumentor
     * @return ThreadPoolInstrumentor
     */
    public static ThreadPoolInstrumentor createThreadPoolInstrumentor(String orbName, String clusterName,
                                                                      com.cboe.instrumentationService.instrumentors.ThreadPoolInstrumentor threadPoolInstrumentor)
    {
        return new ThreadPoolInstrumentorImpl(orbName, clusterName, threadPoolInstrumentor);
    }

    /**
     * Creates an instance of CalculatedThreadPoolInstrumentor from an infra ThreadPoolInstrumentor and CalculatedThreadPoolInstrumentor.
     * @param threadPoolInstrumentor instance of infra ThreadPoolInstrumentor
     * @param calculatedThreadPoolInstrumentor instance of infra CalculatedThreadPoolInstrumentor
     * @return CalculatedThreadPoolInstrumentor
     */
    public static CalculatedThreadPoolInstrumentor createThreadPoolInstrumentor(String orbName, String clusterName,
                                                                      com.cboe.instrumentationService.instrumentors.ThreadPoolInstrumentor threadPoolInstrumentor,
                                                                      com.cboe.instrumentationService.instrumentors.CalculatedThreadPoolInstrumentor calculatedThreadPoolInstrumentor)
    {
        return new ThreadPoolInstrumentorImpl(orbName, clusterName, threadPoolInstrumentor, calculatedThreadPoolInstrumentor);
    }

    /**
     * Creates an instance of CountInstrumentor from an infra CountInstrumentor.
     * @param countInstrumentor instance of infra CountInstrumentor
     * @return CountInstrumentor
     */
    public static CountInstrumentor createCountInstrumentor(String orbName, String clusterName,
                                                                      com.cboe.instrumentationService.instrumentors.CountInstrumentor countInstrumentor)
    {
        return new CountInstrumentorImpl(orbName, clusterName, countInstrumentor);
    }

    /**
     * Creates an instance of CalculatedCountInstrumentor from an infra CountInstrumentor and CalculatedCountInstrumentor.
     * @param countInstrumentor instance of infra CountInstrumentor
     * @param calculatedCountInstrumentor instance of infra CalculatedCountInstrumentor
     * @return CalculatedCountInstrumentor
     */
    public static CalculatedCountInstrumentor createCountInstrumentor(String orbName, String clusterName,
                                                                      com.cboe.instrumentationService.instrumentors.CountInstrumentor countInstrumentor,
                                                                      com.cboe.instrumentationService.instrumentors.CalculatedCountInstrumentor calculatedCountInstrumentor)
    {
        return new CountInstrumentorImpl(orbName, clusterName, countInstrumentor, calculatedCountInstrumentor);
    }

    /**
     * Creates an instance of EventInstrumentor from an infra EventInstrumentor.
     * @param eventInstrumentor instance of infra EventInstrumentor
     * @return EventInstrumentor
     */
    public static EventInstrumentor createEventInstrumentor(String orbName, String clusterName,
                                                                      com.cboe.instrumentationService.instrumentors.EventChannelInstrumentor eventInstrumentor)
    {
        return new EventInstrumentorImpl(orbName, clusterName, eventInstrumentor);
    }

    /**
     * Creates an instance of CalculatedEventInstrumentor from an infra EventInstrumentor and CalculatedEventInstrumentor.
     * @param eventInstrumentor instance of infra EventInstrumentor
     * @param calculatedEventInstrumentor instance of infra CalculatedEventInstrumentor
     * @return CalculatedEventInstrumentor
     */
    public static CalculatedEventInstrumentor createEventInstrumentor(String orbName, String clusterName,
                                                                      com.cboe.instrumentationService.instrumentors.EventChannelInstrumentor eventInstrumentor,
                                                                      com.cboe.instrumentationService.instrumentors.CalculatedEventChannelInstrumentor calculatedEventInstrumentor)
    {
        return new EventInstrumentorImpl(orbName, clusterName, eventInstrumentor, calculatedEventInstrumentor);
    }

    /**
     * Creates an instance of EventInstrumentor from an infra EventInstrumentor.
     * @param jmxInstrumentor instance of infra EventInstrumentor
     * @return EventInstrumentor
     */
    public static JmxInstrumentorImpl createJmxInstrumentor(String orbName, String clusterName,
                                                                      com.cboe.instrumentationService.instrumentors.JmxInstrumentor jmxInstrumentor)
    {
        return new JmxInstrumentorImpl(orbName, clusterName, jmxInstrumentor);
    }

    /**
     * Creates an instance of CalculatedEventInstrumentor from an infra EventInstrumentor and CalculatedEventInstrumentor.
     * @param jmxInstrumentor instance of infra EventInstrumentor
     * @param calculatedJmxInstrumentor instance of infra CalculatedEventInstrumentor
     * @return CalculatedEventInstrumentor
     */
    public static CalculatedJmxInstrumentor createJmxInstrumentor(String orbName, String clusterName,
                                                                      com.cboe.instrumentationService.instrumentors.JmxInstrumentor jmxInstrumentor,
                                                                      com.cboe.instrumentationService.instrumentors.CalculatedJmxInstrumentor calculatedJmxInstrumentor)
    {
        return new JmxInstrumentorImpl(orbName, clusterName, jmxInstrumentor, calculatedJmxInstrumentor);
    }


    /**
     * Creates an instance of EventInstrumentor from an infra EventInstrumentor.
     * @param jstatInstrumentor instance of infra EventInstrumentor
     * @return EventInstrumentor
     */
    public static JstatInstrumentorImpl createJstatInstrumentor(String orbName, String clusterName,
                                                                      com.cboe.instrumentationService.instrumentors.JstatInstrumentor jstatInstrumentor)
    {
        return new JstatInstrumentorImpl(orbName, clusterName, jstatInstrumentor);
    }

    /**
     * Creates an instance of CalculatedEventInstrumentor from an infra EventInstrumentor and CalculatedEventInstrumentor.
     * @param jstatInstrumentor instance of infra EventInstrumentor
     * @param calculatedJstatInstrumentor instance of infra CalculatedEventInstrumentor
     * @return CalculatedEventInstrumentor
     */
    public static CalculatedJStatInstrumentor createJstatInstrumentor(String orbName, String clusterName,
                                                                      com.cboe.instrumentationService.instrumentors.JstatInstrumentor jstatInstrumentor,
                                                                      com.cboe.instrumentationService.instrumentors.CalculatedJstatInstrumentor calculatedJstatInstrumentor)
    {
        return new JstatInstrumentorImpl(orbName, clusterName, jstatInstrumentor, calculatedJstatInstrumentor);
    }
}
