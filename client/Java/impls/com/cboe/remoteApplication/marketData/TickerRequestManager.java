package com.cboe.remoteApplication.marketData;

import com.cboe.domain.util.RemoteMarketDataSubscriptionInfoContainer;
import com.cboe.domain.util.SessionKeyContainer;
import com.cboe.idl.cmiCallbackV2.CMITickerConsumer;
import com.cboe.idl.consumers.TickerConsumer;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.events.IECRemoteCASTickerConsumerHome;
import com.cboe.remoteApplication.shared.RemoteServicesHelper;
import com.cboe.remoteApplication.shared.RemoteMarketDataSessionNameHelper;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.util.event.EventChannelListener;

/**
 * @author Jing Chen
 */
public class TickerRequestManager implements EventChannelListener {

    protected Integer groupKey;
    protected IECRemoteCASTickerConsumerHome tickerConsumerHome;

    public TickerRequestManager(Integer groupKey)
    {
        this.groupKey = groupKey;
        tickerConsumerHome = RemoteServicesHelper.getRemoteCASTickerConsumerHome();
        subscribeForEvents();
    }

    protected void subscribeForEvents()
    {
        String[] sessionNames = RemoteMarketDataSessionNameHelper.getLocalFilterOnlySessions();
        try
        {
            ChannelKey channelKey;
            
            for(int i = 0; i < sessionNames.length; i++)
            {
                SessionKeyContainer sessionGroupKey = new SessionKeyContainer(sessionNames[i], groupKey.intValue());
                channelKey = new ChannelKey(ChannelType.SUBSCRIBE_TICKER_BY_CLASS_V2, sessionGroupKey);
                EventChannelAdapterFactory.find().addChannelListener(this, this, channelKey);
                tickerConsumerHome.addFilter(channelKey);
                channelKey = new ChannelKey(ChannelType.SUBSCRIBE_TICKER_BY_PRODUCT_V2, sessionGroupKey);
                EventChannelAdapterFactory.find().addChannelListener(this, this, channelKey);
                tickerConsumerHome.addFilter(channelKey);
                channelKey = new ChannelKey(ChannelType.UNSUBSCRIBE_TICKER_BY_CLASS_V2, sessionGroupKey);
                EventChannelAdapterFactory.find().addChannelListener(this, this, channelKey);
                tickerConsumerHome.addFilter(channelKey);
                channelKey = new ChannelKey(ChannelType.UNSUBSCRIBE_TICKER_BY_PRODUCT_V2, sessionGroupKey);
                EventChannelAdapterFactory.find().addChannelListener(this, this, channelKey);
                tickerConsumerHome.addFilter(channelKey);
                channelKey = new ChannelKey(ChannelType.SUBSCRIBE_TICKER_BY_PRODUCT, sessionGroupKey);
                EventChannelAdapterFactory.find().addChannelListener(this, this, channelKey);
                tickerConsumerHome.addFilter(channelKey);
                channelKey = new ChannelKey(ChannelType.UNSUBSCRIBE_TICKER_BY_PRODUCT, sessionGroupKey);
                EventChannelAdapterFactory.find().addChannelListener(this, this, channelKey);
                tickerConsumerHome.addFilter(channelKey);
                channelKey = new ChannelKey(ChannelType.SUBSCRIBE_LARGE_TRADE_LAST_SALE_BY_CLASS, sessionGroupKey);
                EventChannelAdapterFactory.find().addChannelListener(this, this, channelKey);
                tickerConsumerHome.addFilter(channelKey);
                channelKey = new ChannelKey(ChannelType.UNSUBSCRIBE_LARGE_TRADE_LAST_SALE_BY_CLASS, sessionGroupKey);
                EventChannelAdapterFactory.find().addChannelListener(this, this, channelKey);
                tickerConsumerHome.addFilter(channelKey);
            }
        }
        catch (Exception e)
        {
            Log.exception(e);
        }
    }

