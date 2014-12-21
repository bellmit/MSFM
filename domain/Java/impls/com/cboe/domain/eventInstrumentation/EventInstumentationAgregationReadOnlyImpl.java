package com.cboe.domain.eventInstrumentation;

import com.cboe.interfaces.domain.eventInstrumentation.EventInstrumentation;

/**
 * This class is used strictly to combine any number of individual eventInsturmentors into a single
 * instrumentor with one name.
 * 
 * @author dowat
 *
 */
public class EventInstumentationAgregationReadOnlyImpl implements EventInstrumentation
{
    private EventInstrumentation[] instrumentors;
    private String name;
    
    public EventInstumentationAgregationReadOnlyImpl(String name, EventInstrumentation[] instrumentors) 
    {
        this.name = name;
        this.instrumentors = instrumentors;
    }

    public long getCount()
    {
        long count = 0;
        for(EventInstrumentation instrumentation:this.instrumentors)
        {
            count += instrumentation.getCount();
        }
        return count;
    }

    public String getName()
    {
        return name;
    }

    public void increment()
    {
        // do nothing
    }

}
