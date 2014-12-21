//
// -----------------------------------------------------------------------------------
// Source file: InstrumentorCollectorEventImpl.java
//
// PACKAGE: com.cboe.presentation.collector
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.collector;

import java.util.*;

import com.cboe.interfaces.instrumentation.collector.InstrumentorCollectorEvent;

public class InstrumentorCollectorEventImpl implements InstrumentorCollectorEvent
{
    private List instrumentors;
    private String orbName;
    private String clusterName;

    public InstrumentorCollectorEventImpl(String clusterName, String orbName, List instrumentors)
    {
        super();

        if(clusterName == null)
        {
            throw new IllegalArgumentException("clusterName may not be null.");
        }
        if( orbName == null )
        {
            throw new IllegalArgumentException("orbName may not be null.");
        }
        if( instrumentors == null )
        {
            throw new IllegalArgumentException("instrumentors may not be null.");
        }

        this.clusterName = clusterName;
        this.instrumentors = instrumentors;
        this.orbName = orbName;
    }

    public boolean equals(Object otherObject)
    {
        boolean isEqual = super.equals(otherObject);

        if(!isEqual)
        {
            if(otherObject instanceof InstrumentorCollectorEvent)
            {
                InstrumentorCollectorEvent castedObject = ( InstrumentorCollectorEvent ) otherObject;

                if( getClusterName().equals(castedObject.getClusterName())  &&
                        getOrbName().equals(castedObject.getOrbName()) )
                {
                    isEqual = true;
                }
            }
        }
        return isEqual;
    }

    public int hashCode()
    {
        int result;
        result = orbName.hashCode();
        result = 29 * result + clusterName.hashCode();
        return result;
    }

    public List getInstrumentors()
    {
        return instrumentors;
    }

    public String getOrbName()
    {
        return orbName;
    }

    public String getClusterName()
    {
        return clusterName;
    }
}
