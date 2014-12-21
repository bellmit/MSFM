/*
 * Created by IntelliJ IDEA.
 * User: torresl
 * Date: Oct 3, 2002
 * Time: 9:41:16 AM
 */
package com.cboe.intermarketPresentation.api;

import com.cboe.interfaces.intermarketPresentation.api.IntermarketAPI;
import com.cboe.interfaces.intermarketPresentation.api.IntermarketHeldOrderAPI;
import com.cboe.interfaces.intermarketPresentation.intermarketMessages.BookDepthDetailed;
import com.cboe.interfaces.presentation.product.SessionProductClass;
import com.cboe.interfaces.presentation.product.SessionProduct;
import com.cboe.idl.cmiIntermarket.NBBOAgentSessionManager;
import com.cboe.consumers.intermarketCallback.*;
import com.cboe.idl.cmiIntermarketMessages.CurrentIntermarketStruct;
import com.cboe.idl.cmiIntermarketMessages.AdminStruct;
import com.cboe.idl.cmiIntermarketMessages.BookDepthDetailedStruct;
import com.cboe.idl.cmiIntermarketCallback.CMIIntermarketOrderStatusConsumer;
import com.cboe.idl.cmiIntermarketCallback.CMINBBOAgentSessionAdmin;
import com.cboe.idl.cmiIntermarket.IntermarketUserSessionManager;
import com.cboe.idl.cmiIntermarket.IntermarketQuery;
import com.cboe.idl.cmiIntermarket.NBBOAgent;
import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.util.event.*;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.exceptions.*;
import com.cboe.util.ExceptionBuilder;
import com.cboe.domain.util.SessionKeyContainer;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.api.APIHome;
import com.cboe.intermarketPresentation.intermarketMessages.BookDepthDetailedFactory;

import java.util.HashSet;

public class IntermarketAPIImpl implements IntermarketAPI
{
    protected IntermarketUserSessionManager imUserSessionManager;
    protected IntermarketQuery intermarketQuery;
    protected NBBOAgent nbboAgent;
    protected CMIIntermarketOrderStatusConsumer intermarketOrderStatusConsumer;
    protected CMINBBOAgentSessionAdmin NBBOAgentSessionAdmin;
    protected EventChannelAdapter eventChannel;
    private HashSet registrationsMap;
    public IntermarketAPIImpl()
    {
        super();
        registrationsMap = new HashSet();
//        IntermarketHeldOrderAPIFactory.createIntermarketHeldOrderAPIInstance();
    }
    public void initialize(IntermarketUserSessionManager sessionManager)
        throws AuthorizationException, CommunicationException, SystemException
    {
        imUserSessionManager = sessionManager;
        intermarketQuery = imUserSessionManager.getIntermarketQuery();
        nbboAgent = imUserSessionManager.getNBBOAgent();
        eventChannel = EventChannelAdapterFactory.find();
        eventChannel.setDynamicChannels(true);
        initializeIntermarketCallbackConsumers();
    }

    protected void initializeIntermarketCallbackConsumers()
    {
        // Create all the CMIIntermarketCallback objects
        intermarketOrderStatusConsumer = IntermarketOrderStatusConsumerFactory.create(eventChannel);
        NBBOAgentSessionAdmin = NBBOAgentSessionAdminConsumerFactory.create(eventChannel);
    }

    public CurrentIntermarketStruct getIntermarketByProductForSession( int productKey, String session )
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException
    {
        return intermarketQuery.getIntermarketByProductForSession(productKey, session);
    }

    public CurrentIntermarketStruct[] getIntermarketByClassForSession( int classKey, String session )
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException
    {
        return intermarketQuery.getIntermarketByClassForSession(classKey, session);
    }

    public AdminStruct[] getAdminMessage(String sessionName, int productKey, int adminMessageKey,
                                         String sourceExchange)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotAcceptedException
    {
        return intermarketQuery.getAdminMessage(sessionName, productKey, adminMessageKey, sourceExchange);
    }

    public BookDepthDetailedStruct getDetailedOrderBook(String sessionName, int productKey)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException
    {
        return intermarketQuery.getDetailedOrderBook(sessionName, productKey);
    }

    public BookDepthDetailedStruct showMarketableOrderBookAtPrice(String sessionName, int productKey,PriceStruct price)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException
    {
        return intermarketQuery.showMarketableOrderBookAtPrice(sessionName, productKey, price);
    }

