//
// -----------------------------------------------------------------------------------
// Source file: AbstractCallbackConsumer.java
//
// PACKAGE: com.cboe.consumers.callback
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.consumers.callback;

import com.cboe.interfaces.consumers.callback.AcceptTimeDelay;
import com.cboe.interfaces.consumers.callback.QueueDepthLogging;

import com.cboe.util.event.EventChannelAdapter;
import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.util.ChannelKey;
import com.cboe.util.channel.ChannelEvent;

import com.cboe.presentation.common.properties.AppPropertiesFileFactory;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.presentation.common.formatters.QueueActions;

public abstract class AbstractCallbackConsumer implements AcceptTimeDelay, QueueDepthLogging
{
    public static final int LOG_COUNT = 100;
    private static final int defaultQueueThreshold = 1000;
    private int queueThreshold;

    protected int count;
    protected EventChannelAdapter eventChannel;

    private boolean isAcceptDelayEnabled;
    private int delayMillis;

    protected AbstractCallbackConsumer(EventChannelAdapter eventChannel)
    {
        super();
        this.eventChannel = eventChannel;

        isAcceptDelayEnabled = false;
        delayMillis = 0;
        count = 0;
        queueThreshold = defaultQueueThreshold;
        initialize();
    }

    protected abstract String getDelayPropertyName();

    protected abstract String getLogQueueDepthPropertyName();

    protected String getDelaySectionName()
    {
        return TIME_DELAY_PROPERTY_SECTION;
    }

    protected String getLogQueueDepthSectionName()
    {
        return LOGGING_PROPERTY_SECTION;
    }
    protected int getDelayMillis()
    {
        return delayMillis;
    }

    protected boolean isAcceptDelayEnabled()
    {
        return isAcceptDelayEnabled;
    }

    /**
     * Waits for a specific delay if the accept delays are enabled
     */
    protected void waitDelay()
    {
        if(isAcceptDelayEnabled())
        {
            try
            {
                Thread.sleep(getDelayMillis());
            }
            catch( InterruptedException e )
            {
                //do nothing, just return
            }
        }
    }

    protected void logQueueDepth(int queueDepth, short queueAction, String methodName, int structSize)
    {
        if (queueThreshold >= 0 && queueDepth >= queueThreshold && GUILoggerHome.find().isInformationOn())
        {
            StringBuffer msg = new StringBuffer(100);
            msg.append(this.getClass().getName()).append('.').append(methodName);
            msg.append(" QueueDepth=").append(queueDepth).append(" QueueAction=");
            msg.append(QueueActions.toString(queueAction));
            msg.append(" StructSize=").append(structSize);
            GUILoggerHome.find().information(msg.toString(), GUILoggerBusinessProperty.COMMON);
        }
    }
    /**
     * Logs a debug message if the method call has been made a predetermined number of times
     * @param key that identifies what the method call was for. toString() will be called on it.
     * @param methodName being called
     */
    protected void logMethodCall(Object key, String methodName)
    {
        if( isMethodCallLoggingOn() && this.count % LOG_COUNT == 0 )
        {
            GUILoggerHome.find().debug(this.getClass().getName() + "." + methodName + " Count for " + key + " ",
                                       GUILoggerBusinessProperty.COMMON, String.valueOf(this.count));
        }
    }

    protected boolean isMethodCallLoggingOn()
    {
        return GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerBusinessProperty.COMMON);
    }

    protected void increaseMethodCallCounter()
    {
        count++;
    }

    /**
     * Publishes an event on the event channel
     * @param source of event
     * @param channelType to publish event on
     * @param key to publish on channel with
     * @param data to publish with event
     */
    protected void dispatchEvent(Object source, int channelType, Object key, Object data)
    {
        ChannelKey channelKey = new ChannelKey(channelType, key);
        ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(source, channelKey, data);
        eventChannel.dispatch(event);
    }

    private void initialize()
    {
        if (AppPropertiesFileFactory.isAppPropertiesAvailable())
        {
            isAcceptDelayEnabled = ConsumerPropertyManager.getInstance().getBooleanValue(
                    getDelaySectionName(),
                    DELAY_ENABLED_PROPERTY_NAME,
                    DELAY_ENABLED_PROPERTY_NAME,
                    isAcceptDelayEnabled
                    );
            delayMillis = ConsumerPropertyManager.getInstance().getIntValue(
                    getDelaySectionName(),
                    DEFAULT_CONSUMER_DELAY_PROPERTY_NAME,
                    DEFAULT_CONSUMER_DELAY_PROPERTY_NAME,
                    delayMillis
            );
            // setup default value in delayMillis
            delayMillis = ConsumerPropertyManager.getInstance().getIntValue(
                    getDelaySectionName(),
                    getDelayPropertyName(),
                    DEFAULT_CONSUMER_DELAY_PROPERTY_NAME,
                    delayMillis
                    );
            // set the queue threshold to the default from the property file
            queueThreshold = ConsumerPropertyManager.getInstance().getIntValue(
                    getLogQueueDepthSectionName(),
                    DEFAULT_LOG_QUEUE_DEPTH_THRESHOLD,
                    DEFAULT_LOG_QUEUE_DEPTH_THRESHOLD,
                    defaultQueueThreshold);

            // override the queue threshold with its value in the property file
            queueThreshold = ConsumerPropertyManager.getInstance().getIntValue(
                    getLogQueueDepthSectionName(),
                    getLogQueueDepthPropertyName(),
                    DEFAULT_LOG_QUEUE_DEPTH_THRESHOLD,
                    queueThreshold);

        }
    }
}
