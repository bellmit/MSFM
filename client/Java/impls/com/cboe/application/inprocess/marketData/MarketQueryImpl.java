package com.cboe.application.inprocess.marketData;

import com.cboe.application.inprocess.consumer.proxy.*;
import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.shared.consumer.UserSessionLogoutProcessor;
import com.cboe.application.shared.consumer.UserSessionLogoutProcessorFactory;
import com.cboe.domain.iec.ClientIECFactory;
import com.cboe.domain.iec.ConcurrentEventChannelAdapter;
import com.cboe.domain.logout.LogoutServiceFactory;
import com.cboe.domain.util.SessionKeyContainer;
import com.cboe.domain.util.SessionKeyUserDataHelper;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiConstants.QueueActions;
import com.cboe.idl.cmiMarketData.BookDepthStruct;
import com.cboe.idl.cmiMarketData.CurrentMarketStruct;
import com.cboe.idl.cmiMarketData.NBBOStruct;
import com.cboe.idl.cmiMarketData.RecapStruct;
import com.cboe.idl.cmiSession.SessionClassStruct;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.UserMarketDataService;
import com.cboe.interfaces.application.UserSessionLogoutCollector;
import com.cboe.interfaces.application.inprocess.InProcessSessionManager;
import com.cboe.interfaces.application.inprocess.MarketQuery;
import com.cboe.interfaces.application.subscription.SubscriptionService;
import com.cboe.interfaces.application.inprocess.*;
//import com.cboe.interfaces.callback.*;
import com.cboe.interfaces.domain.instrumentedChannel.InstrumentedChannelListenerProxy;
import com.cboe.util.ChannelKey;
import com.cboe.util.UserDataTypes;
import com.cboe.util.channel.ChannelListenerProxy;
import com.cboe.util.event.EventChannelAdapterFactory;

/**
 * @author Jing Chen
 */
public class MarketQueryImpl extends BObject implements MarketQuery, UserSessionLogoutCollector
{
    protected InProcessSessionManager inProcessSessionManager;
    protected UserSessionLogoutProcessor logoutProcessor;
    protected UserMarketDataService userMarketQuery;
    protected SubscriptionService subscriptionService;
    protected String userId;
    private ConcurrentEventChannelAdapter internalEventChannel;

    public MarketQueryImpl()
    {
        super();
        try
        {
            internalEventChannel = ClientIECFactory.findConcurrentEventChannelAdapter(ClientIECFactory.MARKETDATA_INSTRUMENTED_IEC);
        }
        catch (Exception e)
        {
        	Log.exception(this, "Exception getting MARKETDATA_INSTRUMENTED_IEC!", e);
        }

    }

    public void setInProcessSessionManager(InProcessSessionManager session)
    {
        inProcessSessionManager = session;
        try
        {
            userId = inProcessSessionManager.getUserId();
        }
        catch(Exception e)
        {
            Log.exception(this, e);
        }
        logoutProcessor = UserSessionLogoutProcessorFactory.create(this);
        EventChannelAdapterFactory.find().addChannelListener(this, logoutProcessor, session);
        LogoutServiceFactory.find().addLogoutListener(session, this);
        userMarketQuery = ServicesHelper.getUserMarketDataService(session);
        subscriptionService = ServicesHelper.getSubscriptionService(session);
    }

    public BookDepthStruct getBookDepth(String sessionName, int productKey)
            throws SystemException, CommunicationException, AuthorizationException,
            DataValidationException, NotFoundException, NotAcceptedException
    {
        return userMarketQuery.getBookDepth(sessionName, productKey);
    }

    public CurrentMarketStruct getCurrentMarketForProduct(String sessionName, int productKey)
            throws SystemException, CommunicationException, AuthorizationException,
            DataValidationException, NotFoundException, NotAcceptedException
    {
        return userMarketQuery.getCurrentMarketForProduct(sessionName, productKey);
    }

    public RecapStruct getRecapForProduct(String sessionName, int productKey)
            throws SystemException, CommunicationException, AuthorizationException,
            DataValidationException, NotFoundException, NotAcceptedException
    {
        return userMarketQuery.getRecapForProduct(sessionName, productKey);
    }


