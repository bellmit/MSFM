//
// ------------------------------------------------------------------------
// FILE: AlarmNotificationCache.java
// 
// PACKAGE: com.cboe.presentation.api;
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2004 The Chicago Board Options Exchange.  All Rights Reserved.
// ------------------------------------------------------------------------
//
package com.cboe.presentation.api;

import java.util.*;
import java.util.prefs.*;

import com.cboe.idl.alarmConstants.Severities;

import com.cboe.interfaces.instrumentation.alarms.AlarmNotification;
import com.cboe.interfaces.instrumentation.alarms.AlarmNotificationInfo;
import com.cboe.interfaces.presentation.common.memory.MemoryUsage;

import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.util.event.EventChannelListener;

import com.cboe.presentation.alarms.AlarmNotificationInfoImpl;
import com.cboe.presentation.common.properties.InstrumentationProperties;
import com.cboe.presentation.common.properties.AppPropertiesFileFactory;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;
import com.cboe.presentation.common.memory.MemoryWatcher;
import com.cboe.presentation.common.preferences.InstrumentationBusinessPreferenceHelper;

public class AlarmNotificationCache implements EventChannelListener
{
    public static final int COLLECTION_INITIAL_SIZE = 10000;

    public static final int DEFAULT_CLEANUP_TIMER_DELAY = 1000;
    public static final String CLEANUP_TIMER_DELAY_KEY_NAME = "NotificationCacheCleanupDelayMillis";

    public static final ChannelKey EXPIRED_NOTIFICATION =
            new ChannelKey(ChannelType.IC_EXPIRED_ALARM_NOTIFICATION, new Integer(0));
    public static final ChannelKey NEW_NOTIFICATION =
            new ChannelKey(ChannelType.IC_NEW_ALARM_NOTIFICATION, new Integer(0));
    public static final ChannelKey VISUAL_EXPIRED_NOTIFICATION =
            new ChannelKey(ChannelType.IC_VISUAL_EXPIRED_ALARM_NOTIFICATION, new Integer(0));

    public static final ChannelKey EXPIRED_NOTIFICATIONS_MEMORY_THRESHOLD_EXCEEDED =
            new ChannelKey(ChannelType.IC_EXPIRED_NOTIFICATIONS_MEMORY_THRESHOLD_EXCEEDED, new Integer(0));

    protected EventChannelAdapter eventChannelAdapter;
    protected Timer taskTimer;
    protected CustomArrayList<AlarmNotificationInfo> backingStore;

    private static final Object BACKING_STORE_LOCK = new Object();

    public AlarmNotificationCache()
    {
        initialize();
    }

    public void channelUpdate(ChannelEvent event)
    {
        ChannelKey channelKey = (ChannelKey) event.getChannel();
        if(channelKey.channelType == ChannelType.IC_ALARM_NOTIFICATION)
        {
            AlarmNotification[] notifications = (AlarmNotification[]) event.getEventData();
            List<AlarmNotificationInfo> eventNotificationWrappersList = new ArrayList<AlarmNotificationInfo>(notifications.length);
            for(int i = 0; i < notifications.length; i++)
            {
                eventNotificationWrappersList.add(new AlarmNotificationInfoImpl(notifications[i], false));
            }

            synchronized(BACKING_STORE_LOCK)
            {
                backingStore.addAll(eventNotificationWrappersList);
            }

            for(Iterator<AlarmNotificationInfo> i = eventNotificationWrappersList.iterator(); i.hasNext();)
            {
                AlarmNotificationInfo notificationInfo = i.next();
                getEventChannelAdapter().dispatch(getEventChannelAdapter().getChannelEvent(this, NEW_NOTIFICATION,
                                                                                           notificationInfo));
                String[] subjectNames = notificationInfo.getSubjectNames();
                for(int j = 0; j < subjectNames.length; j++)
                {
                    String subjectName = subjectNames[j];
                    ChannelKey newChannelKeyForSubjectName =
                            new ChannelKey(ChannelType.IC_NEW_ALARM_NOTIFICATION, subjectName);
                    getEventChannelAdapter().dispatch(getEventChannelAdapter().getChannelEvent(this,
                                                                                               newChannelKeyForSubjectName,
                                                                                               notificationInfo));
                }
            }
        }
    }

    public AlarmNotification[] getAlarmNotifications()
    {
        AlarmNotificationInfo[] notificationsInfo = getAlarmNotificationsInfo();
        AlarmNotification[] notifications = new AlarmNotification[notificationsInfo.length];
        for(int i = 0; i < notificationsInfo.length; i++)
        {
            notifications[i] = notificationsInfo[i].getAlarmNotification();
        }
        return notifications;
    }

