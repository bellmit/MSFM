//
// -----------------------------------------------------------------------------------
// Source file: TradingSessionEventImpl.java
//
// PACKAGE: com.cboe.internalPresentation.tradingSession;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.tradingSession;

import java.util.Hashtable;

import com.cboe.idl.constants.TradingSessionEventStates;
import com.cboe.idl.session.TradingSessionEventDescriptionStruct;
import com.cboe.idl.session.TradingSessionEventHistoryStructV2;
import com.cboe.idl.session.TradingSessionServerEventStateStruct;

import com.cboe.interfaces.domain.dateTime.DateTime;
import com.cboe.interfaces.internalPresentation.tradingSession.TradingSessionEvent;
import com.cboe.interfaces.internalPresentation.tradingSession.RegisteredServerEvent;

import com.cboe.presentation.common.dateTime.DateTimeImpl;

import com.cboe.internalPresentation.common.comparators.TradingSessionEventReverseDateComparator;
import com.cboe.internalPresentation.common.comparators.RegisteredServerEventReverseDateComparator;

public class TradingSessionEventImpl implements TradingSessionEvent
{
    private short eventType;
    private String eventName;
    private short eventState = TradingSessionEventStates.NOT_AVAILABLE;
    private String tradingSessionName;
    private DateTime dateTime;
    private String contextString;
    private String eventGroup;
    private int historyKey;
    private RegisteredServerEventCollection serverEvents;

    private TradingSessionEventReverseDateComparator comparator;

    private TradingSessionEventDescriptionStruct descriptionStruct;
    private TradingSessionEventHistoryStructV2 eventStruct;

    protected TradingSessionEventImpl()
    {
        super();
        comparator = new TradingSessionEventReverseDateComparator();
        serverEvents = new RegisteredServerEventCollection(new RegisteredServerEventReverseDateComparator());
    }

    public TradingSessionEventImpl(TradingSessionEventDescriptionStruct struct)
    {
        this();
        setTradingSessionEventDescriptionStruct(struct);
    }

    public TradingSessionEventImpl(TradingSessionEventHistoryStructV2 struct)
    {
        this();
        setTradingSessionEventHistoryStruct(struct);
    }

    /**
     * This method uses the TradingSessionEventReverseDateComparator
     */
    public int compareTo(Object o)
    {
        int result = comparator.compare(this, o);
        return result;
    }

    /**
     * This method overrides implementation of <code>equal()</code> method for this class to
     * use this class <code>compareTo()<\code> method.
     * @param o Object
     * @return boolean
     */
    public boolean equals(Object o)
    {
        if (o == null)
        {
            return false;
        }

        boolean isEqual = super.equals(o);
                                                           
        if(!isEqual)
        {
            if(o instanceof TradingSessionEvent )
            {
                TradingSessionEvent castedObj = ( TradingSessionEvent ) o;
                if(getEventName().equals(castedObj.getEventName()) &&
                        getEventState() == castedObj.getEventState() &&
                        getEventType() == castedObj.getEventType() &&
                        getTradingSessionName().equals(castedObj.getTradingSessionName()) &&
                        getDateTime().equals(castedObj.getDateTime()) &&
                        getContextString().equals(castedObj.getContextString()) &&
                        getEventGroup().equals(castedObj.getEventGroup()) &&
                        getHistoryKey() == castedObj.getHistoryKey())
                {
                    isEqual = true;
                }
            }
        }
        return isEqual;
    }

    /**
     * @deprecated provided for testing capability
     */
    public TradingSessionEventDescriptionStruct getDescriptionStruct()
    {
        return descriptionStruct;
    }

    /**
     * @deprecated provided for testing capability
     */
    public TradingSessionEventHistoryStructV2 getEventStruct()
    {
        return eventStruct;
    }

    public RegisteredServerEvent[] getRegisteredServerEvents()
    {
        return serverEvents.getModelSequence();
    }

    public void setRegisteredServerEvents(RegisteredServerEvent[] serverEvents)
    {
        this.serverEvents.clear();
        for( int i = 0; i < serverEvents.length; i++ )
        {
            RegisteredServerEvent serverEvent = serverEvents[i];
            this.serverEvents.put(serverEvent);
        }
    }

    public String getContextString()
    {
        return contextString;
    }

    public void setContextString(String contextString)
    {
        this.contextString = contextString;
    }

    public String getEventGroup()
    {
        return eventGroup;
    }

    public void setEventGroup(String eventGroup)
    {
        this.eventGroup = eventGroup;
    }

    public int getHistoryKey()
    {
        return historyKey;
    }

    public void setHistoryKey(int historyKey)
    {
        this.historyKey = historyKey;
    }

    public void setEventType(short type)
    {
        eventType = type;
    }

    public short getEventType()
    {
        return eventType;
    }

    public void setEventName(String name)
    {
        eventName = name;
    }

    public String getEventName()
    {
        return eventName;
    }

    public void setEventState(short state)
    {
        eventState = state;
    }

    public short getEventState()
    {
        return eventState;
    }

    public void setTradingSessionName(String name)
    {
        tradingSessionName = name;
    }

    public String getTradingSessionName()
    {
        return tradingSessionName;
    }

    public void setDateTime(DateTime dateTime)
    {
        this.dateTime = dateTime;
    }

    public DateTime getDateTime()
    {
        return dateTime;
    }

    public Object getKey()
    {
        return this;
    }

    /**
     * Sets values of the eventType, eventName attributes to the corresponding struct values
     * @param struct to initialize with
     */
    private void setTradingSessionEventDescriptionStruct(TradingSessionEventDescriptionStruct struct)
    {
        if( struct != null )
        {
            setEventType(struct.type);
            setEventName(struct.eventName);
        }

        this.descriptionStruct = struct;
    }

    /**
     * Sets values of the <code>eventType, eventName, eventState, tradingSessionKey, dateTime <\code>
     * attributes to the corresponding struct values
     * @param struct to initialize with
     */
    private void setTradingSessionEventHistoryStruct(TradingSessionEventHistoryStructV2 struct)
    {
        if( struct != null )
        {
            setTradingSessionEventDescriptionStruct(struct.eventDescription);
            setEventState(struct.eventState);
            setTradingSessionName(struct.sessionName);
            setDateTime(new DateTimeImpl(struct.dateTime));
            setContextString(struct.contextString);
            setEventGroup(struct.eventGroup);
            setHistoryKey(struct.eventHistoryKey);

            TradingSessionServerEventStateStruct[] serverEvents = struct.serverEventState;
            RegisteredServerEvent[] events = new RegisteredServerEvent[serverEvents.length];
            for( int i = 0; i < serverEvents.length; i++ )
            {
                TradingSessionServerEventStateStruct serverEvent = serverEvents[i];
                events[i] = RegisteredServerEventFactory.createRegisteredServer(serverEvent, this);
            }
            setRegisteredServerEvents(events);
        }

        this.eventStruct = struct;
    }
}