    public IntermarketHeldOrderAPI register(
        int classKey,
        String session,
        boolean forceOverride,
        EventChannelListener imOrderStatusListener,
        EventChannelListener nbboAgentSessionListener )
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, TransactionFailedException
    {
        SessionKeyContainer sessionKey = new SessionKeyContainer(session, classKey);
        ChannelKey key = new ChannelKey(ChannelType.CB_NEW_HELD_ORDER, sessionKey);
        eventChannel.addChannelListener(EventChannelAdapterFactory.find(), imOrderStatusListener, key);

        key = new ChannelKey(ChannelType.CB_CANCEL_HELD_ORDER_REQUEST, sessionKey);
        eventChannel.addChannelListener(EventChannelAdapterFactory.find(), imOrderStatusListener, key);

        key = new ChannelKey(ChannelType.CB_HELD_ORDERS, sessionKey);
        eventChannel.addChannelListener(EventChannelAdapterFactory.find(), imOrderStatusListener, key);

        key = new ChannelKey(ChannelType.CB_HELD_ORDER_CANCELED_REPORT, sessionKey);
        eventChannel.addChannelListener(EventChannelAdapterFactory.find(), imOrderStatusListener, key);

        key = new ChannelKey(ChannelType.CB_HELD_ORDER_FILLED_REPORT, sessionKey);
        eventChannel.addChannelListener(EventChannelAdapterFactory.find(), imOrderStatusListener, key);

        key = new ChannelKey(ChannelType.CB_FILL_REJECT_REPORT, sessionKey);
        eventChannel.addChannelListener(EventChannelAdapterFactory.find(), imOrderStatusListener, key);

        key = new ChannelKey(ChannelType.CB_NBBO_AGENT_FORCED_OUT, sessionKey);
        eventChannel.addChannelListener(EventChannelAdapterFactory.find(), nbboAgentSessionListener, key);

        key = new ChannelKey(ChannelType.CB_NBBO_AGENT_REMINDER, sessionKey);
        eventChannel.addChannelListener(EventChannelAdapterFactory.find(), nbboAgentSessionListener, key);

        key = new ChannelKey(ChannelType.CB_NBBO_AGENT_SATISFACTION_ALERT, sessionKey);
        eventChannel.addChannelListener(EventChannelAdapterFactory.find(), nbboAgentSessionListener, key);

        key = new ChannelKey(ChannelType.CB_NBBO_AGENT_INTERMARKET_ADMIN, sessionKey);
        eventChannel.addChannelListener(EventChannelAdapterFactory.find(), nbboAgentSessionListener, key);

        SessionKeyContainer noClassSessionKey = new SessionKeyContainer(session, 0);
        key = new ChannelKey(ChannelType.CB_NBBO_AGENT_INTERMARKET_ADMIN_BROADCAST, noClassSessionKey);
        eventChannel.addChannelListener(EventChannelAdapterFactory.find(), nbboAgentSessionListener, key);
        SessionProductClass spc = null;
        try
        {
            spc = APIHome.findProductQueryAPI().getClassByKeyForSession(session, classKey);
            IntermarketHeldOrderAPIFactory.createIntermarketHeldOrderAPIInstance(spc);
        }
        catch (NotFoundException e)
        {
            GUILoggerHome.find().exception(e, e.details.message);
            throw ExceptionBuilder.systemException(e.toString(), 0);
        }
        NBBOAgentSessionManager nbboAgentSessionManager = nbboAgent.registerAgent(classKey, session, forceOverride, intermarketOrderStatusConsumer, NBBOAgentSessionAdmin);
        IntermarketHeldOrderAPI intermarketHeldOrder = null;
        try
        {
            //intermarketHeldOrder = IntermarketHeldOrderAPIFactory.find(nbboAgentSessionManager);
            IntermarketHeldOrderAPIFactory.initializeIntermarketHeldOrderAPI(spc, nbboAgentSessionManager);
            synchronized (registrationsMap)
            {
                SessionKeyContainer skc = new SessionKeyContainer(session, classKey);
                registrationsMap.add(skc);
            }

        }
        catch (Exception e)
        {
            GUILoggerHome.find().exception(e);
            unregister(classKey, session, imOrderStatusListener, nbboAgentSessionListener);
            throw ExceptionBuilder.systemException(e.toString(), 0);
        }
        return IntermarketHeldOrderAPIFactory.find(spc);
    }