    public AlarmNotificationInfo[] getAlarmNotificationsInfo()
    {
        AlarmNotificationInfo[] notifications;
        synchronized(BACKING_STORE_LOCK)
        {
            notifications = new AlarmNotificationInfo[backingStore.size()];
            notifications = backingStore.toArray(notifications);
        }
        return notifications;
    }

    protected EventChannelAdapter getEventChannelAdapter()
    {
        if(eventChannelAdapter == null)
        {
            eventChannelAdapter = EventChannelAdapterFactory.find();
        }
        return eventChannelAdapter;
    }

    private void initialize()
    {
        backingStore = createNewList();
        getEventChannelAdapter().addChannelListener(getEventChannelAdapter(), this,
                                                    new ChannelKey(ChannelType.IC_ALARM_NOTIFICATION, new Integer(0)));

        taskTimer = new Timer(true);

        TimerTask expireCleanupTask = new ExpiredCleanupTask(backingStore, BACKING_STORE_LOCK,
                                                             getEventChannelAdapter());
        taskTimer.schedule(expireCleanupTask, getCleanupTaskDelay(), getCleanupTaskDelay());
        MemoryWatcher.getInstance(); // creates and initializes memory watcher.
    }

    private CustomArrayList<AlarmNotificationInfo> createNewList()
    {
        return new CustomArrayList<AlarmNotificationInfo>(COLLECTION_INITIAL_SIZE);
    }

    private int getCleanupTaskDelay()
    {
        int returnValue = DEFAULT_CLEANUP_TIMER_DELAY;

        if(AppPropertiesFileFactory.isAppPropertiesAvailable())
        {
            String property =
                    AppPropertiesFileFactory.find().getValue(InstrumentationProperties.ALARM_NOTIFICATION_SECTION_NAME,
                                                             CLEANUP_TIMER_DELAY_KEY_NAME);
            if(property != null && property.length() > 0)
            {
                try
                {
                    returnValue = Integer.parseInt(property);
                }
                catch(NumberFormatException e)
                {
                    GUILoggerHome.find().exception("Property: [" +
                                                   InstrumentationProperties.ALARM_NOTIFICATION_SECTION_NAME +
                                                   "]" + CLEANUP_TIMER_DELAY_KEY_NAME + " contained an invalid value:" +
                                                   property, e);
                }
            }
        }
        else
        {
            GUILoggerHome.find().alarm("Property: [" + InstrumentationProperties.ALARM_NOTIFICATION_SECTION_NAME +
                                       "]" + CLEANUP_TIMER_DELAY_KEY_NAME + " could not be obtained." +
                                       "Properties were not available.");
        }

        return returnValue;
    }
}

class ExpiredCleanupTask extends TimerTask implements EventChannelListener
{
    private Object backingStoreLock;
    private CustomArrayList<AlarmNotificationInfo> backingStore;
    private EventChannelAdapter eventChannelAdapter;
    private MemoryUsage memoryUsage;
    private final Object memoryUsageLock = new Object();
    private AlarmNotificationComparator alarmNotificationComparator;

    ExpiredCleanupTask(CustomArrayList<AlarmNotificationInfo> backingStore, Object backingStoreLock,
                       EventChannelAdapter eventChannelAdapter)
    {
        this.backingStore = backingStore;
        this.backingStoreLock = backingStoreLock;
        this.eventChannelAdapter = eventChannelAdapter;
        this.alarmNotificationComparator = new AlarmNotificationComparator();
        EventChannelAdapterFactory.find().addChannelListener(EventChannelAdapterFactory.find(), this,
                                                             new ChannelKey(ChannelType.MEMORY_USAGE, new Integer(0)));
    }

    public void channelUpdate(ChannelEvent event)
    {
        ChannelKey channelKey = (ChannelKey) event.getChannel();

        if (channelKey.channelType == ChannelType.MEMORY_USAGE)
        {
            MemoryUsage memoryUsageNotification = (MemoryUsage) event.getEventData();
            synchronized (memoryUsageLock)
            {
                if (memoryUsageNotification.getPercentUsage() > memoryUsageNotification.getUsageThreshold())
                {
                    // memory threshold exceeded.  set flag for cleanup
                    try
                    {
                        memoryUsage = (MemoryUsage)memoryUsageNotification.clone();
                    }
                    catch (CloneNotSupportedException e)
                    {
                        GUILoggerHome.find().exception(e);
                    }
                }
                else
                {
                    memoryUsage = null;
                }
            }
        }
    }

