package com.cboe.infra.presentation.network;
// -----------------------------------------------------------------------------------
// Source file: SBTNetworkConnectionLiveNode
//
// PACKAGE: com.cboe.infra.presentation.network
// 
// Created: Apr 26, 2006 11:36:51 AM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
import java.util.*;
import java.beans.PropertyChangeListener;

import com.cboe.utils.monitoringService.TransportPSNetworkConnection;

public class SBTNetworkConnectionLiveNode extends SBTLiveNode
{
    TransportPSNetworkConnection networkConnection;

    public SBTNetworkConnectionLiveNode(TransportPSNetworkConnection networkConnection)
    {
        super(networkConnection.getNetworkConnectionName(), SBTNodeType.NETWORK_CONNECTION);
        this.networkConnection = networkConnection;
    }

    /* (non-Javadoc)
    * @see com.cboe.interfaces.presentation.processes.CBOEProcess#getDisplayName()
    */
    public String getDisplayName()
    {
        if (networkConnection != null)
        {
            return networkConnection.getDisplayName();
        }
        
        return super.getName();
    }

    public boolean isAlive()
    {
        if (networkConnection != null)
        {
            return networkConnection.getIsAlive();
        }
        
        return false;
    }

    public Date getUpdateTime(String propertyName)
    {
        if (networkConnection != null)
        {
            return networkConnection.getTimeUpdated(propertyName);
        }
        
        return new Date();
    }

    public int hashCode()
    {
        if (networkConnection != null)
        {
            return networkConnection.hashCode();
        }
        
        return super.hashCode();
    }

    public boolean equals(Object o)
    {
        if ((o != null)  && (o instanceof SBTNetworkConnectionLiveNode))
        {
            SBTNetworkConnectionLiveNode tmpNC = (SBTNetworkConnectionLiveNode) o;
            return networkConnection.equals(tmpNC.networkConnection); 
        }
        
        return false; 
    }

    public Date getLastUpdateTime()
    {
        if (networkConnection != null)
        {
            return networkConnection.getTimeUpdated();
        }
        
        return new Date();
    }

    public boolean isQueued()
    {
        if (networkConnection != null)
        {
            return networkConnection.getBufferStats().getMsgQueueCount() > 0;
        }
        
        return false;
    }

    public void addPropertyChangeListener(PropertyChangeListener l)
    {
        if (networkConnection != null)
        {
            networkConnection.addPropertyChangeListener(l);
        }
    }

    public void removePropertyChangeListener(PropertyChangeListener l)
    {
        if (networkConnection != null)
        {
            networkConnection.removePropertyChangeListener(l);
        }
    }

    public long getServerWriteBufferSize()
    {
        if (networkConnection != null)
        {
            serverWriteBufferSize =  networkConnection.getBufferStats().getWriteBufferCount();
        }
        
        return serverWriteBufferSize;
    }

    public long getServerReadBufferSize()
    {
        if (networkConnection != null)
        {
            serverReadBufferSize =  networkConnection.getBufferStats().getWriteBufferCount();
        }
        
        return serverReadBufferSize;
    }

    public long getServerQueuedMsgCount()
    {
        if (networkConnection != null)
        {
            serverQueuedMsgCount = networkConnection.getBufferStats().getMsgQueueCount();
        }
        
        return serverQueuedMsgCount;
    }

    public long getServerQueuedBytesCount()
    {
        if (networkConnection != null)
        {
            serverQueuedBytesCount = networkConnection.getBufferStats().getMsgQueueByteCount();
        }
        
        return serverQueuedBytesCount;
    }

    public long getServerDiscardCount()
    {
        if (networkConnection != null)
        {
            serverDiscardCount = networkConnection.getBufferStats().getDiscardedCount();
        }
        
        return serverDiscardCount;
    }

    public Date getServerBufferUpdateTime()
    {
        if (networkConnection != null)
        {
            return networkConnection.getBufferStats().getTimeUpdated();
        }
        
        return new Date();
    }
}
