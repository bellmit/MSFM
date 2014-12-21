package com.cboe.consumers.eventChannel;

import com.cboe.idl.util.SummaryStruct;
import com.cboe.interfaces.events.SystemControlConsumer;

public class SystemControlEventConsumerImpl extends com.cboe.idl.events.POA_SystemControlEventConsumer
        implements SystemControlConsumer
{
    private SystemControlConsumer delegate;

    public SystemControlEventConsumerImpl(SystemControlConsumer systemControlConsumer)
    {
        delegate = systemControlConsumer;
    }

    public void acceptServerFailure(int[] groups, int groupKeyFailed, short serverType, String sessionName,
                                    int[] classKeys, short activityReason, String text)
    {
        delegate.acceptServerFailure(groups, groupKeyFailed, serverType, sessionName, classKeys, activityReason, text);
    }

    public void acceptUserActivityTimeout(int[] groups, String userId, String sessionName, int[] classKeys,
                                          short activityReason, String text)
    {
        delegate.acceptUserActivityTimeout(groups, userId, sessionName, classKeys, activityReason, text);
    }

    public void acceptServerAsMaster(int grp, short type, short reason, byte side, String text)
    {
        delegate.acceptServerAsMaster(grp, type, reason, side, text);
    }

    public void acceptGroupCancelSummary(SummaryStruct cancelSummary)
    {
        delegate.acceptGroupCancelSummary(cancelSummary);
    }

    public org.omg.CORBA.Object get_typed_consumer()
    {
        return null;
    }

    public void push(org.omg.CORBA.Any data) throws org.omg.CosEventComm.Disconnected
    {
    }

    public void disconnect_push_consumer()
    {
    }
}
