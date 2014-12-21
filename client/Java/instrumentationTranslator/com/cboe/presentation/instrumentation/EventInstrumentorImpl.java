// -----------------------------------------------------------------------------------
// Source file: EventInstrumentorImpl.java
//
// PACKAGE: com.cboe.presentation.instrumentation;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.instrumentation;

import com.cboe.interfaces.instrumentation.CalculatedEventInstrumentor;
import com.cboe.interfaces.instrumentation.CalculatedEventInstrumentorMutable;
import com.cboe.interfaces.instrumentation.InstrumentorTypes;

public class EventInstrumentorImpl extends QueueInstrumentorImpl implements CalculatedEventInstrumentorMutable
{
    protected EventInstrumentorImpl()
    {
        super();
    }

    public EventInstrumentorImpl(String orbName, String clusterName, com.cboe.instrumentationService.instrumentors.EventChannelInstrumentor eventInstrumentor)
    {
        super(orbName,clusterName,eventInstrumentor);
    }

    public EventInstrumentorImpl(String orbName, String clusterName,
                                com.cboe.instrumentationService.instrumentors.EventChannelInstrumentor eventInstrumentor,
                                com.cboe.instrumentationService.instrumentors.CalculatedEventChannelInstrumentor calculatedEventInstrumentor)
    {
        super(orbName, clusterName, eventInstrumentor, calculatedEventInstrumentor);
    }

    /**
     * Sets type of the instrumentor.
     */
    protected void setType()
    {
        this.type = InstrumentorTypes.EVENT;
    }

    public void setData(String orbName, String clusterName, com.cboe.instrumentationService.instrumentors.EventChannelInstrumentor eventInstrumentor)
    {
        super.setData(orbName, clusterName, eventInstrumentor);
    }

    public void setCalculatedData(com.cboe.instrumentationService.instrumentors.CalculatedEventChannelInstrumentor calculatedEventInstrumentor)
    {
        super.setCalculatedData(calculatedEventInstrumentor);
    }


    /**
     *  Clone all the parts of the object.  Clone is used in a very
     *  heavy use method, so not everything is cloned.
     */
    public Object clone() throws CloneNotSupportedException
    {
        EventInstrumentorImpl eventInstrumentor = new EventInstrumentorImpl();

        eventInstrumentor.userData = getUserData();
        eventInstrumentor.name = getName();
        eventInstrumentor.instrumentorKey = getInstrumentorKey();
        eventInstrumentor.orbName = getOrbName();
        eventInstrumentor.clusterName = getClusterName();
        eventInstrumentor.lastUpdatedTimeMillis = getLastUpdatedTimeMillis();

        return eventInstrumentor;
    }

    public void instrumentorPlusPlus(CalculatedEventInstrumentor eventInstrumentor)
    {
        super.instrumentorPlusPlus(eventInstrumentor);
    }

    public void setInstrumentedData(CalculatedEventInstrumentor eventInstrumentor)
    {
        super.setInstrumentedData(eventInstrumentor);
    }
}