    public void run()
    {
        if(backingStore.size() > 0)
        {
            List<AlarmNotificationInfo> expiredItems = new ArrayList<AlarmNotificationInfo>(backingStore.size() / 2);

            List<AlarmNotificationInfo> clonedList;

            synchronized(backingStoreLock)
            {
                clonedList = new CustomArrayList<AlarmNotificationInfo>(backingStore);
            }

            if(!clonedList.isEmpty())
            {
                try
                {
                    int lowMillis = InstrumentationBusinessPreferenceHelper.getLowNotificationClearTimoutMillis();
                    int mediumMillis = InstrumentationBusinessPreferenceHelper.getMediumNotificationClearTimoutMillis();
                    int highMillis = InstrumentationBusinessPreferenceHelper.getHighNotificationClearTimoutMillis();
                    long currentMillis = System.currentTimeMillis();
                    long timeStampPlusClearTimout;

                    for(Iterator<AlarmNotificationInfo> iterator = clonedList.iterator(); iterator.hasNext();)
                    {
                        AlarmNotificationInfo alarmNotificationInfo = iterator.next();
                        if(!alarmNotificationInfo.isVisualExpirationNoticeSent())
                        {
                            timeStampPlusClearTimout =
                                alarmNotificationInfo.getAlarmNotification().getReceivedTime();
                            if(alarmNotificationInfo.getSeverity().shortValue() == Severities.LOW)
                            {
                                timeStampPlusClearTimout += lowMillis;
                            }
                            else if(alarmNotificationInfo.getSeverity().shortValue() == Severities.MEDIUM)
                            {
                                timeStampPlusClearTimout += mediumMillis;
                            }
                            else if(alarmNotificationInfo.getSeverity().shortValue() == Severities.HIGH)
                            {
                                timeStampPlusClearTimout += highMillis;
                            }

                            if(timeStampPlusClearTimout <= currentMillis)
                            {
                                expiredItems.add(alarmNotificationInfo);
                            }
                        }
                    }
                }
                catch(InvalidPreferencesFormatException e)
                {
                    DefaultExceptionHandlerHome.find().process(e, "Invalid Preferences Format for a Notification Clear " +
                                                                  "Timout Setting. Visual Notification events will not " +
                                                                  "be sent.");
                }
            }

            if(!expiredItems.isEmpty())
            {
                for(Iterator<AlarmNotificationInfo> iterator = expiredItems.iterator(); iterator.hasNext();)
                {
                    AlarmNotificationInfo visualExpiredNotificationInfo = iterator.next();
                    visualExpiredNotificationInfo.setVisualExpirationNoticeSent(true);
                    eventChannelAdapter.dispatch(eventChannelAdapter.getChannelEvent(this,
                                                                                     AlarmNotificationCache.VISUAL_EXPIRED_NOTIFICATION,
                                                                                     visualExpiredNotificationInfo));
                    String[] expiredSubjectNames = visualExpiredNotificationInfo.getSubjectNames();
                    for(int i = 0; i < expiredSubjectNames.length; i++)
                    {
                        String expiredSubjectName = expiredSubjectNames[i];
                        ChannelKey expiredChannelKeyForSubjectName =
                                new ChannelKey(ChannelType.IC_VISUAL_EXPIRED_ALARM_NOTIFICATION, expiredSubjectName);
                        eventChannelAdapter.dispatch(eventChannelAdapter.getChannelEvent(this,
                                                                                         expiredChannelKeyForSubjectName,
                                                                                         visualExpiredNotificationInfo));
                    }
                }
            }

            AlarmNotificationInfo[] expiredNotifications = null;
            int lastArrayIndexForEvent = 0;

            int backingStoreMaxSize = InstrumentationBusinessPreferenceHelper.getNotificationCacheExpireCount();

            synchronized (memoryUsageLock)
            {
                // if the Memory Usage Based Notification Expiration Percentage property is 0, then this
                // feature is disabled.
                if (memoryUsage != null && memoryUsage.getPercentUsage() > memoryUsage.getUsageThreshold() &&
                        InstrumentationProperties.getNotificationExpireMemoryUsagePercentage() > 0 )
                {
                    // only force the expiration if there are no items to expire.
                    if(backingStoreMaxSize > backingStore.size())
                    {

                        backingStoreMaxSize = backingStore.size() -
                                              (int)(backingStore.size() *
                                                    (InstrumentationProperties.getNotificationExpireMemoryUsagePercentage())/100.0);
                        eventChannelAdapter.dispatch(eventChannelAdapter.getChannelEvent(this,
                                                                                         AlarmNotificationCache.EXPIRED_NOTIFICATIONS_MEMORY_THRESHOLD_EXCEEDED,
                                                                                         new AlarmNotificationExpirationImpl(memoryUsage,backingStore.size(), (backingStore.size() - backingStoreMaxSize))));

                    }
                }
                memoryUsage = null;
            }

            if(backingStoreMaxSize != 0)
            {
                synchronized(backingStoreLock)
                {
                    if(backingStore.size() > backingStoreMaxSize)
                    {
                        Collections.sort(backingStore, alarmNotificationComparator);
                        lastArrayIndexForEvent = backingStore.size() - backingStoreMaxSize;
                        expiredNotifications = backingStore.toArray(new AlarmNotificationInfo[backingStore.size()]);
                        backingStore.removeRange(0, lastArrayIndexForEvent);
                    }
                }
            }

            if(expiredNotifications != null && expiredNotifications.length > 0)
            {
                for(int i = 0; i < lastArrayIndexForEvent; i++)
                {
                    // dispatch an expired notification
                    AlarmNotificationInfo expiredNotificationInfo = expiredNotifications[i];

                    // first check if we sent a visual expiration
                    if(!expiredNotificationInfo.isVisualExpirationNoticeSent())
                    {
                        expiredNotificationInfo.setVisualExpirationNoticeSent(true);
                        eventChannelAdapter.dispatch(eventChannelAdapter.getChannelEvent(this,
                                                                                         AlarmNotificationCache.VISUAL_EXPIRED_NOTIFICATION,
                                                                                         expiredNotificationInfo));
                        String[] expiredSubjectNames = expiredNotificationInfo.getSubjectNames();
                        for (int j = 0; j < expiredSubjectNames.length; j++)
                        {
                            String expiredSubjectName = expiredSubjectNames[j];
                            ChannelKey expiredChannelKeyForSubjectName =
                                    new ChannelKey(ChannelType.IC_VISUAL_EXPIRED_ALARM_NOTIFICATION, expiredSubjectName);
                            eventChannelAdapter.dispatch(eventChannelAdapter.getChannelEvent(this,
                                                                                             expiredChannelKeyForSubjectName,
                                                                                             expiredNotificationInfo));
                        }
                    }

                    eventChannelAdapter.dispatch(eventChannelAdapter.getChannelEvent(this,
                                                                                     AlarmNotificationCache.EXPIRED_NOTIFICATION,
                                                                                     expiredNotificationInfo));
                    String[] expiredSubjectNames = expiredNotificationInfo.getSubjectNames();
                    for(int j = 0; j < expiredSubjectNames.length; j++)
                    {
                        String expiredSubjectName = expiredSubjectNames[j];
                        ChannelKey expiredChannelKeyForSubjectName =
                                new ChannelKey(ChannelType.IC_EXPIRED_ALARM_NOTIFICATION, expiredSubjectName);
                        eventChannelAdapter.dispatch(eventChannelAdapter.getChannelEvent(this,
                                                                                         expiredChannelKeyForSubjectName,
                                                                                         expiredNotificationInfo));
                    }
                }
            }
        }
    }
}

