package com.cboe.consumers.eventChannel;

import com.cboe.domain.util.CallbackDeregistrationInfoStruct;
import com.cboe.idl.cmiUtil.CallbackInformationStruct;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.domain.session.CallbackDeregistrationInfo;
import com.cboe.interfaces.events.RemoteCASCallbackRemovalConsumer;
import com.cboe.util.ChannelKey;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.EventChannelAdapter;
import com.cboe.util.event.EventChannelAdapterFactory;

public class RemoteCASCallbackRemovalConsumerIECImpl extends BObject implements RemoteCASCallbackRemovalConsumer
{
    private EventChannelAdapter internalEventChannel;

    public RemoteCASCallbackRemovalConsumerIECImpl()
    {
        internalEventChannel = EventChannelAdapterFactory.find();
    }
    
    public void acceptCallbackRemoval(
            String casOrigin,
            String userId,
            String userSessionIOR,
            String reason,
            int errorCode,
            CallbackInformationStruct callbackInfo)
    {
    	
        if (Log.isDebugOn()) {
			StringBuilder calling = new StringBuilder(casOrigin.length()
					+ userId.length() + reason.length() +callbackInfo.subscriptionInterface.length()+ 100);
			calling.append("calling acceptCallbackRemoval: casOrigin=").append(
					casOrigin).append(";userId=").append(userId).append(
					";reason=").append(reason).append(";errorcode=").append(
					errorCode).append(";interface:").append(
					callbackInfo.subscriptionInterface);
			Log.notification(this, calling.toString());
		}
        
        CallbackDeregistrationInfo deregistrationInfo =
            new CallbackDeregistrationInfoStruct(callbackInfo, reason, errorCode);
        
        ChannelKey channelKey = new ChannelKey(ChannelKey.MDCAS_CALLBACK_REMOVAL, userSessionIOR);
        ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, channelKey, deregistrationInfo);
        internalEventChannel.dispatch(event);
    }
}
