package com.cboe.internalPresentation.productStateEventHistory.cache;
// -----------------------------------------------------------------------------------
// Source file: PSEventHistoryCacheEvent
//
// PACKAGE: com.cboe.internalPresentation.productStateEventHistory
// 
// Created: Mar 13, 2006 1:15:27 PM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
import java.util.*;

public class PSEventHistoryCacheEvent extends EventObject
{
    public enum EventType { DATA_CHANGE }
    
    private EventType type; 

    public PSEventHistoryCacheEvent(Object source, EventType type)
    {
        super(source);
        this.type = type;
    }

    public EventType getType()
    {
        return type;
    }
}