    public void cleanUp()
    {
        String[] sessionNames = RemoteMarketDataSessionNameHelper.getLocalFilterOnlySessions();
        try
        {
            ChannelKey channelKey;
            
            for(int i = 0; i < sessionNames.length; i++)
            {
                SessionKeyContainer sessionGroupKey = new SessionKeyContainer(sessionNames[i], groupKey.intValue());
                channelKey = new ChannelKey(ChannelType.SUBSCRIBE_TICKER_BY_CLASS_V2, sessionGroupKey);
                EventChannelAdapterFactory.find().removeChannelListener(this, this, channelKey);
                tickerConsumerHome.removeFilter(channelKey);
                channelKey = new ChannelKey(ChannelType.SUBSCRIBE_TICKER_BY_PRODUCT_V2, sessionGroupKey);
                EventChannelAdapterFactory.find().removeChannelListener(this, this, channelKey);
                tickerConsumerHome.removeFilter(channelKey);
                channelKey = new ChannelKey(ChannelType.UNSUBSCRIBE_TICKER_BY_CLASS_V2, sessionGroupKey);
                EventChannelAdapterFactory.find().removeChannelListener(this, this, channelKey);
                tickerConsumerHome.removeFilter(channelKey);
                channelKey = new ChannelKey(ChannelType.UNSUBSCRIBE_TICKER_BY_PRODUCT_V2, sessionGroupKey);
                EventChannelAdapterFactory.find().removeChannelListener(this, this, channelKey);
                tickerConsumerHome.removeFilter(channelKey);
                channelKey = new ChannelKey(ChannelType.SUBSCRIBE_TICKER_BY_PRODUCT, sessionGroupKey);
                EventChannelAdapterFactory.find().removeChannelListener(this, this, channelKey);
                tickerConsumerHome.removeFilter(channelKey);
                channelKey = new ChannelKey(ChannelType.UNSUBSCRIBE_TICKER_BY_PRODUCT, sessionGroupKey);
                EventChannelAdapterFactory.find().removeChannelListener(this, this, channelKey);
                tickerConsumerHome.removeFilter(channelKey);
                channelKey = new ChannelKey(ChannelType.SUBSCRIBE_LARGE_TRADE_LAST_SALE_BY_CLASS, sessionGroupKey);
                EventChannelAdapterFactory.find().removeChannelListener(this, this, channelKey);
                tickerConsumerHome.removeFilter(channelKey);
                channelKey = new ChannelKey(ChannelType.UNSUBSCRIBE_LARGE_TRADE_LAST_SALE_BY_CLASS, sessionGroupKey);
                EventChannelAdapterFactory.find().removeChannelListener(this, this, channelKey);
                tickerConsumerHome.removeFilter(channelKey);
            }
        }
        catch (Exception e)
        {
            Log.exception(e);
        }
    }

    private void subscribeTickerByClassV2(String sessionName,
                                               int classKey,
                                               String casOrigin,
                                               String userId,
                                               String userIor,
                                               com.cboe.idl.cmiCallbackV2.CMITickerConsumer clientListener,
                                               short actionOnQueue)
    {
        try
        {
            RemoteServicesHelper.getRemoteCASSessionManagerHome().find(userIor,userId,casOrigin).
                getRemoteCASMarketDataService().subscribeTickerForClassV2(sessionName, classKey, clientListener, actionOnQueue);
        }
        catch(Exception e)
        {
            //TODO need   better exception handling
            Log.exception(e);
        }
    }
    
    

    protected void subscribeLargeTradeLastSaleByClass(String sessionName, 
    		int classKey, 
    		String casOrigin, 
    		String userId, 
    		String userSessionIOR, 
    		TickerConsumer consumer, 
    		short actionOnQueue) {
    	try
        {
            RemoteServicesHelper.getRemoteCASSessionManagerHome().find(userSessionIOR,userId,casOrigin).
            getRemoteCASMarketDataService().subscribeLargeTradeLastSaleForClass(sessionName, classKey, consumer, actionOnQueue);
        }
        catch(Exception e)
        {
            Log.exception("TickerRequestManager:" + this + " -->subscribeLargeTradeLastSaleByClass",e);
        }
 	
 	
    }

    private void subscribeTickerByProductV2(String sessionName,
                                               int classKey,
                                               int productKey,
                                               String casOrigin,
                                               String userId,
                                               String userIor,
                                               com.cboe.idl.cmiCallbackV2.CMITickerConsumer clientListener,
                                               short actionOnQueue)
    {
        try
        {
            RemoteServicesHelper.getRemoteCASSessionManagerHome().find(userIor,userId,casOrigin).
                getRemoteCASMarketDataService().subscribeTickerForProductV2(sessionName, classKey, productKey, clientListener, actionOnQueue);
        }
        catch(Exception e)
        {
            //TODO need   better exception handling
            Log.exception(e);
        }
    }