class CustomArrayList<T> extends ArrayList<T>
{
    protected CustomArrayList(){}

    protected CustomArrayList(List<T> c)
    {
        super(c);
    }

    protected CustomArrayList(int initialCapacity)
    {
        super(initialCapacity);
    }

    public void removeRange(int fromIndex, int toIndex)
    {
        super.removeRange(fromIndex, toIndex);
    }
}

class AlarmNotificationComparator implements Comparator<AlarmNotificationInfo>
{
    int highEvictAgeSec, mediumEvictAgeSec = 0;

    public AlarmNotificationComparator()
    {
        super();
        try
        {
            mediumEvictAgeSec = InstrumentationBusinessPreferenceHelper.getNotificationCacheMediumEvictTime();
            highEvictAgeSec = InstrumentationBusinessPreferenceHelper.getNotificationCacheHighEvictTime();
        }
        catch(InvalidPreferencesFormatException e)
        {
            DefaultExceptionHandlerHome.find().process(e, "Invalid Preferences Format for a Cache Eviction Times. " +
                                                          "FIFO only processesing to occur. ");
        }
    }

    public int compare(AlarmNotificationInfo alarm1, AlarmNotificationInfo alarm2)
    {
        Long adjustedTime1 = alarm1.getAlarmNotification().getTimeStamp().getTimeInMillis();
        Long adjustedTime2 = alarm2.getAlarmNotification().getTimeStamp().getTimeInMillis();

        if(alarm1.getSeverity().shortValue() == Severities.HIGH)
        {
            adjustedTime1 += (highEvictAgeSec * 1000);
        }
        else if(alarm1.getSeverity().shortValue() == Severities.MEDIUM)
        {
            adjustedTime1 += (mediumEvictAgeSec * 1000);
        }
        if(alarm2.getSeverity().shortValue() == Severities.HIGH)
        {
            adjustedTime2 += (highEvictAgeSec * 1000);
        }
        else if(alarm2.getSeverity().shortValue() == Severities.MEDIUM)
        {
            adjustedTime2 += (mediumEvictAgeSec * 1000);
        }

        return adjustedTime1.compareTo(adjustedTime2);
    }
}

