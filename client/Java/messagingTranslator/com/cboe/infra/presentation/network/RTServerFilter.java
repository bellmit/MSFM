//
// -----------------------------------------------------------------------------------
// Source file: RTServerFilter.java
//
// PACKAGE: com.cboe.infra.presentation.network
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.infra.presentation.network;

import com.cboe.infra.presentation.filter.Filter;

/**
 * Looks for nodes with a specifc RT server name
 */
public class RTServerFilter implements Filter
{
    String toMatch;

    public RTServerFilter(String serverName)
    {
        toMatch = serverName.trim();;
    }

    public boolean accept(Object o)
    {
        if ( o != null && o instanceof SBTLiveNode )
        {
            SBTLiveNode casted = (SBTLiveNode) o;
            return casted.getServerName().equalsIgnoreCase(toMatch);
        }
        return false;
    }
}