    private void unsubscribeTickerByClassV2(String sessionName,
                                               int classKey,
                                               String casOrigin,
                                               String userId,
                                               String userIor,
                                               com.cboe.idl.cmiCallbackV2.CMITickerConsumer clientListener)
    {
        try
        {
            RemoteServicesHelper.getRemoteCASSessionManagerHome().find(userIor,userId,casOrigin).
                getRemoteCASMarketDataService().unsubscribeTickerForClassV2(sessionName, classKey, clientListener);
        }
        catch(Exception e)
        {
            //TODO need   better exception handling
            Log.exception(e);
        }
    }
    
    protected void unsubscribeLargeTradeLastSaleByClass(String sessionName, 
    		int classKey, 
    		String casOrigin, 
    		String userId, 
    		String userSessionIOR, 
    		com.cboe.idl.consumers.TickerConsumer consumer) {
    	try
        {
            RemoteServicesHelper.getRemoteCASSessionManagerHome().find(userSessionIOR,userId,casOrigin).
                getRemoteCASMarketDataService().unsubscribeLargeTradeLastSaleForClass(sessionName, classKey, consumer);
        }
        catch(Exception e)
        {
            Log.exception("TickerRequestManager:" + this + " -->unsubscribeLargeTradeLastSaleByClass",e);
        }
    	
    }

    private void unsubscribeTickerByProductV2(String sessionName,
                                               int classKey,
                                               int productKey,
                                               String casOrigin,
                                               String userId,
                                               String userIor,
                                               com.cboe.idl.cmiCallbackV2.CMITickerConsumer clientListener)
    {
        try
        {
            RemoteServicesHelper.getRemoteCASSessionManagerHome().find(userIor,userId,casOrigin).
                getRemoteCASMarketDataService().unsubscribeTickerForProductV2(sessionName, classKey, productKey, clientListener);
        }
        catch(Exception e)
        {
            //TODO need   better exception handling
            Log.exception(e);
        }
    }

    private void subscribeTickerByProduct(String sessionName,
                                               int classKey,
                                               int productKey,
                                               String casOrigin,
                                               String userId,
                                               String userIor,
                                               com.cboe.idl.cmiCallback.CMITickerConsumer clientListener)
    {
        try
        {
            RemoteServicesHelper.getRemoteCASSessionManagerHome().find(userIor,userId,casOrigin).
                getRemoteCASMarketDataService().subscribeTickerForProduct(sessionName, classKey, productKey, clientListener);
        }
        catch(Exception e)
        {
            //TODO need   better exception handling
            Log.exception(e);
        }
    }

     private void unsubscribeTickerByProduct(String sessionName,
                                               int classKey,
                                               int productKey,
                                               String casOrigin,
                                               String userId,
                                               String userIor,
                                               com.cboe.idl.cmiCallback.CMITickerConsumer clientListener)
    {
        try
        {
            RemoteServicesHelper.getRemoteCASSessionManagerHome().find(userIor,userId,casOrigin).
                getRemoteCASMarketDataService().unsubscribeTickerForProduct(sessionName, classKey, productKey, clientListener);
        }
        catch(Exception e)
        {
            //TODO need   better exception handling
            Log.exception(e);
        }
    }

