//
// ------------------------------------------------------------------------
// FILE: InstrumentorOutputEventPublisherImpl.java
// 
// PACKAGE: com.cboe.publishers.eventChannel
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2004 The Chicago Board Options Exchange.  All Rights Reserved.
// ------------------------------------------------------------------------
//

package com.cboe.publishers.eventChannel;

import com.cboe.idl.instrumentationService.instrumentors.CountInstrumentorStruct;
import com.cboe.idl.instrumentationService.instrumentors.HeapInstrumentorStruct;
import com.cboe.idl.instrumentationService.instrumentors.InstrumentorInfo;
import com.cboe.idl.instrumentationService.instrumentors.KeyValueInstrumentorStruct;
import com.cboe.idl.instrumentationService.instrumentors.InstrumentorOutput;
import com.cboe.idl.instrumentationService.instrumentors.InstrumentorOutputOperations;
import com.cboe.idl.instrumentationService.instrumentors.MethodInstrumentorStruct;
import com.cboe.idl.instrumentationService.instrumentors.NetworkConnectionInstrumentorStruct;
import com.cboe.idl.instrumentationService.instrumentors.QueueInstrumentorStruct;
import com.cboe.idl.instrumentationService.instrumentors.ThreadPoolInstrumentorStruct;
import org.omg.CORBA.Any;
import org.omg.CosEventComm.Disconnected;

/**
 * @author torresl@cboe.com
 */
public class InstrumentorOutputEventPublisherImpl
        implements InstrumentorOutputOperations
{
    private InstrumentorOutput instrumentorOutputEventChannel;

    public InstrumentorOutputEventPublisherImpl(InstrumentorOutput instrumentorOutputEventChannel)
    {
        super();
        this.instrumentorOutputEventChannel = instrumentorOutputEventChannel;
    }
    public void monitorQueueInstrumentor(String monitorName, String clusterName, String orbName, InstrumentorInfo[] instrumentorInfos, QueueInstrumentorStruct[] queueInstrumentorStructs)
    {
        instrumentorOutputEventChannel.monitorQueueInstrumentor(monitorName, clusterName, orbName, instrumentorInfos, queueInstrumentorStructs);
    }

    public void monitorEventChannelInstrumentor(String monitorName, String clusterName, String orbName, InstrumentorInfo[] instrumentorInfos, QueueInstrumentorStruct[] queueInstrumentorStructs)
    {
        instrumentorOutputEventChannel.monitorEventChannelInstrumentor(monitorName, clusterName, orbName, instrumentorInfos, queueInstrumentorStructs);
    }

    public void monitorThreadPoolInstrumentor(String monitorName, String clusterName, String orbName, InstrumentorInfo[] instrumentorInfos, ThreadPoolInstrumentorStruct[] threadPoolInstrumentorStructs)
    {
        instrumentorOutputEventChannel.monitorThreadPoolInstrumentor(monitorName, clusterName, orbName, instrumentorInfos, threadPoolInstrumentorStructs);
    }

    public void monitorNetworkConnectionInstrumentor(String monitorName, String clusterName, String orbName, InstrumentorInfo[] instrumentorInfos, NetworkConnectionInstrumentorStruct[] networkConnectionInstrumentorStructs)
    {
        instrumentorOutputEventChannel.monitorNetworkConnectionInstrumentor(monitorName, clusterName, orbName, instrumentorInfos, networkConnectionInstrumentorStructs);
    }

    public void monitorHeapInstrumentor(String monitorName, String clusterName, String orbName, InstrumentorInfo[] instrumentorInfos, HeapInstrumentorStruct[] heapInstrumentorStructs)
    {
        instrumentorOutputEventChannel.monitorHeapInstrumentor(monitorName, clusterName, orbName, instrumentorInfos, heapInstrumentorStructs);
    }

    public void monitorMethodInstrumentor(String monitorName, String clusterName, String orbName, InstrumentorInfo[] instrumentorInfos, MethodInstrumentorStruct[] methodInstrumentorStructs)
    {
        instrumentorOutputEventChannel.monitorMethodInstrumentor(monitorName, clusterName, orbName, instrumentorInfos, methodInstrumentorStructs);
    }

    public void monitorCountInstrumentor(String monitorName, String clusterName, String orbName, InstrumentorInfo[] instrumentorInfos, CountInstrumentorStruct[] countInstrumentorStructs)
    {
        instrumentorOutputEventChannel.monitorCountInstrumentor(monitorName, clusterName, orbName, instrumentorInfos, countInstrumentorStructs);
    }

    public void monitorKeyValueInstrumentor(String monitorName, String clusterName, String orbName, InstrumentorInfo[] instrumentorInfos, KeyValueInstrumentorStruct[] keyValueInstrumentorStructs)
    {
        instrumentorOutputEventChannel.monitorKeyValueInstrumentor(monitorName, clusterName, orbName, instrumentorInfos, keyValueInstrumentorStructs);
    }

    public org.omg.CORBA.Object get_typed_consumer()
    {
        return null;
    }

    public void push(Any any) throws Disconnected
    {

    }

    public void disconnect_push_consumer()
    {

    }
}
