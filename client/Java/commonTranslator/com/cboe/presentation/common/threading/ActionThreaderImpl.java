//
// -----------------------------------------------------------------------------------
// Source file: ActionThreaderImpl.java
//
// PACKAGE: com.cboe.presentation.common.threading
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.threading;

import java.awt.event.ActionEvent;

import com.cboe.interfaces.presentation.common.threading.ActionThreader;
import com.cboe.interfaces.presentation.common.threading.ActionThreaderPool;
import com.cboe.interfaces.presentation.threading.APIWorker;

import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.util.event.EventChannelListener;

public class ActionThreaderImpl implements EventChannelListener, ActionThreader
{
    protected ActionThreaderPool pool;

    protected ActionThreaderImpl(ActionThreaderPool pool)
    {
        super();
        if(pool == null)
        {
            throw new IllegalArgumentException("ActionThreaderPool may not be null.");
        }
        this.pool = pool;

        EventChannelAdapterFactory.find().setDynamicChannels(true);
        EventChannelAdapterFactory.find().addChannelListener(this, this, this);
    }

    public void cleanup()
    {
        EventChannelAdapterFactory.find().removeChannelListener(this, this, this);
        pool = null;
    }

    public void launchAction(APIWorker worker)
    {
        ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, this, worker);
        EventChannelAdapterFactory.find().dispatch(event);
    }

    public void channelUpdate(ChannelEvent event)
    {
        APIWorker worker = (APIWorker)event.getEventData();
        ActionEvent actionEvent = new ActionEvent(this, 0, "Threaded Action");
        worker.actionPerformed(actionEvent);

        Object lock = worker.getWorker().getSharedLockReference();
        if(lock != null)
        {
            pool.decrementSharedReferenceCount(lock);
        }

        // notify the pool that this thread is now available to be reused
        pool.actionThreaderCompleted(this);
    }
}