   public void channelUpdate(ChannelEvent event)
    {
        ChannelKey channelKey = (ChannelKey)event.getChannel();
        SessionKeyContainer sessionGroupKey = (SessionKeyContainer) channelKey.key;
        if (Log.isDebugOn())
        {
            Log.debug("TickerRequestManager:" + this + " -->  ChannelUpdate " + channelKey.channelType + ": sessionName=" + sessionGroupKey.getSessionName());
        }
        RemoteMarketDataSubscriptionInfoContainer subscriptionInfo = (RemoteMarketDataSubscriptionInfoContainer) event.getEventData();
        switch (channelKey.channelType)
        {
            case ChannelType.SUBSCRIBE_TICKER_BY_CLASS_V2:
                subscribeTickerByClassV2(subscriptionInfo.getSessionName(),
                            subscriptionInfo.getClassKey(),
                            subscriptionInfo.getCasOrigin(),
                            subscriptionInfo.getUserId(),
                            subscriptionInfo.getUserSessionIOR(),
                            (com.cboe.idl.cmiCallbackV2.CMITickerConsumer)subscriptionInfo.getCmiConsumer(),
                            subscriptionInfo.getActionOnQueue());
                break;
            case ChannelType.SUBSCRIBE_TICKER_BY_PRODUCT_V2:
                subscribeTickerByProductV2(subscriptionInfo.getSessionName(),
                        subscriptionInfo.getClassKey(),
                        subscriptionInfo.getProductKey(),
                        subscriptionInfo.getCasOrigin(),
                        subscriptionInfo.getUserId(),
                        subscriptionInfo.getUserSessionIOR(),
                        (com.cboe.idl.cmiCallbackV2.CMITickerConsumer)subscriptionInfo.getCmiConsumer(),
                        subscriptionInfo.getActionOnQueue());
                break;
            case ChannelType.UNSUBSCRIBE_TICKER_BY_CLASS_V2:
                unsubscribeTickerByClassV2(subscriptionInfo.getSessionName(),
                        subscriptionInfo.getClassKey(),
                        subscriptionInfo.getCasOrigin(),
                        subscriptionInfo.getUserId(),
                        subscriptionInfo.getUserSessionIOR(),
                        (com.cboe.idl.cmiCallbackV2.CMITickerConsumer)subscriptionInfo.getCmiConsumer());
                break;
            case ChannelType.UNSUBSCRIBE_TICKER_BY_PRODUCT_V2:
                unsubscribeTickerByProductV2(subscriptionInfo.getSessionName(),
                        subscriptionInfo.getClassKey(),
                        subscriptionInfo.getProductKey(),
                        subscriptionInfo.getCasOrigin(),
                        subscriptionInfo.getUserId(),
                        subscriptionInfo.getUserSessionIOR(),
                        (com.cboe.idl.cmiCallbackV2.CMITickerConsumer)subscriptionInfo.getCmiConsumer());
                break;
            case ChannelType.SUBSCRIBE_TICKER_BY_PRODUCT:
                subscribeTickerByProduct(subscriptionInfo.getSessionName(),
                        subscriptionInfo.getClassKey(),
                        subscriptionInfo.getProductKey(),
                        subscriptionInfo.getCasOrigin(),
                        subscriptionInfo.getUserId(),
                        subscriptionInfo.getUserSessionIOR(),
                        (com.cboe.idl.cmiCallback.CMITickerConsumer)subscriptionInfo.getCmiConsumer());
                break;
            case ChannelType.UNSUBSCRIBE_TICKER_BY_PRODUCT:
                unsubscribeTickerByProduct(subscriptionInfo.getSessionName(),
                        subscriptionInfo.getClassKey(),
                        subscriptionInfo.getProductKey(),
                        subscriptionInfo.getCasOrigin(),
                        subscriptionInfo.getUserId(),
                        subscriptionInfo.getUserSessionIOR(),
                        (com.cboe.idl.cmiCallback.CMITickerConsumer)subscriptionInfo.getCmiConsumer());
                break;
            case ChannelType.SUBSCRIBE_LARGE_TRADE_LAST_SALE_BY_CLASS:
            	subscribeLargeTradeLastSaleByClass(subscriptionInfo.getSessionName(),
                        subscriptionInfo.getClassKey(),
                        subscriptionInfo.getCasOrigin(),
                        subscriptionInfo.getUserId(),
                        subscriptionInfo.getUserSessionIOR(),
                        // internal consumer instead of CmiConsumer
                        (com.cboe.idl.consumers.TickerConsumer) subscriptionInfo.getCmiConsumer(),
                        subscriptionInfo.getActionOnQueue());
            	break;
            case ChannelType.UNSUBSCRIBE_LARGE_TRADE_LAST_SALE_BY_CLASS:
            	 unsubscribeLargeTradeLastSaleByClass(subscriptionInfo.getSessionName(),
                         subscriptionInfo.getClassKey(),
                         subscriptionInfo.getCasOrigin(),
                         subscriptionInfo.getUserId(),
                         subscriptionInfo.getUserSessionIOR(),
                         // internal consumer instead of CmiConsumer
                         (com.cboe.idl.consumers.TickerConsumer) subscriptionInfo.getCmiConsumer());
            	break;
            default :
                if (Log.isDebugOn())
                {
                    Log.debug("Wrong Channel : " + channelKey.channelType + " subscription: sessionName/" +
                            subscriptionInfo.getSessionName() + " classKey/" +
                            subscriptionInfo.getClassKey() + " productKey/" +
                            subscriptionInfo.getProductKey() + " CasOrigin/" +
                            subscriptionInfo.getCasOrigin() + " userId/" +
                            subscriptionInfo.getUserId());
                }
            
            break;
        }
    }


}
