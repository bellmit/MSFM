//
// ------------------------------------------------------------------------
// FILE: InstrumentorOutputEventConsumerImpl.java
// 
// PACKAGE: com.cboe.consumers.eventChannel
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2004 The Chicago Board Options Exchange.  All Rights Reserved.
// ------------------------------------------------------------------------
//

package com.cboe.consumers.eventChannel;

import com.cboe.idl.instrumentationService.instrumentors.CountInstrumentorStruct;
import com.cboe.idl.instrumentationService.instrumentors.HeapInstrumentorStruct;
import com.cboe.idl.instrumentationService.instrumentors.InstrumentorInfo;
import com.cboe.idl.instrumentationService.instrumentors.KeyValueInstrumentorStruct;
import com.cboe.idl.instrumentationService.instrumentors.InstrumentorOutputPOA;
import com.cboe.idl.instrumentationService.instrumentors.MethodInstrumentorStruct;
import com.cboe.idl.instrumentationService.instrumentors.NetworkConnectionInstrumentorStruct;
import com.cboe.idl.instrumentationService.instrumentors.QueueInstrumentorStruct;
import com.cboe.idl.instrumentationService.instrumentors.ThreadPoolInstrumentorStruct;
import com.cboe.interfaces.events.InstrumentationEventConsumer;

/**
 * @author torresl@cboe.com
 */
public class InstrumentorOutputEventConsumerImpl
        extends InstrumentorOutputPOA
{

    protected InstrumentationEventConsumer delegate;

    public InstrumentorOutputEventConsumerImpl(InstrumentationEventConsumer delegate)
    {
        super();
        this.delegate = delegate;
    }

    public void monitorQueueInstrumentor(String monitorName, String clusterName, String orbName, InstrumentorInfo[] instrumentorInfos, QueueInstrumentorStruct[] queueInstrumentorStructs)
    {
        delegate.monitorQueueInstrumentor(monitorName, clusterName, orbName, instrumentorInfos, queueInstrumentorStructs);
    }

    public void monitorEventChannelInstrumentor(String monitorName, String clusterName, String orbName, InstrumentorInfo[] instrumentorInfos, QueueInstrumentorStruct[] queueInstrumentorStructs)
    {
        delegate.monitorEventChannelInstrumentor(monitorName, clusterName, orbName, instrumentorInfos, queueInstrumentorStructs);
    }

    public void monitorThreadPoolInstrumentor(String monitorName, String clusterName, String orbName, InstrumentorInfo[] instrumentorInfos, ThreadPoolInstrumentorStruct[] threadPoolInstrumentorStructs)
    {
        delegate.monitorThreadPoolInstrumentor(monitorName, clusterName, orbName, instrumentorInfos, threadPoolInstrumentorStructs);
    }

    public void monitorNetworkConnectionInstrumentor(String monitorName, String clusterName, String orbName, InstrumentorInfo[] instrumentorInfos, NetworkConnectionInstrumentorStruct[] networkConnectionInstrumentorStructs)
    {
        delegate.monitorNetworkConnectionInstrumentor(monitorName, clusterName, orbName, instrumentorInfos, networkConnectionInstrumentorStructs);
    }

    public void monitorHeapInstrumentor(String monitorName, String clusterName, String orbName, InstrumentorInfo[] instrumentorInfos, HeapInstrumentorStruct[] heapInstrumentorStructs)
    {
        delegate.monitorHeapInstrumentor(monitorName, clusterName, orbName, instrumentorInfos, heapInstrumentorStructs);
    }

    public void monitorMethodInstrumentor(String monitorName, String clusterName, String orbName, InstrumentorInfo[] instrumentorInfos, MethodInstrumentorStruct[] methodInstrumentorStructs)
    {
        delegate.monitorMethodInstrumentor(monitorName, clusterName, orbName, instrumentorInfos, methodInstrumentorStructs);
    }

    public void monitorCountInstrumentor(String monitorName, String clusterName, String orbName, InstrumentorInfo[] instrumentorInfos, CountInstrumentorStruct[] countInstrumentorStructs)
    {
        delegate.monitorCountInstrumentor(monitorName, clusterName, orbName, instrumentorInfos, countInstrumentorStructs);
    }

    public void monitorKeyValueInstrumentor(String monitorName, String clusterName, String orbName, InstrumentorInfo[] instrumentorInfos, KeyValueInstrumentorStruct[] keyValueInstrumentorStructs)
    {
        delegate.monitorKeyValueInstrumentor(monitorName, clusterName, orbName, instrumentorInfos, keyValueInstrumentorStructs);
    }

    public org.omg.CORBA.Object get_typed_consumer()
    {
        return null;
    }

    public void push(org.omg.CORBA.Any any) throws org.omg.CosEventComm.Disconnected
    {
    }

    public void disconnect_push_consumer()
    {
    }

}
