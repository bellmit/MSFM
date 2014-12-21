//
// -----------------------------------------------------------------------------------
// Source file: NotificationDispatcher.java
//
// PACKAGE: com.cboe.presentation.alarms.events.consumers
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.alarms.events.consumers;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.cboe.idl.alarm.AlarmNotificationStruct;
import com.cboe.interfaces.instrumentation.alarms.AlarmNotification;
import com.cboe.presentation.alarms.AlarmNotificationImpl;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.EventChannelAdapter;

class NotificationDispatcher extends TimerTask
{
    private BlockingQueue<AlarmNotification> alarmNotificationQueue = new LinkedBlockingQueue<AlarmNotification>();
    private EventChannelAdapter eventChannel;

    NotificationDispatcher(EventChannelAdapter eventChannel)
    {
        this.eventChannel = eventChannel;
    }

    public void run()
    {
        if(alarmNotificationQueue.isEmpty() == false)
        {
        	final int size = alarmNotificationQueue.size();
			List<AlarmNotification> list = new ArrayList<AlarmNotification>(size);
			alarmNotificationQueue.drainTo(list, size);
            AlarmNotification[] notifications = list.toArray(new AlarmNotification[size]);
            dispatchEvent(ChannelType.IC_ALARM_NOTIFICATION, notifications);
        }
    }

	void addAlarmNotificationStructs(AlarmNotificationStruct[] structs)
	{
		for (int i = 0; i < structs.length; i++)
		{
			alarmNotificationQueue.add(new AlarmNotificationImpl(structs[i], System.currentTimeMillis()));
		}
	}

    protected void dispatchEvent(int channelType, AlarmNotification[] notifications)
    {
        ChannelEvent channelEventAll =
                eventChannel.getChannelEvent(this, new ChannelKey(channelType, new Integer(0)), notifications);
        eventChannel.dispatch(channelEventAll);
    }
}
