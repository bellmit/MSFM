//
// ------------------------------------------------------------------------
// FILE: AssociatedContextFactory.java
//
// PACKAGE: com.cboe.presentation.instrumentation
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
//
// ------------------------------------------------------------------------
//

package com.cboe.presentation.instrumentation;

import com.cboe.interfaces.instrumentation.AssociatedContext;
import com.cboe.client.xml.bind.GIContextDetailType;
import com.cboe.client.xml.bind.GIAssociatedContextType;

public class AssociatedContextFactory
{
    public static AssociatedContext[] createAssociatedContexts(String orbName, String clusterName,GIContextDetailType contextDetailType)
    {
        AssociatedContext[] associatedContexts = new AssociatedContext[contextDetailType.getAssociatedContextsLength()];
        GIAssociatedContextType[] instrumentorTypes = contextDetailType.getAssociatedContexts();
        for (int i = 0; i < instrumentorTypes.length; i++)
        {
            associatedContexts[i] = createAssociatedContext(orbName, clusterName, contextDetailType, instrumentorTypes[i]);
        }
        return associatedContexts;

    }
    public static AssociatedContext createAssociatedContext(String orbName, String clusterName, GIContextDetailType contextDetailType, GIAssociatedContextType associatedContextType)
    {
        return new AssociatedContextImpl(orbName, clusterName, contextDetailType, associatedContextType);
    }
}