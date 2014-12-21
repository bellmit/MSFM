package com.cboe.application.failover;

import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.tradingClassStatus.HandleServerFailure;
import com.cboe.domain.util.ServerFailureEventHolder;
import com.cboe.domain.util.UserActivityTimeoutEventHolder;
import com.cboe.idl.cmiConstants.ActivityReasons;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.events.IECSystemControlConsumerHome;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.util.event.EventChannelListener;

public abstract class ServerFailureListenerBase extends BObject implements EventChannelListener
{
    private final Integer INT_0 = 0;

    protected void subscribeForServerFailureEvents(short serverType) throws Exception
    {
        // Add the serviceImpl to the internal event channel and apply a filter 
        // for server failure events
        ChannelKey channelKey = new ChannelKey(ChannelType.SERVER_FAILURE, Integer.valueOf(serverType));
        EventChannelAdapterFactory.find().addChannelListener(this, this, channelKey);
            
        IECSystemControlConsumerHome eventChannelHome = ServicesHelper.getSystemControlConsumerHome();

        eventChannelHome.addFilter(channelKey);
    }

    protected void subscribeForUserActivityTimeoutEvents() throws Exception
    {
        // Use this object as a listener for UserActivityTimeout events on the
        // Internal Event Channel.
        ChannelKey channelKey = new ChannelKey(ChannelType.USER_ACTIVITY_TIMEOUT, INT_0);
        EventChannelAdapterFactory.find().addChannelListener(this, this, channelKey);

        // Apply a filter to accept UserActivityTimeout events.
        IECSystemControlConsumerHome eventChannelHome = ServicesHelper.getSystemControlConsumerHome();
        eventChannelHome.addFilter(channelKey);
    }

    public void channelUpdate(ChannelEvent event)
    {
        ChannelKey channelKey = (ChannelKey) event.getChannel();
        switch(channelKey.channelType)
        {
            case ChannelType.SERVER_FAILURE:
                ServerFailureEventHolder holder = (ServerFailureEventHolder) event.getEventData();
                processServerFailure(holder);
                //New addition to support CMi V8 implementation. 
                if(holder.getActivityReason() == ActivityReasons.QUOTE_UPDATES_REQUESTED) {
                	if(Log.isDebugOn()) {
                		Log.debug("Server failure event received with QUOTE_UPDATE_REQUEST reason code");
                		//printServerFailureEventHolderObject(holder);
                	}
                	HandleServerFailure.getInstance().notifyUsers(holder.getGroupKeyFailed(), holder.getActivityReason());	
                }
                break;
            case ChannelType.USER_ACTIVITY_TIMEOUT:
                UserActivityTimeoutEventHolder uat = (UserActivityTimeoutEventHolder) event.getEventData();
                processUserActivityTimeout(uat);
                break;
            default:
                Log.alarm(this, "Wrong channel type: " + channelKey.channelType);
        }
    }

	protected abstract void processServerFailure(ServerFailureEventHolder holder);
    protected abstract void processUserActivityTimeout(UserActivityTimeoutEventHolder holder);
    
    
    private void printServerFailureEventHolderObject(ServerFailureEventHolder holder) {
    	StringBuilder sb = new StringBuilder();
    	sb.append("SessionName :<"+holder.getSessionName()+">");
    	sb.append("ServerType :<"+holder.getServerType()+">");
    	sb.append("FailedGroupKey :<"+holder.getGroupKeyFailed()+">");
    	sb.append("ActivityReason :<"+holder.getActivityReason()+">");
    	sb.append("Text :<"+holder.getText()+">");
    	sb.append("Groups :<");
    	for(int i=0;i<holder.getGroups().length;i++) {
    		sb.append(holder.getGroups()[i]+",");
    	}
    	sb.append(">");
    	sb.append("Classes :<");
    	for(int i=0;i<holder.getClassKeys().length;i++) {
    		sb.append(holder.getClassKeys()[i]+",");
    	}
    	sb.append(">");
    	Log.debug(sb.toString());
    	sb = null;
    }
}
