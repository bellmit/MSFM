package com.cboe.consumers.eventChannel;

/**
 * @author Jeff Illian
 */
import com.cboe.domain.instrumentedChannel.event.InstrumentedEventChannelAdapterFactory;
import com.cboe.domain.instrumentedChannel.event.InstrumentedEventChannelAdapter;
import com.cboe.interfaces.events.*;
import com.cboe.idl.cmiSession.*;
import com.cboe.util.*;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;
import com.cboe.idl.session.TradingSessionElementStruct;
import com.cboe.idl.session.TradingSessionEventHistoryStruct;
import com.cboe.idl.session.TradingSessionElementStructV2;

public class TradingSessionConsumerIECImpl extends BObject implements TradingSessionConsumer {
    private InstrumentedEventChannelAdapter internalEventChannel = null;
    private static final Integer INT_0 = 0;
    /**
     * constructor comment.
     */
    public TradingSessionConsumerIECImpl() {
        super();
        internalEventChannel = InstrumentedEventChannelAdapterFactory.find();
    }

   public void acceptBusinessDayEvent(com.cboe.idl.session.BusinessDayStruct currentDay)
   {
	   	if (Log.isDebugOn())
		{
	   		Log.debug(this, "event received -> BusinessDay " + currentDay.currentDay);
		}
   		
        ChannelKey channelKey = new ChannelKey(ChannelKey.BUSINESS_DAY, INT_0);
        ChannelEvent event = internalEventChannel.getChannelEvent(this, channelKey, currentDay);
        internalEventChannel.dispatch(event);
   }

    public void acceptTradingSessionState(TradingSessionStateStruct sessionState) {
    	if (Log.isDebugOn())
    	{
    		Log.debug(this, "event received -> TradingSession" + sessionState.sessionName);
    	}

        ChannelKey channelKey = new ChannelKey(ChannelKey.TRADING_SESSION, INT_0);
        ChannelEvent event = internalEventChannel.getChannelEvent(this, channelKey, sessionState);
        internalEventChannel.dispatch(event);
    }

	public void acceptTradingSessionEventState(TradingSessionEventHistoryStruct eventState) {
    	if (Log.isDebugOn())
    	{
    		Log.debug(this, "event received -> TradingSession,EventType " + eventState.contextString + "," + eventState.eventDescription.eventName);
    	}
	}

    public void setProductStates(int classKey, String sessionName, ProductStateStruct[] productStates) {
        ChannelKey channelKey = null;
        ChannelEvent event = null;

    	if (Log.isDebugOn())
    	{
    		Log.debug(this, "event received -> ProductState for sessionName:" + sessionName + " classKey: " + classKey);
    	}
        channelKey = new ChannelKey(ChannelKey.SET_PRODUCT_STATE, sessionName);
        event = internalEventChannel.getChannelEvent(this, channelKey, productStates);
        internalEventChannel.dispatch(event);
    }

    public void setClassState(ClassStateStruct newState) {
    	if (Log.isDebugOn())
    	{
    		Log.debug(this, "event received -> ClassState : " + newState.classKey);
    	}

        ChannelKey channelKey = new ChannelKey(ChannelKey.SET_CLASS_STATE, newState.sessionName);
        ChannelEvent event = internalEventChannel.getChannelEvent(this, channelKey, newState);
        internalEventChannel.dispatch(event);
    }

    public void updateProduct(SessionProductStruct product) {
    	if (Log.isDebugOn())
    	{
    		Log.debug(this, "event received -> updateProduct : " + product.productStruct.productKeys.productKey + " : " + product.sessionName);
    	}

        ChannelKey channelKey = new ChannelKey(ChannelType.UPDATE_PRODUCT_BY_CLASS, product.sessionName);
        ChannelEvent event = internalEventChannel.getChannelEvent(this, channelKey, product);
        internalEventChannel.dispatch(event);
    }

    public void updateProductClass(SessionClassStruct productClass) {
    	if (Log.isDebugOn())
    	{
    		Log.debug(this, "event received -> updateProductClass : " + productClass.classStruct.classKey + " : " + productClass.sessionName);
    	}

        ChannelKey channelKey = new ChannelKey(ChannelType.UPDATE_PRODUCT_CLASS, productClass.sessionName);
        ChannelEvent event = internalEventChannel.getChannelEvent(this, channelKey, productClass);
        internalEventChannel.dispatch(event);
    }

    public void updateProductStrategy(SessionStrategyStruct updatedStrategy)
    {
//        Log.information(this, "event received -> updateProductStrategy : " +
//        		updatedStrategy.sessionProductStruct.productStruct.productKeys.classKey +
//        		":" + updatedStrategy.sessionProductStruct.productStruct.productKeys.productKey +
//        		" session : " + updatedStrategy.sessionProductStruct.sessionName);

        ChannelKey channelKey = new ChannelKey(ChannelType.STRATEGY_UPDATE, updatedStrategy.sessionProductStruct.sessionName);
        ChannelEvent event = internalEventChannel.getChannelEvent(this, channelKey, updatedStrategy);
        internalEventChannel.dispatch(event);
    }

	@Override
	public void acceptTradingSessionElementUpdate(
			TradingSessionElementStruct sessionElement) {
    	if (Log.isDebugOn())
    	{
    		Log.debug(this, "event received -> acceptTradingSessionElementUpdate : " + sessionElement.elementName + " : " + sessionElement.sessionName);
    	}
	}
    
	@Override
	public void acceptTradingSessionElementUpdateV2(TradingSessionElementStructV2 sessionElement) {
    	if (Log.isDebugOn())
    	{
    		Log.debug(this, "event received -> acceptTradingSessionElementUpdateV2 : " + sessionElement.tradingSessionElementStruct.elementName + " : " + sessionElement.tradingSessionElementStruct.sessionName);
    	}
   	} 
}
