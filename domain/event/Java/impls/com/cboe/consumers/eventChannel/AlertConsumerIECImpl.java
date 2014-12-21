package com.cboe.consumers.eventChannel;


import com.cboe.domain.instrumentedChannel.event.InstrumentedEventChannelAdapterFactory;
import com.cboe.domain.instrumentedChannel.event.InstrumentedEventChannelAdapter;
import com.cboe.domain.util.SessionKeyContainer;
import com.cboe.interfaces.events.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;


public class AlertConsumerIECImpl extends BObject implements AlertConsumer{
    private InstrumentedEventChannelAdapter internalEventChannel;
    private static final Integer INT_0 = 0;

    public AlertConsumerIECImpl() {
        super();
        internalEventChannel = InstrumentedEventChannelAdapterFactory.find();
    }

    public void acceptAlert(com.cboe.idl.cmiIntermarketMessages.AlertStruct alert)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "event received -> Alert : " + alert.nbboAgentId );
        }
        ChannelKey channelKey = new ChannelKey(ChannelType.ALERT, alert.nbboAgentId);
        ChannelEvent event = internalEventChannel.getChannelEvent(this, channelKey, alert);
        internalEventChannel.dispatch(event);

        channelKey = new ChannelKey(ChannelType.ALERT_ALL, INT_0);
        event = internalEventChannel.getChannelEvent(this, channelKey, alert);
        internalEventChannel.dispatch(event);
    }

    public void acceptAlertUpdate(com.cboe.idl.cmiIntermarketMessages.AlertStruct alertUpdated)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "event received -> AlertUpdate : " + alertUpdated.nbboAgentId );
        }
        ChannelKey channelKey = new ChannelKey(ChannelKey.ALERT_UPDATE, alertUpdated.nbboAgentId);
        ChannelEvent event = internalEventChannel.getChannelEvent(this, channelKey, alertUpdated);
        internalEventChannel.dispatch(event);

        channelKey = new ChannelKey(ChannelKey.ALERT_UPDATE_ALL, INT_0);
        event = internalEventChannel.getChannelEvent(this, channelKey, alertUpdated);
        internalEventChannel.dispatch(event);
    }

    public void acceptSatisfactionAlert(com.cboe.idl.cmiIntermarketMessages.SatisfactionAlertStruct alert)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "event received -> satisfactionAlert : PKey" + alert.lastSale.productKeys.classKey + " for " + alert.lastSale.sessionName);
        }
        SessionKeyContainer sessionKey = new SessionKeyContainer(alert.lastSale.sessionName, alert.lastSale.productKeys.classKey);
        ChannelKey channelKey = new ChannelKey(ChannelType.ALERT_SATISFACTION, sessionKey);
        ChannelEvent event = internalEventChannel.getChannelEvent(this, channelKey, alert);
        internalEventChannel.dispatch(event);

        channelKey = new ChannelKey(ChannelType.ALERT_SATISFACTION_ALL, INT_0);
        event = internalEventChannel.getChannelEvent(this, channelKey, alert);
        internalEventChannel.dispatch(event);
    }
}
