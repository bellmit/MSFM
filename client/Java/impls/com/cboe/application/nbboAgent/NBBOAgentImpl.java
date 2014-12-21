package com.cboe.application.nbboAgent;

import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.application.*;
import com.cboe.interfaces.businessServices.NBBOAgentService;
import com.cboe.interfaces.domain.session.BaseSessionManager;
import com.cboe.idl.cmiIntermarketCallback.CMIIntermarketOrderStatusConsumer;
import com.cboe.idl.cmiIntermarketCallback.CMINBBOAgentSessionAdmin;
import com.cboe.idl.cmiErrorCodes.DataValidationCodes;
import com.cboe.idl.cmiOrder.CancelReportStruct;
import com.cboe.idl.cmiOrder.FilledReportStruct;
import com.cboe.idl.cmiOrder.OrderDetailStruct;
import com.cboe.idl.cmiOrder.OrderStruct;
import com.cboe.idl.cmiIntermarketMessages.*;
import com.cboe.idl.cmiIntermarket.NBBOAgentSessionManagerHelper;
import com.cboe.idl.cmiProduct.ProductNameStruct;
import com.cboe.idl.cmiProduct.ProductKeysStruct;
import com.cboe.idl.cmiConstants.StatusUpdateReasons;
import com.cboe.idl.cmiUtil.CboeIdStruct;
import com.cboe.exceptions.*;
import com.cboe.application.shared.consumer.*;
import com.cboe.application.shared.ServicesHelper;
import com.cboe.application.shared.RemoteConnectionFactory;
import com.cboe.application.shared.POANameHelper;
import com.cboe.application.supplier.HeldOrderSupplier;
import com.cboe.application.supplier.HeldOrderSupplierFactory;
import com.cboe.application.supplier.NBBOAgentAdminSupplier;
import com.cboe.application.supplier.NBBOAgentAdminSupplierFactory;
import com.cboe.util.event.EventChannelAdapterFactory;
import com.cboe.util.ExceptionBuilder;
import com.cboe.util.ChannelKey;
import com.cboe.util.ChannelType;
import com.cboe.util.channel.ChannelEvent;
import com.cboe.util.channel.ChannelListener;
import com.cboe.domain.logout.LogoutServiceFactory;
import com.cboe.domain.util.*;
import com.cboe.delegates.application.NBBOAgentSessionManagerDelegate;

import java.util.Enumeration;
import java.util.Hashtable;
import com.cboe.domain.instrumentedChannel.event.InstrumentedEventChannelAdapterFactory;

/**
    NBBOAgent collects Held Order Status as well as NBBO Agent admin message.
    It creates a NBBOAgentSessionManger when the NBBO Agent registeration is succesful

 @author Emily Huang
*/

public class NBBOAgentImpl extends BObject implements NBBOAgentAdminCollector, HeldOrderCollector, NBBOAgent, UserSessionLogoutCollector{

    protected SessionManager sessionManager;  // current session user logged in
    protected String userId;

    protected HeldOrderSupplier heldOrderSupplier;
    protected HeldOrderProcessor heldOrderProcessor;

    protected NBBOAgentAdminSupplier nbboAgentAdminSupplier;
    protected NBBOAgentAdminProcessor nbboAgentAdminProcessor;

    protected Hashtable heldOrderListenersBySessionClass;
    protected Hashtable nbboAgentListenersBySessionClass;
    protected Hashtable sessionClasses;

    protected NBBOAgentService nbboAgentService;
    private ProductQueryServiceAdapter pqAdapter;
    private static final Integer INT_0 = 0;

    // Event Channel Processors
    private UserSessionLogoutProcessor logoutProcessor;

    public NBBOAgentImpl()
    {
        super();
    }

    public void setSessionManager(SessionManager theSession)
    {
        sessionManager = theSession;

        logoutProcessor = UserSessionLogoutProcessorFactory.create(this);
        EventChannelAdapterFactory.find().addChannelListener(this, logoutProcessor, theSession);
        LogoutServiceFactory.find().addLogoutListener(theSession, this);

        try  {
        userId = theSession.getValidSessionProfileUser().userId;
        } catch ( Exception e)
        {
            Log.exception(this, "couldn't get userId", e);
        }
    }

    /** Get the current session manager. Used by SessionBasedCollector. */
    public BaseSessionManager getSessionManager()
    {
        return sessionManager;
    }

