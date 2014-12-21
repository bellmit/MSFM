package com.cboe.presentation.statusMonitor;

import com.cboe.util.event.EventChannelListener;
import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.interfaces.instrumentation.api.InstrumentationMonitorAPI;
import com.cboe.interfaces.instrumentation.InstrumentationStatus;
import com.cboe.presentation.api.InstrumentationTranslatorFactory;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerINBusinessProperty;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Monitors the number of heap instrumentors published for an interval.  If
 * the number of heap instrumentors over a period of time is zero, then an
 * event if fired.  If this happens for two consecutive periods, another event
 * is fired for that.  If a warning or error event is fired, then the next heap
 * received will fire and all good event.
 */
public class InstrumentorMonitor implements EventChannelListener
{
    protected static int DEFAULT_TIMER_INTERVAL = 11000;
    protected static Integer channelKeyZero = new Integer(0);

    protected int intervalEvents;
    protected int oldIntervalEvents;
    protected boolean failedLastTime;
    protected Timer   monitorTimer;
    protected EventChannelAdapter eventChannel;

    public InstrumentorMonitor()
    {
        initialize();
    }

    public void channelUpdate(ChannelEvent event)
    {
        ChannelKey channelKey = (ChannelKey)event.getChannel();

        if (channelKey.channelType == ChannelType.INSTRUMENTOR_BLOCK_UPDATE)
        {
            intervalEvents++;
        }
        else if (channelKey.channelType == ChannelType.INSTRUMENTOR_UPDATE)
        {
            intervalEvents++;
        }
    }

    public void stop()
    {
        monitorTimer.cancel();
    }

    protected void initialize()
    {
        intervalEvents = 0;
        failedLastTime = true;
        initListeners();
        eventChannel = EventChannelAdapterFactory.find();
        setupTimer();
    }

    protected void initListeners()
    {
        // Subscribe to the heap events
        InstrumentationMonitorAPI api = InstrumentationTranslatorFactory.find();
        // Subscribe for PW events
        api.subscribeAllOrbsForSummary(this);
    }

    /**
     * Setup a timer to pop every 11 seconds and check the count
     */
    protected void setupTimer()
    {
        monitorTimer = new Timer();
        TimerTask timerTask = new TimerTask()
        {
            public void run()
            {
                processTimerEvent();
            }
        };
        monitorTimer.scheduleAtFixedRate(timerTask,DEFAULT_TIMER_INTERVAL,DEFAULT_TIMER_INTERVAL);
    }

    /**
     * Process a timer pop
     */
    protected void processTimerEvent()
    {
        oldIntervalEvents = this.intervalEvents;
        this.intervalEvents = 0;
        if (oldIntervalEvents == 0)
        {
            if (failedLastTime)
            {
                fireErrorEvent();
            }
            else
            {
                fireWarningEvent();
            }
            failedLastTime = true;
        }
        else
        {
            //if (failedLastTime)
            {
                fireAllGoodEvent();
            }
            failedLastTime = false;
        }
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug("InstrumentorMonitor",GUILoggerINBusinessProperty.INSTRUMENTATION,
                    "Interval events = " + oldIntervalEvents);
        }
    }

    /**
     * Fire an all good event
     */
    protected void fireAllGoodEvent()
    {
        InstrumentationStatus status = new InstrumentationStatusImpl(InstrumentationStatus.STATUS_NORMAL,oldIntervalEvents);
        dispatchEvent(status);
    }

    /**
     * Fire a warning event.
     */
    protected void fireWarningEvent()
    {
        InstrumentationStatus status = new InstrumentationStatusImpl(InstrumentationStatus.STATUS_WARNING,oldIntervalEvents);
        dispatchEvent(status);
    }

    /**
     * Fire an error event
     */
    protected void fireErrorEvent()
    {
        InstrumentationStatus status = new InstrumentationStatusImpl(InstrumentationStatus.STATUS_ERROR,oldIntervalEvents);
        dispatchEvent(status);
    }

    protected void dispatchEvent(Object eventData)
    {
        ChannelKey channelKey = new ChannelKey(ChannelType.INSTRUMENTOR_INSTRUMENTATION_STATUS_UPDATE , channelKeyZero);
        ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, eventData);
        eventChannel.dispatch(event);
    }
}
