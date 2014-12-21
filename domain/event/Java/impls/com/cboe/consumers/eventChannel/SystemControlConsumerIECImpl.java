package com.cboe.consumers.eventChannel;

import com.cboe.domain.util.ServerFailureEventHolder;
import com.cboe.domain.util.UserActivityTimeoutEventHolder;
import com.cboe.idl.util.SummaryStruct;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.events.SystemControlConsumer;
import com.cboe.util.ChannelKey;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.util.event.EventChannelAdapterFactory;

public class SystemControlConsumerIECImpl extends BObject implements SystemControlConsumer
{
    private EventChannelAdapter internalEventChannel;

    private static final Integer INT_0 = 0;

    public SystemControlConsumerIECImpl()
    {
        internalEventChannel = EventChannelAdapterFactory.find();
    }

    public void acceptServerAsMaster(int p1,short p2,short p3,byte p4,java.lang.String p5)
    {
        StringBuilder received = new StringBuilder(p5.length()+120);
        received.append("event received -> (ignored) acceptServerAsMaster : group=").append(p1)
                .append(", svrType=").append(p2)
                .append(", reason=").append(p3)
                .append(", side=").append(p4)
                .append(", text=\"").append(p5).append("\"");
        Log.information(this, received.toString());
    }

    public void acceptServerFailure(int[] groups, int groupKeyFailed, short serverType, String sessionName,
                                    int[] classKeys, short activityReason, String text)
    {
        StringBuilder received = new StringBuilder(text.length()+90);
        received.append("event received -> acceptServerFailure : serverType=").append(serverType)
                .append(" sessionName=").append(sessionName)
                .append(" Event text=").append(text);
        Log.information(this, received.toString());

        ChannelKey channelKey = new ChannelKey(ChannelKey.SERVER_FAILURE, Integer.valueOf(serverType));
        ServerFailureEventHolder serverFailureEvent = new ServerFailureEventHolder(groups, groupKeyFailed, serverType,
                                                                                   sessionName, classKeys,
                                                                                   activityReason, text);
        ChannelEvent event = internalEventChannel.getChannelEvent(this, channelKey, serverFailureEvent);
        internalEventChannel.dispatch(event);
    }

    public void acceptGroupCancelSummary(SummaryStruct cancelSummary)
    {
        String summary = cancelSummary.toString();
        StringBuilder received = new StringBuilder(summary.length()+70);
        received.append("Event received: acceptGroupCancelSummary, cancelSummary=").append(summary);
        Log.information(this, received.toString());
        received.setLength(0);
        received.append("Dispatching IEC acceptGroupCancelSummary event for cancelSummary=").append(cancelSummary);
        Log.information(this, received.toString());

        ChannelKey channelKey = new ChannelKey(ChannelKey.GROUP_CANCEL, INT_0);
        ChannelEvent event = internalEventChannel.getChannelEvent(this, channelKey, cancelSummary);
        internalEventChannel.dispatch(event);

    }

    public void acceptUserActivityTimeout(int[] groups, String userId, String sessionName, int[] classKeys,
                                          short activityReason, String text)
    {
        StringBuilder received = new StringBuilder(text.length()+110);
        received.append("event received -> acceptUserActivityTimeout : userId=").append(userId)
                .append(" sessionName=").append(sessionName)
                .append(" activityReason=").append(activityReason)
                .append(" Event text=").append(text);
        Log.information(this, received.toString());

        ChannelKey channelKey = new ChannelKey(ChannelKey.USER_ACTIVITY_TIMEOUT, INT_0);
        UserActivityTimeoutEventHolder eventHolder = new UserActivityTimeoutEventHolder(groups, userId, sessionName,
                                                                                        classKeys, activityReason,
                                                                                        text);
        ChannelEvent event = internalEventChannel.getChannelEvent(this, channelKey, eventHolder);
        internalEventChannel.dispatch(event);
    }
}
