//
// -----------------------------------------------------------------------------------
// Source file: RegisteredServerEventImpl.java
//
// PACKAGE: com.cboe.internalPresentation.tradingSession;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.tradingSession;

import com.cboe.idl.constants.TradingSessionEventStates;
import com.cboe.idl.session.TradingSessionServerEventStateStruct;

import com.cboe.interfaces.domain.dateTime.DateTime;
import com.cboe.interfaces.internalPresentation.tradingSession.RegisteredServerEvent;
import com.cboe.interfaces.internalPresentation.tradingSession.TradingSessionEvent;

import com.cboe.presentation.common.dateTime.DateTimeImpl;
import com.cboe.presentation.common.businessModels.AbstractBusinessModel;

import com.cboe.internalPresentation.common.comparators.RegisteredServerEventReverseDateComparator;

public class RegisteredServerEventImpl extends AbstractBusinessModel implements RegisteredServerEvent
{
    private String serverName;
    private String tradingSessionName;
    private String eventName;
    private short eventType;
    private String eventGroup;
    private short eventState = TradingSessionEventStates.NOT_AVAILABLE;
    private DateTime dateTime;
    private int transactionSequenceNumber;

    private RegisteredServerEventReverseDateComparator comparator;

    private TradingSessionServerEventStateStruct struct;
    private TradingSessionEvent tradingSessionEvent;

    protected RegisteredServerEventImpl()
    {
        super();
        comparator = new RegisteredServerEventReverseDateComparator();
    }

    public RegisteredServerEventImpl(TradingSessionServerEventStateStruct struct)
    {
        this();
        setTradingSessionServerEventStateStruct(struct);
    }

    public RegisteredServerEventImpl(TradingSessionServerEventStateStruct struct, TradingSessionEvent tradingSessionEvent)
    {
        this();
        this.tradingSessionEvent = tradingSessionEvent;
        setTradingSessionServerEventStateStruct(struct);
    }

    public Object getKey()
    {
        return this;
    }

//    public int hashCode()
//    {
//        return getTransactionSequenceNumber();
//    }

    /**
     * This method uses the RegisteredServerEventComparator
     */
    public int compareTo(Object o)
    {
        int result = comparator.compare(this, o);
        return result;
    }

    public boolean equals(Object o)
    {
        if (o == null)
        {
            return false;
        }

        boolean isEqual = super.equals(o);

        if(!isEqual)
        {
            if(o instanceof RegisteredServerEvent )
            {
                RegisteredServerEvent castedObj = ( RegisteredServerEvent ) o;
                if(getEventName().equals(castedObj.getEventName()) &&
                        getEventState() == castedObj.getEventState() &&
                        getEventType() == castedObj.getEventType() &&
                        getTradingSessionName().equals(castedObj.getTradingSessionName()) &&
                        getDateTime().equals(castedObj.getDateTime()) &&
                        getServerName().equals(castedObj.getServerName()) &&
                        getEventGroup().equals(castedObj.getEventGroup()) &&
                        getTransactionSequenceNumber() == castedObj.getTransactionSequenceNumber())
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
    public TradingSessionServerEventStateStruct getStruct()
    {
        return struct;
    }

    public String getServerName()
    {
        return serverName;
    }

    public void setServerName(String serverName)
    {
        this.serverName = serverName;
    }

    public int getTransactionSequenceNumber()
    {
        return transactionSequenceNumber;
    }

    public void setTransactionSequenceNumber(int transactionSequenceNumber)
    {
        this.transactionSequenceNumber = transactionSequenceNumber;
    }

    public String getEventGroup()
    {
        return eventGroup;
    }

    public void setEventGroup(String eventGroup)
    {
        this.eventGroup = eventGroup;
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

    public TradingSessionEvent getTradingSessionEvent()
    {
        return tradingSessionEvent;
    }

    private void setTradingSessionServerEventStateStruct(TradingSessionServerEventStateStruct struct)
    {
        if( struct != null )
        {
            setEventType(struct.currentEventDescription.type);
            setEventName(struct.currentEventDescription.eventName);
            setEventState(struct.eventState);
            setTradingSessionName(struct.sessionName);
            setDateTime(new DateTimeImpl(struct.dateTime));
            setServerName(struct.serverName);
            setEventGroup(struct.eventGroup);
            setTransactionSequenceNumber(struct.transactionSequenceNumber);
        }

        this.struct = struct;
    }
}