// -----------------------------------------------------------------------------------
// Source file: AssociatedContextImpl.java
//
// PACKAGE: com.cboe.presentation.instrumentation
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.instrumentation;

import com.cboe.interfaces.instrumentation.AssociatedContext;
import com.cboe.presentation.common.businessModels.AbstractBusinessModel;
import com.cboe.presentation.common.formatters.InstrumentorTypes;
import com.cboe.client.xml.bind.GIContextDetailType;
import com.cboe.client.xml.bind.GIAssociatedContextType;

public class AssociatedContextImpl extends AbstractBusinessModel implements AssociatedContext
{
    private String orbName;
    private String clusterName;
    private String name;
    private String fullName;
    private short[] types;

    private AssociatedContextImpl()
    {
        super();
    }

    protected AssociatedContextImpl(String orbName, String clusterName, GIContextDetailType contextDetailType, GIAssociatedContextType associatedContextType)
    {
        this();
        this.orbName = orbName;
        this.clusterName = clusterName;
        setData(contextDetailType, associatedContextType);
    }

    private void setData(GIContextDetailType contextDetailType, GIAssociatedContextType associatedContextType)
    {
        this.name = associatedContextType.getName();
        this.fullName = associatedContextType.getFullName();
        types = new short[1];
        types[0] = InstrumentorTypes.getValue(associatedContextType.getInstrumentor());
    }

    /**
     * Returns an array of available instrumentor types for this context.
     * @return types short[]
     */
    public short[] getAvailableInstrumentorTypes()
    {
        return this.types;
    }

    /**
     * Returns full name of this context.
     * @return full name String
     */
    public String getFullName()
    {
        return this.fullName;
    }

    /**
     * Returns name of this context.
     * @return name String
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Returns orb name for this context.
     * @return orb name String
     */
    public String getOrbName()
    {
        return this.orbName;
    }

    /**
     * Returns cluster name for this context.
     * @return cluster name String
     */
    public String getClusterName()
    {
        return clusterName;
    }
}
