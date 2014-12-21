//
// ------------------------------------------------------------------------
// FILE: ContextDetailFactory.java
// 
// PACKAGE: com.cboe.presentation.instrumentation
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
// 
// ------------------------------------------------------------------------
//

package com.cboe.presentation.instrumentation;

import com.cboe.interfaces.instrumentation.ContextDetail;
import com.cboe.client.xml.bind.GIContextDetailType;
import com.cboe.client.xml.bind.GIContextDetailResponseType;

public class ContextDetailFactory
{
    public static ContextDetail[] createContextDetails(GIContextDetailResponseType contextDetailResponse, String rawXml)
    {
        ContextDetail[] contextDetails = new ContextDetail[contextDetailResponse.getContextDetailsLength()];
        for (int i = 0; i < contextDetails.length; i++)
        {
            contextDetails[i] = createContextDetail(
                    rawXml,
                    contextDetailResponse.getOrbName(),
                    contextDetailResponse.getClusterName(),
                    contextDetailResponse.getContextDetails(i));
        }
        return contextDetails;
    }
    public static ContextDetail createContextDetail(String rawXml, String orbName, String clusterName, GIContextDetailType contextDetail)
    {
        return new ContextDetailImpl(rawXml, orbName, clusterName, contextDetail);
    }
}