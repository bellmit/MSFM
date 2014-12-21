//
// -----------------------------------------------------------------------------------
// Source file: SummaryFilterMediatorImpl.java
//
// PACKAGE: com.cboe.presentation.collector
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.collector;

import java.util.*;
import java.util.concurrent.*;

import com.cboe.interfaces.instrumentation.Instrumentor;
import com.cboe.interfaces.instrumentation.collector.InstrumentorCollectorEvent;
import com.cboe.interfaces.instrumentation.collector.SummaryFilterMediator;

import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.EventChannelListener;

import com.cboe.presentation.api.InstrumentationTranslatorFactory;

public class SummaryFilterMediatorImpl implements SummaryFilterMediator
{
    private EventChannelListener proxyListener;

    private final Map<String, String> blockMap;

    private boolean subscribed;

    public SummaryFilterMediatorImpl(EventChannelListener proxyListener)
    {
        if(proxyListener == null)
        {
            throw new IllegalArgumentException("proxyListener may not be null.");
        }
        this.proxyListener = proxyListener;
        blockMap = new ConcurrentHashMap<String, String>(10);
        subscribed = false;
    }

    public void channelUpdate(ChannelEvent event)
    {
        if(blockMap.isEmpty())
        {
            proxyListener.channelUpdate(event);
        }
        else
        {
            boolean blockOutput = false;

            ChannelKey channelKey = (ChannelKey) event.getChannel();
            if(channelKey.channelType == ChannelType.INSTRUMENTOR_UPDATE)
            {
                Instrumentor eventData = (Instrumentor) event.getEventData();
                if(blockMap.containsKey(eventData.getOrbName()))
                {
                    blockOutput = true;
                }
            }
            else if(channelKey.channelType == ChannelType.INSTRUMENTOR_BLOCK_UPDATE)
            {
                InstrumentorCollectorEvent eventData =
                        (InstrumentorCollectorEvent) event.getEventData();
                if(blockMap.containsKey(eventData.getOrbName()))
                {
                    blockOutput = true;
                }
            }
            if(!blockOutput)
            {
                proxyListener.channelUpdate(event);
            }
        }
    }

    public void subscribeAll()
    {
        if(!subscribed)
        {
            InstrumentationTranslatorFactory.find().subscribeAllOrbsForSummary(this);
            subscribed = true;
        }
    }

    public void unsubscribeAll()
    {
        if(subscribed)
        {
            InstrumentationTranslatorFactory.find().unsubscribeAllOrbsForSummary(this);
            subscribed = false;
        }
    }

    public void blockPublishing(String orbName)
    {
        blockMap.put(orbName, orbName);
    }

    public void restorePublishing(String orbName)
    {
        blockMap.remove(orbName);
    }
}
