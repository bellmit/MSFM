package com.cboe.consumers.internalPresentation;

import com.cboe.idl.util.SummaryStruct;
import com.cboe.interfaces.callback.SystemControlCallbackConsumer;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.internalPresentation.common.logging.GUILoggerSABusinessProperty;

/**
 * This is the implementation of the consumer for user events.
 */
public class SystemControlCallbackConsumerImpl implements SystemControlCallbackConsumer
{
    private EventChannelAdapter eventChannel = null;

    public SystemControlCallbackConsumerImpl(EventChannelAdapter eventChannel)
    {
        this.eventChannel = eventChannel;
    }

    public void acceptServerAsMaster(int p1,short p2,short p3,byte p4,java.lang.String p5)
    {
        //STUB CODE: No Implementation
    }
    public void acceptUserActivityTimeout(int[] groups, java.lang.String userId, java.lang.String sessionName,
                                          int[] classKeys, short reason, java.lang.String text)
    {
        //STUB CODE: No Implementation
    }

    public void acceptServerFailure(int[] groups, int groupKeyFailed, short serverType, java.lang.String sessionName,
                                    int[] classKeys, short reason, java.lang.String text)
    {
        //No Implementation
    }


    public void acceptGroupCancelSummary(SummaryStruct cancelSummary)
    {
        ChannelKey key = new ChannelKey(ChannelType.GROUP_CANCEL, new Integer(0));

        if(GUILoggerHome.find().isDebugOn() && GUILoggerHome.find().isPropertyOn(GUILoggerSABusinessProperty.ORDER_HANDLING))
        {
            GUILoggerHome.find().debug("SystemControlCallbackConsumerImpl.acceptGroupCancelSummary() received cancel summary",
                    GUILoggerSABusinessProperty.ORDER_HANDLING, cancelSummary);
        }
        ChannelEvent event = eventChannel.getChannelEvent(this, key, cancelSummary);
        eventChannel.dispatch(event);

    }


}