    public void subscribeBookDepthForClass(SessionClassStruct sessionClass, OrderBookV2Consumer consumer)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeBookDepthForClass for " + inProcessSessionManager);
        }
        userMarketQuery.verifyUserBookDepthEnablement(sessionClass.sessionName, sessionClass.classStruct.classKey);
        if(consumer != null)
        {
            SessionKeyContainer sessionKey =
                    new SessionKeyContainer(sessionClass.sessionName, sessionClass.classStruct.classKey);
            BookDepthConsumerProxy listener =
                    BookDepthConsumerProxyFactory.find(inProcessSessionManager, sessionKey, consumer);
            ChannelKey channelKey = new ChannelKey(ChannelKey.BOOK_DEPTH_BY_CLASS, sessionKey);
            ChannelListenerProxy channelListenerProxy =
                    internalEventChannel.addChannelListener(this, listener, channelKey);
            subscriptionService.addBookDepthClassInterest
                    (listener, sessionClass.sessionName, sessionClass.classStruct.classKey);
            String sessionClassKeyString = SessionKeyUserDataHelper.encode(sessionKey);
            try
            {
                InstrumentedChannelListenerProxy instrumentedCLProxy =
                        (InstrumentedChannelListenerProxy) channelListenerProxy;
                instrumentedCLProxy.addUserData(UserDataTypes.SESSION_CLASS, sessionClassKeyString);
            }
            catch(ClassCastException e)
            {
                Log.exception(this, "ClassCastException during addition of user data.", e);
            }
            String smgr = inProcessSessionManager.toString();
            String listenerName = listener.toString();
            StringBuilder suboid = new StringBuilder(smgr.length()+listenerName.length()+sessionClass.sessionName.length()+50);
            suboid.append("Sub:oid for ").append(smgr)
                  .append(' ').append(listenerName)
                  .append(" sessionName:").append(sessionClass.sessionName)
                  .append(" classKey:").append(sessionClass.classStruct.classKey);
            Log.information(this, suboid.toString());

            synchronized(consumer)
            {
                BookDepthStruct[] bookDepthStructs =
                        userMarketQuery.getBookDepthsForClass
                        (sessionClass.sessionName, sessionClass.classStruct.classKey);
                consumer.acceptBookDepth(bookDepthStructs, 0, QueueActions.NO_ACTION);
            }
        }
        else
        {
            Log.alarm(this, "null orderBookConsumer found in subscribeBookDepthForClass for session:"
                    + inProcessSessionManager);
        }
    }

    public void unsubscribeBookDepthForClass(SessionClassStruct sessionClass, OrderBookV2Consumer consumer)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeBookDepthForClass for " + inProcessSessionManager);
        }
        userMarketQuery.verifyUserBookDepthEnablement(sessionClass.sessionName, sessionClass.classStruct.classKey);
        if(consumer != null)
        {
            SessionKeyContainer sessionKey =
                    new SessionKeyContainer(sessionClass.sessionName, sessionClass.classStruct.classKey);
            BookDepthConsumerProxy listener =
                    BookDepthConsumerProxyFactory.find(inProcessSessionManager, sessionKey, consumer);
            subscriptionService.removeBookDepthClassInterest
                    (listener, sessionClass.sessionName, sessionClass.classStruct.classKey);
            ChannelKey channelKey = new ChannelKey(ChannelKey.BOOK_DEPTH_BY_CLASS, sessionKey);
            ChannelListenerProxy channelListenerProxy =
                    internalEventChannel.removeChannelListener(this, listener, channelKey);
            try
            {
                String sessionClassKeyString = SessionKeyUserDataHelper.encode(sessionKey);
                InstrumentedChannelListenerProxy instrumentedCLProxyListener =
                                            (InstrumentedChannelListenerProxy) channelListenerProxy;
                instrumentedCLProxyListener.removeUserData(UserDataTypes.SESSION_CLASS, sessionClassKeyString);
            }
            catch(ClassCastException e)
            {
                Log.exception(this, "ClassCastException when removing sessionKey=" + sessionKey + " from user data", e);
            }
            String smgr = inProcessSessionManager.toString();
            String listenerName = listener.toString();
            StringBuilder unsuboid = new StringBuilder(smgr.length()+listenerName.length()+sessionClass.sessionName.length()+50);
            unsuboid.append("UnSub:oid for ").append(smgr).append(' ').append(listenerName)
                    .append(" sessionName:").append(sessionClass.sessionName)
                    .append(" classKey:").append(sessionClass.classStruct.classKey);
            Log.information(this, unsuboid.toString());
        }
        else
        {
            Log.alarm(this, "null orderBookConsumer found in unsubscribeBookDepthForClass for session:"
                    + inProcessSessionManager);
        }
    }

    public void subscribeCurrentMarketForClass(SessionClassStruct sessionClass, CurrentMarketV2Consumer consumer)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeCurrentMarketForClass for " + inProcessSessionManager);
        }
        userMarketQuery.verifyUserCurrentMarketEnablement(sessionClass.sessionName, sessionClass.classStruct.classKey);
        if(consumer != null)
        {
            SessionKeyContainer sessionKey =
                    new SessionKeyContainer(sessionClass.sessionName, sessionClass.classStruct.classKey);
            CurrentMarketConsumerProxy listener =
                    CurrentMarketConsumerProxyFactory.find(inProcessSessionManager, sessionKey, consumer);
            ChannelKey channelKey = new ChannelKey(ChannelKey.CURRENT_MARKET_BY_CLASS, sessionKey);
            ChannelListenerProxy channelListenerProxy =
                    internalEventChannel.addChannelListener(this, listener, channelKey);
            subscriptionService.addCurrentMarketClassInterest
                    (listener, sessionClass.sessionName, sessionClass.classStruct.classKey);
            String sessionClassKeyString = SessionKeyUserDataHelper.encode(sessionKey);
            try
            {
                InstrumentedChannelListenerProxy instrumentedCLProxy =
                        (InstrumentedChannelListenerProxy) channelListenerProxy;
                instrumentedCLProxy.addUserData(UserDataTypes.SESSION_CLASS, sessionClassKeyString);
            }
            catch(ClassCastException e)
            {
                Log.exception(this, "ClassCastException during addition of user data.", e);
            }
            String smgr = inProcessSessionManager.toString();
            String listenerName = listener.toString();
            StringBuilder suboid = new StringBuilder(smgr.length()+listenerName.length()+sessionClass.sessionName.length()+50);
            suboid.append("Sub:oid for ").append(smgr).append(' ').append(listenerName)
                  .append(" sessionName:").append(sessionClass.sessionName)
                  .append(" classKey:").append(sessionClass.classStruct.classKey);
            Log.information(this, suboid.toString());
            synchronized(consumer)
            {
                CurrentMarketStruct[] currentMarketStructs =
                        userMarketQuery.getCurrentMarketsForClass
                        (sessionClass.sessionName, sessionClass.classStruct.classKey);
                consumer.acceptCurrentMarket(currentMarketStructs, 0, QueueActions.NO_ACTION);
            }
        }
        else
        {
            Log.alarm(this, "null currentMarketConsumer found in subscribeCurrentMarketForClass for session:"
                    + inProcessSessionManager);
        }
    }

    public void unsubscribeCurrentMarketForClass(SessionClassStruct sessionClass, CurrentMarketV2Consumer consumer)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeCurrentMarketForClass for " + inProcessSessionManager);
        }
        userMarketQuery.verifyUserCurrentMarketEnablement(sessionClass.sessionName, sessionClass.classStruct.classKey);
        if(consumer != null)
        {
            SessionKeyContainer sessionKey =
                    new SessionKeyContainer(sessionClass.sessionName, sessionClass.classStruct.classKey);
            CurrentMarketConsumerProxy listener =
                    CurrentMarketConsumerProxyFactory.find(inProcessSessionManager, sessionKey, consumer);
            subscriptionService.removeCurrentMarketClassInterest
                    (listener, sessionClass.sessionName, sessionClass.classStruct.classKey);
            ChannelKey channelKey = new ChannelKey(ChannelKey.CURRENT_MARKET_BY_CLASS, sessionKey);
            ChannelListenerProxy channelListenerProxy = internalEventChannel.
                    removeChannelListener(this, listener, channelKey);
            try
            {
                String sessionClassKeyString = SessionKeyUserDataHelper.encode(sessionKey);
                InstrumentedChannelListenerProxy instrumentedCLProxyListener =
                        (InstrumentedChannelListenerProxy) channelListenerProxy;
                instrumentedCLProxyListener.removeUserData(UserDataTypes.SESSION_CLASS, sessionClassKeyString);
            }
            catch(ClassCastException e)
            {
                Log.exception(this, "ClassCastException when removing sessionKey=" + sessionKey + " from user data", e);
            }
            String smgr = inProcessSessionManager.toString();
            String listenerName = listener.toString();
            StringBuilder unsuboid = new StringBuilder(smgr.length()+listenerName.length()+sessionClass.sessionName.length()+50);
            unsuboid.append("UnSub:oid for ").append(smgr).append(' ').append(listenerName)
                    .append(" sessionName:").append(sessionClass.sessionName)
                    .append(" classKey:").append(sessionClass.classStruct.classKey);
            Log.information(this, unsuboid.toString());
        }
        else
        {
            Log.alarm(this, "null currentMarketConsumer found in unsubscribeCurrentMarketForClass for session:"
                    + inProcessSessionManager);
        }
    }

    public void subscribeExpectedOpeningPriceForClass(SessionClassStruct sessionClass,
                                                      ExpectedOpeningPriceV2Consumer consumer)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeExpectedOpeningPriceForClass for " + inProcessSessionManager);
        }
        userMarketQuery.verifyUserExpectdOpeningPriceEnablement
                (sessionClass.sessionName, sessionClass.classStruct.classKey);
        if(consumer != null)
        {
            SessionKeyContainer sessionKey =
                    new SessionKeyContainer(sessionClass.sessionName, sessionClass.classStruct.classKey);
            ExpectedOpeningPriceConsumerProxy listener =
                    ExpectedOpeningPriceConsumerProxyFactory.find(inProcessSessionManager, sessionKey, consumer);
            ChannelKey channelKey = new ChannelKey(ChannelKey.OPENING_PRICE_BY_CLASS, sessionKey);
            ChannelListenerProxy channelListenerProxy = internalEventChannel.
                    addChannelListener(this, listener, channelKey);
            subscriptionService.addOpeningPriceClassInterest
                    (listener, sessionClass.sessionName, sessionClass.classStruct.classKey);
            String sessionClassKeyString = SessionKeyUserDataHelper.encode(sessionKey);
            try
            {
                InstrumentedChannelListenerProxy instrumentedCLProxy =
                        (InstrumentedChannelListenerProxy) channelListenerProxy;
                instrumentedCLProxy.addUserData(UserDataTypes.SESSION_CLASS, sessionClassKeyString);
            }
            catch(ClassCastException e)
            {
                Log.exception(this, "ClassCastException during addition of user data.", e);
            }
            String smgr = inProcessSessionManager.toString();
            String listenerName = listener.toString();
            StringBuilder suboid = new StringBuilder(smgr.length()+listenerName.length()+sessionClass.sessionName.length()+50);
            suboid.append("Sub:oid for ").append(smgr).append(' ').append(listenerName)
                  .append(" sessionName:").append(sessionClass.sessionName)
                  .append(" classKey:").append(sessionClass.classStruct.classKey);
            Log.information(this, suboid.toString());
        }
        else
        {
            Log.alarm(this, "null eopConsumer found in subscribeExpectedOpeningPriceForClass for session:"
                    + inProcessSessionManager);
        }
    }

    public void unsubscribeExpectedOpeningPriceForClass(SessionClassStruct sessionClass,
                                                        ExpectedOpeningPriceV2Consumer consumer)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeExpectedOpeningPriceForClass for " + inProcessSessionManager);
        }
        userMarketQuery.verifyUserExpectdOpeningPriceEnablement
                (sessionClass.sessionName, sessionClass.classStruct.classKey);
        if(consumer != null)
        {
            SessionKeyContainer sessionKey =
                    new SessionKeyContainer(sessionClass.sessionName, sessionClass.classStruct.classKey);
            ExpectedOpeningPriceConsumerProxy listener =
                    ExpectedOpeningPriceConsumerProxyFactory.find(inProcessSessionManager, sessionKey, consumer);
            subscriptionService.removeOpeningPriceClassInterest
                    (listener, sessionClass.sessionName, sessionClass.classStruct.classKey);
            ChannelKey channelKey = new ChannelKey(ChannelKey.OPENING_PRICE_BY_CLASS, sessionKey);
            ChannelListenerProxy channelListenerProxy = internalEventChannel.
                    removeChannelListener(this, listener, channelKey);
            try
            {
                String sessionClassKeyString = SessionKeyUserDataHelper.encode(sessionKey);
                InstrumentedChannelListenerProxy instrumentedCLProxyListener =
                        (InstrumentedChannelListenerProxy) channelListenerProxy;
                instrumentedCLProxyListener.removeUserData(UserDataTypes.SESSION_CLASS, sessionClassKeyString);
            }
            catch(ClassCastException e)
            {
                Log.exception(this, "ClassCastException when removing sessionKey=" + sessionKey + " from user data", e);
            }
            String smgr = inProcessSessionManager.toString();
            String listenerName = listener.toString();
            StringBuilder unsuboid = new StringBuilder(smgr.length()+listenerName.length()+sessionClass.sessionName.length()+50);
            unsuboid.append("UnSub:oid for ").append(smgr).append(' ').append(listenerName)
                    .append(" sessionName:").append(sessionClass.sessionName)
                    .append(" classKey:").append(sessionClass.classStruct.classKey);
            Log.information(this, unsuboid.toString());
        }
        else
        {
            Log.alarm(this, "null eopConsumer found in unsubscribeExpectedOpeningPriceForClass for session:"
                    + inProcessSessionManager);
        }
    }

    public void subscribeNBBOForClass(SessionClassStruct sessionClass, NBBOV2Consumer consumer)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeNBBOForClass for " + inProcessSessionManager);
        }
        userMarketQuery.verifyUserNBBOEnablement(sessionClass.sessionName, sessionClass.classStruct.classKey);
        if(consumer != null)
        {
            SessionKeyContainer sessionKey =
                    new SessionKeyContainer(sessionClass.sessionName, sessionClass.classStruct.classKey);
            NBBOConsumerProxy listener = NBBOConsumerProxyFactory.find(inProcessSessionManager, sessionKey, consumer);
            ChannelKey channelKey = new ChannelKey(ChannelKey.NBBO_BY_CLASS, sessionKey);
            ChannelListenerProxy channelListenerProxy =
                    internalEventChannel.addChannelListener(this, listener, channelKey);
            subscriptionService.addNBBOClassInterest
                    (listener, sessionClass.sessionName, sessionClass.classStruct.classKey);
            String sessionClassKeyString = SessionKeyUserDataHelper.encode(sessionKey);
            try
            {
                InstrumentedChannelListenerProxy instrumentedCLProxy =
                        (InstrumentedChannelListenerProxy) channelListenerProxy;
                instrumentedCLProxy.addUserData(UserDataTypes.SESSION_CLASS, sessionClassKeyString);
            }
            catch(ClassCastException e)
            {
                Log.exception(this, "ClassCastException during addition of user data.", e);
            }
            synchronized(consumer)
            {
                NBBOStruct[] nbbos =
                        userMarketQuery.getNBBOsForClass(sessionClass.sessionName, sessionClass.classStruct.classKey);
                consumer.acceptNBBO(nbbos, 0, QueueActions.NO_ACTION);
            }
            String smgr = inProcessSessionManager.toString();
            String listenerName = listener.toString();
            StringBuilder suboid = new StringBuilder(smgr.length()+listenerName.length()+sessionClass.sessionName.length()+50);
            suboid.append("Sub:oid for ").append(smgr).append(' ').append(listenerName)
                  .append(" sessionName:").append(sessionClass.sessionName)
                  .append(" classKey:").append(sessionClass.classStruct.classKey);
            Log.information(this, suboid.toString());
        }
        else
        {
            Log.alarm(this, "null nbboConsumer found in subscribeNBBOForClass for session:"+inProcessSessionManager);
        }
    }

    public void unsubscribeNBBOForClass(SessionClassStruct sessionClass, NBBOV2Consumer consumer)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeNBBOForClass for " + inProcessSessionManager);
        }
        userMarketQuery.verifyUserNBBOEnablement(sessionClass.sessionName, sessionClass.classStruct.classKey);
        if(consumer != null)
        {
            SessionKeyContainer sessionKey =
                    new SessionKeyContainer(sessionClass.sessionName, sessionClass.classStruct.classKey);
            NBBOConsumerProxy listener = NBBOConsumerProxyFactory.find(inProcessSessionManager, sessionKey, consumer);
            subscriptionService.removeNBBOClassInterest
                    (listener, sessionClass.sessionName, sessionClass.classStruct.classKey);
            ChannelKey channelKey = new ChannelKey(ChannelKey.NBBO_BY_CLASS, sessionKey);
            ChannelListenerProxy channelListenerProxy = internalEventChannel.
                    removeChannelListener(this, listener, channelKey);
            try
            {
                String sessionClassKeyString = SessionKeyUserDataHelper.encode(sessionKey);
                InstrumentedChannelListenerProxy instrumentedCLProxyListener =
                        (InstrumentedChannelListenerProxy) channelListenerProxy;
                instrumentedCLProxyListener.removeUserData(UserDataTypes.SESSION_CLASS, sessionClassKeyString);
            }
            catch(ClassCastException e)
            {
                Log.exception(this, "ClassCastException when removing sessionKey=" + sessionKey + " from user data", e);
            }
            String smgr = inProcessSessionManager.toString();
            String listenerName = listener.toString();
            StringBuilder unsuboid = new StringBuilder(smgr.length()+listenerName.length()+sessionClass.sessionName.length()+50);
            unsuboid.append("UnSub:oid for ").append(smgr).append(' ').append(listenerName)
                    .append(" sessionName:").append(sessionClass.sessionName)
                    .append(" classKey:").append(sessionClass.classStruct.classKey);
            Log.information(this, unsuboid.toString());
        }
        else
        {
            Log.alarm(this, "null nbboConsumer found in unsubscribeNBBOForClass for session:"+inProcessSessionManager);
        }
    }

    public void subscribeRecapForClass(SessionClassStruct sessionClass, RecapV2Consumer consumer)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeRecapForClass for " + inProcessSessionManager);
        }
        userMarketQuery.verifyUserRecapEnablement(sessionClass.sessionName, sessionClass.classStruct.classKey);
        if(consumer!= null)
        {
            SessionKeyContainer sessionKey =
                    new SessionKeyContainer(sessionClass.sessionName, sessionClass.classStruct.classKey);
            RecapConsumerProxy listener = RecapConsumerProxyFactory.find(inProcessSessionManager, sessionKey, consumer);
            ChannelKey channelKey = new ChannelKey(ChannelKey.RECAP_BY_CLASS, sessionKey);
            ChannelListenerProxy channelListenerProxy =
                    internalEventChannel.addChannelListener(this, listener, channelKey);
            subscriptionService.addRecapClassInterest
                    (listener, sessionClass.sessionName, sessionClass.classStruct.classKey);
            String sessionClassKeyString = SessionKeyUserDataHelper.encode(sessionKey);
            try
            {
                InstrumentedChannelListenerProxy instrumentedCLProxy =
                        (InstrumentedChannelListenerProxy) channelListenerProxy;
                instrumentedCLProxy.addUserData(UserDataTypes.SESSION_CLASS, sessionClassKeyString);
            }
            catch(ClassCastException e)
            {
                Log.exception(this, "ClassCastException during addition of user data.", e);
            }
            String smgr = inProcessSessionManager.toString();
            String listenerName = listener.toString();
            StringBuilder suboid = new StringBuilder(smgr.length()+listenerName.length()+sessionClass.sessionName.length()+50);
            suboid.append("Sub:oid for ").append(smgr).append(' ').append(listenerName)
                  .append(" sessionName:").append(sessionClass.sessionName)
                  .append(" classKey:").append(sessionClass.classStruct.classKey);
            Log.information(this, suboid.toString());
            synchronized(consumer)
            {
                RecapStruct[] recapStructs =
                        userMarketQuery.getRecapsForClass(sessionClass.sessionName, sessionClass.classStruct.classKey);
                consumer.acceptRecap(recapStructs, 0, QueueActions.NO_ACTION);
            }
        }
        else
        {
            Log.alarm(this, "null recapConsumer found in subscribeRecapForClass for session:"+inProcessSessionManager);
        }
    }

    public void unsubscribeRecapForClass(SessionClassStruct sessionClass, RecapV2Consumer consumer)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeRecapForClass for " + inProcessSessionManager);
        }
        userMarketQuery.verifyUserRecapEnablement(sessionClass.sessionName, sessionClass.classStruct.classKey);
        if(consumer!= null)
        {
            SessionKeyContainer sessionKey =
                    new SessionKeyContainer(sessionClass.sessionName, sessionClass.classStruct.classKey);
            RecapConsumerProxy listener = RecapConsumerProxyFactory.find(inProcessSessionManager, sessionKey, consumer);
            subscriptionService.removeRecapClassInterest
                    (listener, sessionClass.sessionName, sessionClass.classStruct.classKey);
            ChannelKey channelKey = new ChannelKey(ChannelKey.RECAP_BY_CLASS, sessionKey);
            ChannelListenerProxy channelListenerProxy = internalEventChannel.
                    removeChannelListener(this, listener, channelKey);
            try
            {
                String sessionClassKeyString = SessionKeyUserDataHelper.encode(sessionKey);
                InstrumentedChannelListenerProxy instrumentedCLProxyListener =
                        (InstrumentedChannelListenerProxy) channelListenerProxy;
                instrumentedCLProxyListener.removeUserData(UserDataTypes.SESSION_CLASS, sessionClassKeyString);
            }
            catch(ClassCastException e)
            {
                Log.exception(this, "ClassCastException when removing sessionKey=" + sessionKey + " from user data", e);
            }
            String smgr = inProcessSessionManager.toString();
            String listenerName = listener.toString();
            StringBuilder unsuboid = new StringBuilder(smgr.length()+listenerName.length()+sessionClass.sessionName.length()+50);
            unsuboid.append("UnSub:oid for ").append(smgr).append(' ').append(listenerName)
                    .append(" sessionName:").append(sessionClass.sessionName)
                    .append(" classKey:").append(sessionClass.classStruct.classKey);
            Log.information(this, unsuboid.toString());
        }
        else
        {
            Log.alarm(this, "null recapConsumer found in unsubscribeRecapForClass for session:"+inProcessSessionManager);
        }
    }

    public void subscribeTickerForClass(SessionClassStruct sessionClass, TickerV2Consumer consumer)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeTickerForClass for " + inProcessSessionManager);
        }
        userMarketQuery.verifyUserTickerEnablement(sessionClass.sessionName, sessionClass.classStruct.classKey);
        if(consumer!= null)
        {
            SessionKeyContainer sessionKey =
                    new SessionKeyContainer(sessionClass.sessionName, sessionClass.classStruct.classKey);
            TickerConsumerProxy listener = TickerConsumerProxyFactory.find(inProcessSessionManager, sessionKey, consumer);
            ChannelKey channelKey = new ChannelKey(ChannelKey.TICKER_BY_CLASS, sessionKey);
            ChannelListenerProxy channelListenerProxy =
                    internalEventChannel.addChannelListener(this, listener, channelKey);
            subscriptionService.addTickerClassInterest
                    (listener, sessionClass.sessionName, sessionClass.classStruct.classKey);
            String sessionClassKeyString = SessionKeyUserDataHelper.encode(sessionKey);
            try
            {
                InstrumentedChannelListenerProxy instrumentedCLProxy =
                        (InstrumentedChannelListenerProxy) channelListenerProxy;
                instrumentedCLProxy.addUserData(UserDataTypes.SESSION_CLASS, sessionClassKeyString);
            }
            catch(ClassCastException e)
            {
                Log.exception(this, "ClassCastException during addition of user data.", e);
            }
            String smgr = inProcessSessionManager.toString();
            String listenerName = listener.toString();
            StringBuilder suboid = new StringBuilder(smgr.length()+listenerName.length()+sessionClass.sessionName.length()+50);
            suboid.append("Sub:oid for ").append(smgr).append(" ").append(listenerName)
                  .append(" sessionName:").append(sessionClass.sessionName)
                  .append(" classKey:").append(sessionClass.classStruct.classKey);
            Log.information(this, suboid.toString());
        }
        else
        {
            Log.alarm(this, "null tickerConsumer found in subscribeTickerForClass for session:"+inProcessSessionManager);
        }
    }

    public void unsubscribeTickerForClass(SessionClassStruct sessionClass, TickerV2Consumer consumer)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unsubscribeTickerForClass for " + inProcessSessionManager);
        }
        userMarketQuery.verifyUserTickerEnablement(sessionClass.sessionName, sessionClass.classStruct.classKey);
        if(consumer!= null)
        {
            SessionKeyContainer sessionKey =
                    new SessionKeyContainer(sessionClass.sessionName, sessionClass.classStruct.classKey);
            TickerConsumerProxy listener = TickerConsumerProxyFactory.find(inProcessSessionManager, sessionKey, consumer);
            subscriptionService.removeTickerClassInterest
                    (listener, sessionClass.sessionName, sessionClass.classStruct.classKey);
            ChannelKey channelKey = new ChannelKey(ChannelKey.TICKER_BY_CLASS, sessionKey);
            ChannelListenerProxy channelListenerProxy =
                    internalEventChannel.removeChannelListener(this, listener, channelKey);
            try
            {
                String sessionClassKeyString = SessionKeyUserDataHelper.encode(sessionKey);
                InstrumentedChannelListenerProxy instrumentedCLProxyListener =
                        (InstrumentedChannelListenerProxy) channelListenerProxy;
                instrumentedCLProxyListener.removeUserData(UserDataTypes.SESSION_CLASS, sessionClassKeyString);
            }
            catch(ClassCastException e)
            {
                Log.exception(this, "ClassCastException when removing sessionKey=" + sessionKey + " from user data", e);
            }
            String smgr = inProcessSessionManager.toString();
            String listenerName = listener.toString();
            StringBuilder unsuboid = new StringBuilder(smgr.length()+listenerName.length()+sessionClass.sessionName.length()+50);
            unsuboid.append("UnSub:oid for ").append(smgr).append(' ').append(listenerName)
                    .append(" sessionName:").append(sessionClass.sessionName)
                    .append(" classKey:").append(sessionClass.classStruct.classKey);
            Log.information(this, unsuboid.toString());
        }
        else
        {
            Log.alarm(this, "null tickerConsumer found in unsubscribeTickerForClass for session:"+inProcessSessionManager);
        }
    }

    private void cleanUpProxies()
    {
        CurrentMarketConsumerProxyFactory.remove(inProcessSessionManager);
        NBBOConsumerProxyFactory.remove(inProcessSessionManager);
        ExpectedOpeningPriceConsumerProxyFactory.remove(inProcessSessionManager);
        RecapConsumerProxyFactory.remove(inProcessSessionManager);
        TickerConsumerProxyFactory.remove(inProcessSessionManager);
        BookDepthConsumerProxyFactory.remove(inProcessSessionManager);
    }

    public void acceptUserSessionLogout()
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling acceptUserSessionLogout for " + inProcessSessionManager);
        }
        EventChannelAdapterFactory.find().removeListenerGroup(this);
        internalEventChannel.removeListenerGroup(this);
        LogoutServiceFactory.find().logoutComplete(inProcessSessionManager,this);
        cleanUpProxies();
        logoutProcessor.setParent(null);
        logoutProcessor = null;
    }
}