    public void acceptUserSessionLogout()
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling acceptUserSessionLogout for " + sessionManager);
        }

        cleanUpRegistration();
        EventChannelAdapterFactory.find().removeListenerGroup(this);
        EventChannelAdapterFactory.find().removeChannel(this);
        LogoutServiceFactory.find().logoutComplete(sessionManager,this);

        // clean up the suppliers
        heldOrderSupplier.removeListenerGroup(this);
        heldOrderSupplier = null;

        nbboAgentAdminSupplier.removeListenerGroup(this);
        nbboAgentAdminSupplier = null;

        // Clean up the processors
        heldOrderProcessor.setParent(null);
        heldOrderProcessor = null;
        logoutProcessor.setParent(null);
        logoutProcessor = null;

        // Clean up instance variables.
        nbboAgentService = null;
        sessionManager = null;
        
        pqAdapter = null;
    }

    /**
     * subscribes the Held Order for given classes
     *
     * @param classKey the class's key
     * @param heldOrderConsumer the client call back reference
     * @exception SystemException System Error
     * @exception CommunicationException Communication Error
     * @exception AuthorizationException Authorization Error
     * @exception DataValidationException Data Validation Error
     */
    protected void subscribeHeldOrder(String sessionName, int classKey, CMIIntermarketOrderStatusConsumer heldOrderConsumer)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeHeldOrder for " + sessionManager);
        }

        if ( heldOrderConsumer != null)
        {
                //verify if it is enabled
                //getUserEnablementService().verfiyUserEnablement(sessionName, productType, classKey);
                // do we need to consider user enablement here?

                ///////// add the call back consumer to the supplier list/////
                SessionKeyContainer key = new SessionKeyContainer(sessionName, classKey);

                ChannelKey channelKey = new ChannelKey(ChannelType.CB_HELD_ORDER_CANCELED_REPORT, key);
                ChannelListener proxyListener = ServicesHelper.getHeldOrderConsumerProxy(heldOrderConsumer, sessionManager);
                heldOrderSupplier.addChannelListener(this, proxyListener, channelKey);

                channelKey = new ChannelKey(ChannelType.CB_HELD_ORDER_FILLED_REPORT, key);
                heldOrderSupplier.addChannelListener(this, proxyListener, channelKey);

                channelKey = new ChannelKey(ChannelType.CB_HELD_ORDERS, key);
                heldOrderSupplier.addChannelListener(this, proxyListener, channelKey);

                channelKey = new ChannelKey(ChannelType.CB_NEW_HELD_ORDER, key);
                heldOrderSupplier.addChannelListener(this, proxyListener, channelKey);

                channelKey = new ChannelKey(ChannelType.CB_CANCEL_HELD_ORDER_REQUEST, key);
                heldOrderSupplier.addChannelListener(this, proxyListener, channelKey);

                channelKey = new ChannelKey(ChannelType.CB_FILL_REJECT_REPORT, key);
                heldOrderSupplier.addChannelListener(this, proxyListener, channelKey);

                channelKey = new ChannelKey(ChannelType.NEW_HELD_ORDER, key);
                InstrumentedEventChannelAdapterFactory.find().addChannelListener(this, heldOrderProcessor, channelKey);

                channelKey = new ChannelKey(ChannelType.HELD_ORDER_CANCEL_REPORT, key);
                InstrumentedEventChannelAdapterFactory.find().addChannelListener(this, heldOrderProcessor, channelKey);

                channelKey = new ChannelKey(ChannelType.HELD_ORDER_STATUS, key);
                InstrumentedEventChannelAdapterFactory.find().addChannelListener(this, heldOrderProcessor, channelKey);

                channelKey = new ChannelKey(ChannelType.HELD_ORDERS, key);
                InstrumentedEventChannelAdapterFactory.find().addChannelListener(this, heldOrderProcessor, channelKey);

                channelKey = new ChannelKey(ChannelType.HELD_ORDER_FILLED_REPORT, key);
                InstrumentedEventChannelAdapterFactory.find().addChannelListener(this, heldOrderProcessor, channelKey);

                channelKey = new ChannelKey(ChannelType.CANCEL_HELD_ORDER, key);
                InstrumentedEventChannelAdapterFactory.find().addChannelListener(this, heldOrderProcessor, channelKey);

                channelKey = new ChannelKey(ChannelType.FILL_REJECT_REPORT, key);
                InstrumentedEventChannelAdapterFactory.find().addChannelListener(this, heldOrderProcessor, channelKey);
        }

    }
    protected void unSubscribeHeldOrder(String sessionName, int classKey, CMIIntermarketOrderStatusConsumer heldOrderConsumer)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unSubscribeHeldOrder for " + sessionManager);
        }

        if ( heldOrderConsumer != null)
        {
                //verify if it is enabled
                //getUserEnablementService().verfiyUserEnablement(sessionName, productType, classKey);

                ///////// add the call back consumer to the supplier list/////
            SessionKeyContainer key = new SessionKeyContainer(sessionName, classKey);

            ChannelKey channelKey = new ChannelKey(ChannelType.CB_HELD_ORDER_CANCELED_REPORT, key);
            ChannelListener proxyListener = ServicesHelper.getHeldOrderConsumerProxy(heldOrderConsumer, sessionManager);
            heldOrderSupplier.removeChannelListener(this, proxyListener, channelKey);

            channelKey = new ChannelKey(ChannelType.CB_HELD_ORDER_FILLED_REPORT, key);
            heldOrderSupplier.removeChannelListener(this, proxyListener, channelKey);

            channelKey = new ChannelKey(ChannelType.CB_HELD_ORDERS, key);
            heldOrderSupplier.removeChannelListener(this, proxyListener, channelKey);

            channelKey = new ChannelKey(ChannelType.CB_NEW_HELD_ORDER, key);
            heldOrderSupplier.removeChannelListener(this, proxyListener, channelKey);

            channelKey = new ChannelKey(ChannelType.CB_CANCEL_HELD_ORDER_REQUEST, key);
            heldOrderSupplier.removeChannelListener(this, proxyListener, channelKey);

            channelKey = new ChannelKey(ChannelType.NEW_HELD_ORDER, key);
            InstrumentedEventChannelAdapterFactory.find().removeChannelListener(this, heldOrderProcessor, channelKey);

            channelKey = new ChannelKey(ChannelType.HELD_ORDER_CANCEL_REPORT, key);
            InstrumentedEventChannelAdapterFactory.find().removeChannelListener(this, heldOrderProcessor, channelKey);

            channelKey = new ChannelKey(ChannelType.HELD_ORDER_STATUS, key);
            InstrumentedEventChannelAdapterFactory.find().removeChannelListener(this, heldOrderProcessor, channelKey);

            channelKey = new ChannelKey(ChannelType.HELD_ORDERS, key);
            InstrumentedEventChannelAdapterFactory.find().removeChannelListener(this, heldOrderProcessor, channelKey);

            channelKey = new ChannelKey(ChannelType.HELD_ORDER_FILLED_REPORT, key);
            InstrumentedEventChannelAdapterFactory.find().removeChannelListener(this, heldOrderProcessor, channelKey);

            channelKey = new ChannelKey(ChannelType.CANCEL_HELD_ORDER, key);
            InstrumentedEventChannelAdapterFactory.find().removeChannelListener(this, heldOrderProcessor, channelKey);

            channelKey = new ChannelKey(ChannelType.FILL_REJECT_REPORT, key);
            InstrumentedEventChannelAdapterFactory.find().removeChannelListener(this, heldOrderProcessor, channelKey);
        }

    }

    /**
     * subscribes the NBBO Agent admin message and Satisfaction alert
     *
     * @param classKey the class's key
     * @param nbboAdmin the client call back reference
     * @exception SystemException System Error
     * @exception CommunicationException Communication Error
     * @exception AuthorizationException Authorization Error
     * @exception DataValidationException Data Validation Error
     */
    protected void subscribeNBBOAgentAdmin(String sessionName, int classKey, CMINBBOAgentSessionAdmin nbboAdmin)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling subscribeNBBOAgentAdmin for " + sessionManager);
        }

        if ( nbboAdmin != null)
        {
            ///////// add the call back consumer to the supplier list/////
            UserSessionClassContainer key = new UserSessionClassContainer(userId, sessionName, classKey);
            SessionKeyContainer sessionKey = new SessionKeyContainer(sessionName, classKey);

            ChannelListener proxyListener = ServicesHelper.getNBBOAgentSessionAdminProxy(nbboAdmin, sessionManager);

            ChannelKey channelKey = new ChannelKey(ChannelType.CB_NBBO_AGENT_FORCED_OUT, key);
            nbboAgentAdminSupplier.addChannelListener(this, proxyListener, channelKey);

            channelKey = new ChannelKey(ChannelType.CB_NBBO_AGENT_REMINDER, key);
            nbboAgentAdminSupplier.addChannelListener(this, proxyListener, channelKey);

            channelKey = new ChannelKey(ChannelType.CB_NBBO_AGENT_INTERMARKET_ADMIN, key);
            nbboAgentAdminSupplier.addChannelListener(this, proxyListener, channelKey);

            channelKey = new ChannelKey(ChannelType.CB_NBBO_AGENT_INTERMARKET_ADMIN_BROADCAST, new UserSessionClassContainer(userId, sessionName, 0));
            nbboAgentAdminSupplier.addChannelListener(this, proxyListener, channelKey);

            channelKey = new ChannelKey(ChannelType.CB_NBBO_AGENT_SATISFACTION_ALERT, key);
            nbboAgentAdminSupplier.addChannelListener(this, proxyListener, channelKey);

            channelKey = new ChannelKey(ChannelType.NBBO_AGENT_FORCED_OUT, key);
            InstrumentedEventChannelAdapterFactory.find().addChannelListener(this, nbboAgentAdminProcessor, channelKey);

            channelKey = new ChannelKey(ChannelType.NBBO_AGENT_REMINDER, key);
            InstrumentedEventChannelAdapterFactory.find().addChannelListener(this, nbboAgentAdminProcessor, channelKey);

            channelKey = new ChannelKey(ChannelType.INTERMARKET_ADMIN_MESSAGE, sessionKey);
            InstrumentedEventChannelAdapterFactory.find().addChannelListener(this, nbboAgentAdminProcessor, channelKey);

            channelKey = new ChannelKey(ChannelType.INTERMARKET_ADMIN_MESSAGE_BROADCAST, INT_0);
            InstrumentedEventChannelAdapterFactory.find().addChannelListener(this, nbboAgentAdminProcessor, channelKey);

            channelKey = new ChannelKey(ChannelType.ALERT_SATISFACTION, sessionKey);
            InstrumentedEventChannelAdapterFactory.find().addChannelListener(this, nbboAgentAdminProcessor, channelKey);

        }

    }// end of subscribeNBBOAgentAdmin

    protected void unSubscribeNBBOAgentAdmin(String sessionName, int classKey, CMINBBOAgentSessionAdmin nbboAdmin)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unSubscribeNBBOAgentAdmin for " + sessionManager);
        }

        if ( nbboAdmin != null)
        {
                //verify if it is enabled
                //getUserEnablementService().verfiyUserEnablement(sessionName, productType, classKey);

                ///////// add the call back consumer to the supplier list/////
            UserSessionClassContainer key = new UserSessionClassContainer(userId, sessionName, classKey);
            SessionKeyContainer sessionClass = new SessionKeyContainer(sessionName, classKey);

            ChannelListener proxyListener = ServicesHelper.getNBBOAgentSessionAdminProxy(nbboAdmin, sessionManager);
            ChannelKey channelKey = new ChannelKey(ChannelType.CB_NBBO_AGENT_FORCED_OUT, key);
            nbboAgentAdminSupplier.removeChannelListener(this, proxyListener, channelKey);

            channelKey = new ChannelKey(ChannelType.CB_NBBO_AGENT_REMINDER, key);
            nbboAgentAdminSupplier.removeChannelListener(this, proxyListener, channelKey);

            channelKey = new ChannelKey(ChannelType.CB_NBBO_AGENT_INTERMARKET_ADMIN, key);
            nbboAgentAdminSupplier.removeChannelListener(this, proxyListener, channelKey);

            channelKey = new ChannelKey(ChannelType.CB_NBBO_AGENT_INTERMARKET_ADMIN_BROADCAST, new UserSessionClassContainer(userId, sessionName, 0));
            nbboAgentAdminSupplier.removeChannelListener(this, proxyListener, channelKey);

            channelKey = new ChannelKey(ChannelType.CB_NBBO_AGENT_SATISFACTION_ALERT, key);
            nbboAgentAdminSupplier.removeChannelListener(this, proxyListener, channelKey);

            channelKey = new ChannelKey(ChannelType.NBBO_AGENT_FORCED_OUT, key);
            InstrumentedEventChannelAdapterFactory.find().removeChannelListener(this, nbboAgentAdminProcessor, channelKey);

            channelKey = new ChannelKey(ChannelType.NBBO_AGENT_REMINDER, key);
            InstrumentedEventChannelAdapterFactory.find().removeChannelListener(this, nbboAgentAdminProcessor, channelKey);

            channelKey = new ChannelKey(ChannelType.INTERMARKET_ADMIN_MESSAGE_BROADCAST, INT_0);
            InstrumentedEventChannelAdapterFactory.find().removeChannelListener(this, nbboAgentAdminProcessor, channelKey);

            channelKey = new ChannelKey(ChannelType.INTERMARKET_ADMIN_MESSAGE, sessionClass);
            InstrumentedEventChannelAdapterFactory.find().removeChannelListener(this, nbboAgentAdminProcessor, channelKey);

            channelKey = new ChannelKey(ChannelType.ALERT_SATISFACTION, sessionClass);
            InstrumentedEventChannelAdapterFactory.find().removeChannelListener(this, nbboAgentAdminProcessor, channelKey);

        }

    }// end of subscribeNBBOAgentAdmin



    public void create(String name)
    {
        super.create(name);

        // init the nbboAgent service
    	getNBBOAgentService();

        heldOrderSupplier = HeldOrderSupplierFactory.create();
        // Make Sure all suppliers are dynamic channels
        heldOrderSupplier.setDynamicChannels(true);

        heldOrderProcessor = HeldOrderProcessorFactory.create(this);

        nbboAgentAdminSupplier = NBBOAgentAdminSupplierFactory.create();
        // Make Sure all suppliers are dynamic channels
        nbboAgentAdminSupplier.setDynamicChannels(true);

        nbboAgentAdminProcessor = NBBOAgentAdminProcessorFactory.create(this);
        heldOrderListenersBySessionClass = new Hashtable(11);
        nbboAgentListenersBySessionClass = new Hashtable(11);
        sessionClasses = new Hashtable(11);

    }// end of create


   /** user need to register with NBBO Agent service first
    *  an instance of NBBOAgentSessionManager will be return after registering with NBBO Agent Service
    */
    public com.cboe.idl.cmiIntermarket.NBBOAgentSessionManager registerAgent(int classKey, String sessionName, boolean forceOverride, CMIIntermarketOrderStatusConsumer consumer, CMINBBOAgentSessionAdmin admin)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, TransactionFailedException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling register for session:"+sessionName+" classKey:"+classKey+" forceOverride:"+forceOverride);
        }

        if (admin == null)  {
            throw ExceptionBuilder.dataValidationException("Need a valid imorderstatus consumer", DataValidationCodes.MISSING_LISTENER);
        }
        if (consumer == null)  {
            throw ExceptionBuilder.dataValidationException("Need a valid im session admin", DataValidationCodes.MISSING_LISTENER);
        }

        // register with NBBOAgentService
        nbboAgentService.registerNBBOAgent(sessionName, userId, classKey, forceOverride);

        // register on IEC
        this.subscribeHeldOrder(sessionName, classKey, consumer);
        this.subscribeNBBOAgentAdmin(sessionName, classKey, admin);


        // add fillter on event channel
        this.addHeldOrderClassInterest(sessionName, classKey);
        this.addNBBOAgentAdminInterest(sessionName, classKey);

        // make sure we didn't miss any held orders
        nbboAgentService.publishHeldOrdersForClass(sessionName, classKey, userId);
        SessionKeyContainer sessionClass = new SessionKeyContainer(sessionName, classKey);
        addSessionClass(sessionClass, consumer, admin);

        // if registeration with NBBOAgentService is succesful, build NBBOAgentSessionManager.
        try {
            NBBOAgentSessionManagerHome home = ServicesHelper.getNBBOAgentSessionManagerHome();
            String poaName = POANameHelper.getPOAName((BOHome)home);
            NBBOAgentSessionManager nbboAgentSession = home.find(sessionManager);
            NBBOAgentSessionManagerDelegate delegate = new NBBOAgentSessionManagerDelegate(nbboAgentSession);
            org.omg.CORBA.Object obj = (org.omg.CORBA.Object) RemoteConnectionFactory.find().
                    register_object(delegate, poaName);
            com.cboe.idl.cmiIntermarket.NBBOAgentSessionManager nbboAgentSessionCorba
                    = NBBOAgentSessionManagerHelper.narrow(obj);

            nbboAgentSession.setRemoteDelegate(nbboAgentSessionCorba);

            return nbboAgentSessionCorba;
        }
        catch( Exception poae )
        {
            //need to unregister if register is alreday succesful
            // unregisterAgentBySessionClass(sessionClass);
            unregisterAgent(classKey, sessionName, consumer, admin);
            throw ExceptionBuilder.systemException("Could not bind nbboAgentSessionCorba", 1);
        }

    }

    public void unregisterAgent(int classKey, String sessionName, CMIIntermarketOrderStatusConsumer consumer, CMINBBOAgentSessionAdmin admin)
            throws SystemException, CommunicationException, AuthorizationException, DataValidationException, TransactionFailedException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling unregister for session:" + sessionName +" classKey:"+classKey);
        }
        nbboAgentService.unregisterNBBOAgent(sessionName, sessionManager.getValidSessionProfileUser().userId, classKey );
        unregisterAgentInternal(classKey, sessionName, consumer, admin);
    }

    /**
     * Following is HeldOrderCollector interface implementation
     */
    public void acceptNewHeldOrder(String sessionName, int classKey, HeldOrderStruct heldOrder)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling acceptNewHeldOrder for " + sessionManager);
        }

        HeldOrderDetailStruct heldOrderDetail = buildHeldOrderDetailStruct(heldOrder, StatusUpdateReasons.NEW);

	    ChannelKey key = new ChannelKey(ChannelType.CB_NEW_HELD_ORDER, new SessionKeyContainer(sessionName, classKey));
	    ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, key, heldOrderDetail);
	    heldOrderSupplier.dispatch(event);

    }

    public void acceptHeldOrders(String sessionName, int classKey, HeldOrderStruct[] heldOrders)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling acceptHeldOrders for " + sessionManager);
        }

        HeldOrderDetailStruct[] heldOrderDetails = buildHeldOrderDetailStruct(heldOrders, StatusUpdateReasons.NEW);

	    ChannelKey key = new ChannelKey(ChannelType.CB_HELD_ORDERS, new SessionKeyContainer(sessionName, classKey));
	    ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, key, heldOrderDetails);
	    heldOrderSupplier.dispatch(event);

    }

    public void acceptCancelHeldOrderRequest(String sessionName, int classKey, ProductKeysStruct productKeys, HeldOrderCancelRequestStruct cancelRequest )
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling acceptCancelHeldOrderRequest for " + sessionManager);
        }

	    ChannelKey key = new ChannelKey(ChannelType.CB_CANCEL_HELD_ORDER_REQUEST, new SessionKeyContainer(sessionName, classKey));
        HeldOrderCancelRequestContainer cancelRequestContainer = new HeldOrderCancelRequestContainer(productKeys, cancelRequest);
        ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, key, cancelRequestContainer);
	    heldOrderSupplier.dispatch(event);
    }

    public void acceptHeldOrderStatus(String sessionName, int classKey, HeldOrderStruct heldOrder)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling acceptHeldOrderStatus for " + sessionManager);
        }

        HeldOrderDetailStruct heldOrderDetail = buildHeldOrderDetailStruct(heldOrder, StatusUpdateReasons.UPDATE);
        HeldOrderDetailStruct[] heldOrdersDetail = {heldOrderDetail};
	    ChannelKey key = new ChannelKey(ChannelType.CB_HELD_ORDERS, new SessionKeyContainer(sessionName, classKey));
 	    ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, key, heldOrdersDetail);
	    heldOrderSupplier.dispatch(event);
    }

    public void acceptHeldOrderCanceledReport(String sessionName, int classKey, HeldOrderStruct heldOrder, CboeIdStruct cancelId,  CancelReportStruct cancleReport )
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling acceptHeldOrderCanceledReport for " + sessionManager);
        }

        HeldOrderCancelReportStruct heldOrderCanceledReport;

        HeldOrderDetailStruct heldOrderDetail = buildHeldOrderDetailStruct(heldOrder, StatusUpdateReasons.CANCEL);
        heldOrderCanceledReport = new HeldOrderCancelReportStruct(heldOrderDetail,
                                                       cancelId,
                                                       cancleReport);

	    ChannelKey key = new ChannelKey(ChannelType.CB_HELD_ORDER_CANCELED_REPORT, new SessionKeyContainer(sessionName, classKey));
	    ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, key, heldOrderCanceledReport);

	    heldOrderSupplier.dispatch(event);
    }

    public void acceptHeldOrderFilledReport(String sessionName, int classKey, HeldOrderStruct heldOrder, FilledReportStruct[] filledReport )
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling acceptHeldOrderFilledReport for " + sessionManager);
        }

        HeldOrderDetailStruct heldOrderDetail = buildHeldOrderDetailStruct(heldOrder, StatusUpdateReasons.FILL);
        HeldOrderFilledReportStruct heldOrderfilledReport = new HeldOrderFilledReportStruct(heldOrderDetail, filledReport);

	    ChannelKey key = new ChannelKey(ChannelType.CB_HELD_ORDER_FILLED_REPORT, new SessionKeyContainer(sessionName, classKey));
	    ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, key, heldOrderfilledReport);
	    heldOrderSupplier.dispatch(event);
    }

    public void acceptFillRejectReport(String sessionName, int classKey, FillRejectStruct[] fillRejects)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling acceptFillRejectReport for " + sessionManager);
        }
        SessionKeyContainer sessionKey = new SessionKeyContainer(sessionName, classKey);
	    ChannelKey key = new ChannelKey(ChannelType.CB_FILL_REJECT_REPORT, sessionKey);
        OrderDetailStruct orderDetail = buildOrderDetailStruct(fillRejects[0].order, StatusUpdateReasons.FILL);
        OrderFillRejectStruct orderFillReject = new OrderFillRejectStruct(orderDetail, fillRejects );
	    ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, key, orderFillReject);
	    heldOrderSupplier.dispatch(event);
    }

    // implementation for NBBOAgentAdminCollector interface
    public void acceptForcedOut(String reason, int classKey, String sessionName)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling acceptForcedOut for " + sessionManager);
        }
        UserSessionClassContainer userSessionKey = new UserSessionClassContainer(userId, sessionName, classKey);
	    ChannelKey key = new ChannelKey(ChannelType.CB_NBBO_AGENT_FORCED_OUT, userSessionKey);
	    ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, key, reason);
	    nbboAgentAdminSupplier.dispatch(event);
        try
        {
            unregisterAgentBySessionClass(new SessionKeyContainer(sessionName,classKey) );
        }
        catch(Exception e)
        {
            Log.exception(this, "user:" + userId, e);
        }
    }

    public void acceptReminder(OrderReminderStruct orderReminder, int classKey, String sessionName)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling acceptReminder for " + sessionManager);
        }

	    ChannelKey key = new ChannelKey(ChannelType.CB_NBBO_AGENT_REMINDER,
	                                    new UserSessionClassContainer(userId, sessionName, classKey));
	    ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, key, orderReminder);
	    nbboAgentAdminSupplier.dispatch(event);
    }

    public void acceptSatisfactionAlert(SatisfactionAlertStruct alert)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling acceptSatisfactionAlert for " + sessionManager);
        }
	    ChannelKey key = new ChannelKey(ChannelType.CB_NBBO_AGENT_SATISFACTION_ALERT,
	                                    new UserSessionClassContainer(userId, alert.lastSale.sessionName, alert.lastSale.productKeys.classKey));
	    ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, key, alert);
	    nbboAgentAdminSupplier.dispatch(event);
    }

    public void acceptIntermarketAdminMessage(String sessionName, String srcExchange, ProductKeysStruct productKeys, AdminStruct adminMessage)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling acceptIntermarketAdminMessage for " + sessionManager);
        }
	    ChannelKey key = new ChannelKey(ChannelType.CB_NBBO_AGENT_INTERMARKET_ADMIN,
	                                    new UserSessionClassContainer(userId, sessionName, productKeys.classKey));
        IntermarketAdminMessageContainer container = new IntermarketAdminMessageContainer(productKeys, adminMessage, srcExchange, sessionName);
	    ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, key, container);
	    nbboAgentAdminSupplier.dispatch(event);
    }

    public void acceptBroadcastIntermarketAdminMessage(String sessionName, String srcExchange, AdminStruct adminMessage)
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "calling acceptBroadcastIntermarketAdminMessage for " + sessionManager);
        }
	    ChannelKey key = new ChannelKey(ChannelType.CB_NBBO_AGENT_INTERMARKET_ADMIN_BROADCAST,
	                                     new UserSessionClassContainer(userId, sessionName, 0));
        IntermarketBroadcastMessageContainer container = new IntermarketBroadcastMessageContainer(adminMessage, srcExchange, sessionName);
	    ChannelEvent event = EventChannelAdapterFactory.find().getChannelEvent(this, key, container);
	    nbboAgentAdminSupplier.dispatch(event);
    }
    // impemetnation for NBBOAgentAdminCollector interface ends

    private NBBOAgentService getNBBOAgentService()
    {
        if ( nbboAgentService == null )
        {
            nbboAgentService = ServicesHelper.getNBBOAgentService();
        }

        return nbboAgentService;
    }

    private void addSessionClass(SessionKeyContainer sessionClass,CMIIntermarketOrderStatusConsumer consumer, CMINBBOAgentSessionAdmin admin )
    {
        sessionClasses.put(sessionClass, sessionClass);
        heldOrderListenersBySessionClass.put(sessionClass,consumer);
        nbboAgentListenersBySessionClass.put(sessionClass,admin);
    }

    private void removeSessionClass(SessionKeyContainer sessionClass)
    {
        heldOrderListenersBySessionClass.remove(sessionClass);
        nbboAgentListenersBySessionClass.remove(sessionClass);
        sessionClasses.remove(sessionClass);
    }

    private void cleanUpRegistration()
    {
        Enumeration sessionClassesEnum = sessionClasses.elements();

		SessionKeyContainer sessionClass = null;
		while(sessionClassesEnum.hasMoreElements())
		{
            try
            {
		        sessionClass = (SessionKeyContainer)sessionClassesEnum.nextElement();
                unregisterAgentBySessionClass(sessionClass);
            }
            catch(Exception e)
            {
                Log.exception(this, "user:" + userId, e);
            }
        }
    }

    private void unregisterAgentInternal(int classKey, String sessionName, CMIIntermarketOrderStatusConsumer consumer, CMINBBOAgentSessionAdmin admin)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        unSubscribeNBBOAgentAdmin(sessionName,classKey, admin);
        unSubscribeHeldOrder(sessionName,classKey, consumer);

        // remove fillter on event channel
        removeHeldOrderClassInterest(sessionName, classKey);
        removeNBBOAgentAdminInterest(sessionName, classKey);

        // remove (sessionName + classKey) from current NBBOAgent's registeration list
        removeSessionClass(new SessionKeyContainer(sessionName, classKey));

        // remove an instance of NBBOAgentSessionManager, which is created in register method.
        // ServicesHelper.getNBBOAgentSessionManagerHome().remove(sessionManager);
    }

    private void unregisterAgentBySessionClass(SessionKeyContainer sessionClass)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        CMIIntermarketOrderStatusConsumer consumer = (CMIIntermarketOrderStatusConsumer)heldOrderListenersBySessionClass.get(sessionClass);
        CMINBBOAgentSessionAdmin admin = (CMINBBOAgentSessionAdmin)nbboAgentListenersBySessionClass.get(sessionClass);
        unregisterAgentInternal(sessionClass.getKey(), sessionClass.getSessionName(), consumer, admin);
    }

    private void addHeldOrderClassInterest(String sessionName, int classKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Adding held order status class filter session:" + sessionName + " classKey:" + classKey);
        }
        ChannelKey channelKey;
        SessionKeyContainer sessionContainer = new SessionKeyContainer(sessionName, classKey);
        if (heldOrderListenersBySessionClass.get(sessionContainer) == null)
        {
            channelKey = new ChannelKey(ChannelType.HELD_ORDER_CANCEL_REPORT, sessionContainer);
            ServicesHelper.getIntermarketOrderStatusConsumerHome().addFilter(channelKey);

            channelKey = new ChannelKey(ChannelType.HELD_ORDER_FILLED_REPORT, sessionContainer);
            ServicesHelper.getIntermarketOrderStatusConsumerHome().addFilter(channelKey);

            channelKey = new ChannelKey(ChannelType.HELD_ORDER_STATUS, sessionContainer);
            ServicesHelper.getIntermarketOrderStatusConsumerHome().addFilter(channelKey);

            channelKey = new ChannelKey(ChannelType.HELD_ORDERS, sessionContainer);
            ServicesHelper.getIntermarketOrderStatusConsumerHome().addFilter(channelKey);

            channelKey = new ChannelKey(ChannelType.NEW_HELD_ORDER, sessionContainer);
            ServicesHelper.getIntermarketOrderStatusConsumerHome().addFilter(channelKey);

            channelKey = new ChannelKey(ChannelType.CANCEL_HELD_ORDER, sessionContainer);
            ServicesHelper.getIntermarketOrderStatusConsumerHome().addFilter(channelKey);
        }
     }

    private void addNBBOAgentAdminInterest(String sessionName, int classKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Adding NBBOAgentAdmin and Satisfaction Alert class filter, session:" + sessionName + " classKey:" + classKey);
        }
        ChannelKey channelKey;

        if (nbboAgentListenersBySessionClass.get(new SessionKeyContainer(sessionName,classKey)) == null)
        {
            UserSessionClassContainer container = new UserSessionClassContainer(userId,sessionName, classKey);
            channelKey = new ChannelKey(ChannelType.NBBO_AGENT_FORCED_OUT, container);
            ServicesHelper.getNBBOAgentAdminConsumerHome().addFilter(channelKey);

            channelKey = new ChannelKey(ChannelType.NBBO_AGENT_REMINDER, container);
            ServicesHelper.getNBBOAgentAdminConsumerHome().addFilter(channelKey);

            channelKey = new ChannelKey(ChannelType.ALERT_SATISFACTION, container);
            ServicesHelper.getAlertConsumerHome().addFilter(channelKey);

            channelKey = new ChannelKey(ChannelType.INTERMARKET_ADMIN_MESSAGE, container);
            ServicesHelper.getIntermarketAdminMessageConsumerHome().addFilter(channelKey);

            channelKey = new ChannelKey(ChannelType.INTERMARKET_ADMIN_MESSAGE_BROADCAST, INT_0);
            ServicesHelper.getIntermarketAdminMessageConsumerHome().addFilter(channelKey);

        }
     }

    private void removeHeldOrderClassInterest(String sessionName, int classKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Removing held order status class filter session:" + sessionName + " classKey:" + classKey);
        }
        ChannelKey channelKey;
        SessionKeyContainer sessionContainer = new SessionKeyContainer(sessionName, classKey);

        channelKey = new ChannelKey(ChannelType.HELD_ORDER_CANCEL_REPORT, sessionContainer);
        ServicesHelper.getIntermarketOrderStatusConsumerHome().removeFilter(channelKey);

        channelKey = new ChannelKey(ChannelType.HELD_ORDER_FILLED_REPORT, sessionContainer);
        ServicesHelper.getIntermarketOrderStatusConsumerHome().removeFilter(channelKey);

        channelKey = new ChannelKey(ChannelType.HELD_ORDER_STATUS, sessionContainer);
        ServicesHelper.getIntermarketOrderStatusConsumerHome().removeFilter(channelKey);

        channelKey = new ChannelKey(ChannelType.HELD_ORDERS, sessionContainer);
        ServicesHelper.getIntermarketOrderStatusConsumerHome().removeFilter(channelKey);

        channelKey = new ChannelKey(ChannelType.NEW_HELD_ORDER, sessionContainer);
        ServicesHelper.getIntermarketOrderStatusConsumerHome().removeFilter(channelKey);

        channelKey = new ChannelKey(ChannelType.CANCEL_HELD_ORDER, sessionContainer);
        ServicesHelper.getIntermarketOrderStatusConsumerHome().removeFilter(channelKey);

     }

    private void removeNBBOAgentAdminInterest(String sessionName, int classKey)
        throws SystemException, CommunicationException, AuthorizationException, DataValidationException
    {
        if (Log.isDebugOn())
        {
            Log.debug(this, "Removing NBBOAgentAdmin class filter session:" + sessionName + " classKey:" + classKey);
        }
        ChannelKey channelKey;

        UserSessionClassContainer container = new UserSessionClassContainer(userId, sessionName, classKey);
        channelKey = new ChannelKey(ChannelType.NBBO_AGENT_FORCED_OUT, container);
        ServicesHelper.getNBBOAgentAdminConsumerHome().removeFilter(channelKey);

        channelKey = new ChannelKey(ChannelType.NBBO_AGENT_REMINDER, container);
        ServicesHelper.getNBBOAgentAdminConsumerHome().removeFilter(channelKey);

        channelKey = new ChannelKey(ChannelType.ALERT_SATISFACTION, container);
        ServicesHelper.getAlertConsumerHome().removeFilter(channelKey);

        channelKey = new ChannelKey(ChannelType.INTERMARKET_ADMIN_MESSAGE, container);
        ServicesHelper.getIntermarketAdminMessageConsumerHome().removeFilter(channelKey);

        channelKey = new ChannelKey(ChannelType.INTERMARKET_ADMIN_MESSAGE_BROADCAST, INT_0);
        ServicesHelper.getIntermarketAdminMessageConsumerHome().removeFilter(channelKey);

     }

     /*
      * Helper method to build a CAS heldorder detail struct from the CBOE heldorder struct.
      * @param order HeldOrderStruct
      * @return HeldOrderDetailStruct
      */
    private HeldOrderDetailStruct buildHeldOrderDetailStruct( HeldOrderStruct heldOrder, short statusUpdateReason)
    {
        ProductNameStruct productName = null;
        try {
            //PQRefactor
            //productName = ProductQueryManagerImpl.getProduct(heldOrder.order.productKey).productName;
            productName = getProductQueryServiceAdapter().getProductByKey(heldOrder.order.productKey).productName;
        }
        catch (org.omg.CORBA.UserException e) {
            Log.exception(this, "Can't find product Name", e);
            return null;
        }

        return new HeldOrderDetailStruct( productName, statusUpdateReason, heldOrder);
    }

    private OrderDetailStruct buildOrderDetailStruct( OrderStruct order, short statusUpdateReason)
    {
        ProductNameStruct productName = null;
        try {
            //PQRefactor
            //productName = ProductQueryManagerImpl.getProduct(order.productKey).productName;
            productName = getProductQueryServiceAdapter().getProductByKey(order.productKey).productName;
        }
        catch (org.omg.CORBA.UserException e) {
            Log.exception(this, "Can't find product Name", e);
            return null;
        }

        return new OrderDetailStruct( productName, statusUpdateReason, order);
    }

    private HeldOrderDetailStruct[] buildHeldOrderDetailStruct( HeldOrderStruct[] heldOrders, short statusUpdateReason)
    {
        HeldOrderDetailStruct[] heldOrderDetails = new HeldOrderDetailStruct[ heldOrders.length ];
        for ( int i = 0; i < heldOrderDetails.length; i++ )
        {
            heldOrderDetails[ i ] = buildHeldOrderDetailStruct( heldOrders[ i ], statusUpdateReason );
        }

        return heldOrderDetails;
    }

    protected ProductQueryServiceAdapter getProductQueryServiceAdapter()
    {
        if(pqAdapter == null)
        {
            pqAdapter = ServicesHelper.getProductQueryServiceAdapter();
        }
        return pqAdapter;
    }
}