    public void unregister(
        int classKey,
        String session,
        EventChannelListener imOrderStatusListener,
        EventChannelListener nbboAgentSessionListener )
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException, TransactionFailedException
    {
        SessionKeyContainer sessionKey = new SessionKeyContainer(session, classKey);
        ChannelKey key = new ChannelKey(ChannelType.CB_NEW_HELD_ORDER, sessionKey);
        eventChannel.removeChannelListener(EventChannelAdapterFactory.find(), imOrderStatusListener, key);

        key = new ChannelKey(ChannelType.CB_CANCEL_HELD_ORDER_REQUEST, sessionKey);
        eventChannel.removeChannelListener(EventChannelAdapterFactory.find(), imOrderStatusListener, key);

        key = new ChannelKey(ChannelType.CB_HELD_ORDERS, sessionKey);
        eventChannel.removeChannelListener(EventChannelAdapterFactory.find(), imOrderStatusListener, key);

        key = new ChannelKey(ChannelType.CB_HELD_ORDER_CANCELED_REPORT, sessionKey);
        eventChannel.removeChannelListener(EventChannelAdapterFactory.find(), imOrderStatusListener, key);

        key = new ChannelKey(ChannelType.CB_HELD_ORDER_FILLED_REPORT, sessionKey);
        eventChannel.removeChannelListener(EventChannelAdapterFactory.find(), imOrderStatusListener, key);

        key = new ChannelKey(ChannelType.CB_FILL_REJECT_REPORT, sessionKey);
        eventChannel.removeChannelListener(EventChannelAdapterFactory.find(), imOrderStatusListener, key);

        key = new ChannelKey(ChannelType.CB_NBBO_AGENT_FORCED_OUT, sessionKey);
        eventChannel.removeChannelListener(EventChannelAdapterFactory.find(), nbboAgentSessionListener, key);

        key = new ChannelKey(ChannelType.CB_NBBO_AGENT_REMINDER, sessionKey);
        eventChannel.removeChannelListener(EventChannelAdapterFactory.find(), nbboAgentSessionListener, key);

        key = new ChannelKey(ChannelType.CB_NBBO_AGENT_SATISFACTION_ALERT, sessionKey);
        eventChannel.removeChannelListener(EventChannelAdapterFactory.find(), nbboAgentSessionListener, key);

        key = new ChannelKey(ChannelType.CB_NBBO_AGENT_INTERMARKET_ADMIN, sessionKey);
        eventChannel.removeChannelListener(EventChannelAdapterFactory.find(), nbboAgentSessionListener, key);

        SessionKeyContainer noClassSessionKey = new SessionKeyContainer(session, 0);
        key = new ChannelKey(ChannelType.CB_NBBO_AGENT_INTERMARKET_ADMIN_BROADCAST, noClassSessionKey);
        eventChannel.removeChannelListener(EventChannelAdapterFactory.find(), nbboAgentSessionListener, key);

        nbboAgent.unregisterAgent(classKey, session, intermarketOrderStatusConsumer, NBBOAgentSessionAdmin);
        synchronized(registrationsMap)
        {
            SessionKeyContainer skc = new SessionKeyContainer(session, classKey);
            registrationsMap.remove(skc);
        }
    }

    public boolean isRegistered(int classKey, String session)
    {
        synchronized(registrationsMap)
        {
            SessionKeyContainer skc = new SessionKeyContainer(session, classKey);
            return registrationsMap.contains(skc);
        }
    }

    public BookDepthDetailed getDetailedOrderBook(SessionProduct sessionProduct)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException, NotAcceptedException
    {
        return BookDepthDetailedFactory.createBookDepthDetailed(getDetailedOrderBook(sessionProduct.getTradingSessionName(), sessionProduct.getProductKey()));
    }
    
    /* Get current state of the OrderBook
     *
     * @param sessionName The session name where the method has been invoked.
     * @param productKey ProductKey whose orderBook state will be queried.
     *
     * @return boolean State of the OrderBook.
     *
     * @throw SystemException
     * @throw CommunicationException
     * @throw AuthorizationException
     * @throw DataValidationException
     * @throw TransactionFailedException
     * @throw NotAccetedException
     *
     * @author Sandip Chatterjee
     */
    public short getOrderBookStatus(
        String sessionName,
        int productKey
        )
        throws SystemException,
               CommunicationException,
               AuthorizationException,
               DataValidationException,
               NotFoundException,
               NotAcceptedException
    {
        return intermarketQuery.getOrderBookStatus(sessionName,productKey);
    }
}
