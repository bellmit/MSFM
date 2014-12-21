//
// -----------------------------------------------------------------------------------
// Source file: RegisteredServerImpl.java
//
// PACKAGE: com.cboe.internalPresentation.tradingSession;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.tradingSession;

import java.util.*;

import com.cboe.idl.session.TradingSessionStruct;
import com.cboe.idl.session.RegisteredServerNameDetailStruct;
import com.cboe.idl.constants.TradingSessionEventStates;

import com.cboe.interfaces.internalPresentation.tradingSession.RegisteredServer;
import com.cboe.interfaces.internalPresentation.tradingSession.TradingSessionModel;
import com.cboe.interfaces.internalPresentation.tradingSession.RegisteredServerEvent;
import com.cboe.interfaces.internalPresentation.tradingSession.RegisteredServerListener;
import com.cboe.interfaces.presentation.common.properties.KeyValueProperties;

import com.cboe.presentation.common.businessModels.AbstractBusinessModel;
import com.cboe.presentation.common.properties.KeyValuePropertiesImpl;

public class RegisteredServerImpl extends AbstractBusinessModel implements RegisteredServer
{
    public static final String UNKNOWN_LAST_EVENT = "Unknown";
    public static final short UNKNOWN_LAST_EVENT_STATE = 0; //TradingSessionEventStates.NOT_AVAILABLE;
    public static final String KEY_VALUE_FOR_AVAILABILITY = "activeState";
    public static final String KEY_VALUE_FOR_HOSTNAME = "hostName";

    private TradingSessionModel sessionModel;
    private String serverName;
    private KeyValueProperties serverDetails;
    private RegisteredServerEvent lastEvent;

    private final ArrayList listeners = new ArrayList(2);

    public RegisteredServerImpl(TradingSessionStruct sessionStruct, RegisteredServerNameDetailStruct serverStruct)
    {
        super();
        checkParam(sessionStruct, "TradingSessionStruct");
        checkParam(serverStruct, "RegisteredServerNameDetailStruct");
        sessionModel = TradingSessionModelFactory.createTradingSessionModel(sessionStruct);
        serverName = serverStruct.serverName;
        serverDetails = KeyValuePropertiesImpl.createKeyValueProperties(serverStruct.serverDetails);
    }

    public int hashCode()
    {
        return getServerName().hashCode();
    }

    public String toString()
    {
        return getServerName().toString();
    }

    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }

        boolean isEqual = super.equals(obj);
        if(!isEqual)
        {
            if(obj instanceof RegisteredServerImpl)
            {
                isEqual = getServerName().equals(((RegisteredServerImpl)obj).getServerName());
            }
        }
        return isEqual;
    }

    public void addListener(RegisteredServerListener listener)
    {
        synchronized( listeners )
        {
            if( !listeners.contains(listener) )
            {
                listeners.add(listener);
            }
        }
    }

    public void removeListener(RegisteredServerListener listener)
    {
        synchronized( listeners )
        {
            listeners.remove(listener);
        }
    }

    public String getServerName()
    {
        return serverName;
    }

    public TradingSessionModel getTradingSession()
    {
        return sessionModel;
    }

    public KeyValueProperties getServerDetails()
    {
        return serverDetails;
    }

    public boolean isAvailable()
    {
        String value = getServerDetails().getProperties().getProperty(KEY_VALUE_FOR_AVAILABILITY, "false");
        boolean isAvailable = Boolean.valueOf(value).booleanValue();
        return isAvailable;
    }

    public String getHostName()
    {
        return  getServerDetails().getProperties().getProperty(KEY_VALUE_FOR_HOSTNAME, "");
    }

    public RegisteredServerEvent getLastEvent()
    {
        return lastEvent;
    }

    public void setLastEvent(RegisteredServerEvent event)
    {
        if( event == null || getLastEvent() == null ||
                getLastEvent().getTransactionSequenceNumber() < event.getTransactionSequenceNumber())
        {
            this.lastEvent = event;
            fireLastEventUpdate();
        }
    }

    public synchronized String getLastEventName()
    {
        if( getLastEvent() != null)
        {
            return getLastEvent().getEventName();
        }
        else
        {
            return UNKNOWN_LAST_EVENT;
        }
    }

    public synchronized short getLastEventState()
    {
        if( getLastEvent() != null )
        {
            return getLastEvent().getEventState();
        }
        else
        {
            return UNKNOWN_LAST_EVENT_STATE;
        }
    }

    private void fireLastEventUpdate()
    {
        RegisteredServerListener[] listenersArray;
        synchronized( listeners )
        {
            listenersArray = new RegisteredServerListener[listeners.size()];
            listenersArray = ( RegisteredServerListener[] ) listeners.toArray(listenersArray);
        }
        if( listenersArray != null && listenersArray.length > 0 )
        {
            for( int i = 0; i < listenersArray.length; i++ )
            {
                RegisteredServerListener listener = listenersArray[i];
                listener.lastEventUpdated(this);
            }
        }
    }
}
